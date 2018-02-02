package com.davymoreau.android.beerbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davymoreau.android.beerbook.beersData.BeersData;
import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;
import com.davymoreau.android.beerbook.database.DataUtil;
import com.davymoreau.android.beerbook.firebase.BeerFB;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static com.davymoreau.android.beerbook.constApp.FB_PHOTO_PATH;
import static com.davymoreau.android.beerbook.constApp.PIC_PATH;
import static com.davymoreau.android.beerbook.constApp.PIC_URI;
import static com.davymoreau.android.beerbook.firebase.SyncUtil.firebasesync;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeerListAdapter.BeerClickListener {

    File dir;

    private static final int RC_SIGN_IN = 123;

    //sqlite
    SQLiteDatabase mDb;
    //firebase
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ValueEventListener mValueEventListener;
    String mUserID;


    BeerListAdapter mAdapter;
    Context mContext;
    ArrayList mLocalList;
    ArrayList mCloudList;
    ArrayList mAdapterList;
    LinearLayoutManager mLayoutManager;

    ProgressBar mProgressBar;
    TextView mtvNoData;
    String mSearch;

    // 0 - date
    // 1 - alpha
    // 2 - ratting
    int mSort = 0;

    MenuItem sortItem;
    MenuItem cloudItem;

    boolean mLocal = true;

    RecyclerView mRecycler;
    ItemTouchHelper mItemTouchHelper;

    NavigationView mNavigationView;

    boolean UserChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        mUserID = "";

        mAdapterList = new ArrayList();
        mSearch = "";

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mtvNoData = (TextView) findViewById(R.id.tv_nodata);

        mContext = MainActivity.this;

        mCloudList = new ArrayList();

        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseDatabase.getReference().child("beers");
        mFirebaseStorage = FirebaseStorage.getInstance();

        // Auth
        mFirebaseAuth = FirebaseAuth.getInstance();


        // Ajout nouvelle bière
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddBeerActivity.class);
                intent.putExtra("auth", mUserID);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // gestion vue navigation
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);


        // cnx base de données
        BeerTastingHelper helper = new BeerTastingHelper(this);
        mDb = helper.getWritableDatabase();

        // implementation du recyclerview
        mRecycler = (RecyclerView) findViewById(R.id.recycler_beers);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);

        mAdapter = new BeerListAdapter(this);
        mRecycler.setAdapter(mAdapter);


        // gestion du swipe pour suppression
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /*| ItemTouchHelper.RIGHT*/) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final long id = (long) viewHolder.itemView.getTag();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                builder.setTitle("Effacer une dégustation")
                        .setMessage("Etes-vous sûr de vouloir supprimer cette dégustation ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BeersData.removeBeer(mDb, id, dir);
                                updateLocal();
                                setAdapterList();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updateBd();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecycler);


        mValueEventListener = new ValueEventListener() {
            boolean mNewBeer = false;


            /*query.addListenerForSingleValueEvent(new ValueEventListener() {
    boolean processDone = false;

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
                            final long nbChild = ++ ChildCount ;
                    BeerFB beer = postSnapshot.getValue(BeerFB.class);
                    ContentValues cv = beer.retrieveContentValue();
                    final String key = postSnapshot.getKey();
                    String userID = beer.getUserID();
                    // tester si photo existe
                    //FBUtil fbUtil = new FBUtil();
                    //String path ="";

                    //Uri pathUri = fbUtil.GetPhotoUri( key);

                    String picPath = FB_PHOTO_PATH + key + ".jpg";
                    cv.put("picpath", picPath);

                    mStorageReference = mFirebaseStorage.getReferenceFromUrl(picPath);
                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            BeerFB beer = postSnapshot.getValue(BeerFB.class);
                            ContentValues cv = beer.retrieveContentValue();
                            String userID = beer.getUserID();
                            cv.put("picUri", uri.getPath());

                            // ajouter dans mCloudList
                            mCloudList.add(cv);
                            // ajouter dans adapter
                            if (!mLocal) {
                                mAdapter.addBeer(cv);
                            }

                            if (nbChild == nbChildren){
                                if (!mLocal) {
                                    mAdapter.setBeers(mCloudList);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            BeerFB beer = postSnapshot.getValue(BeerFB.class);
                            ContentValues cv = beer.retrieveContentValue();
                            // ajouter dans mCloudList
                            mCloudList.add(cv);
                            // ajouter dans adapter
                            if (!mLocal) {
                                mAdapter.addBeer(cv);
                            }

                            if (nbChild == nbChildren){
                                if (!mLocal) {
                                    mAdapter.setBeers(mCloudList);
                                }
                            }
                        }
                    });
        } else {
                mFirebaseReference.removeEventListener(mValueEventListener);
        }
    }

    @Override public void onCancelled(DatabaseError databaseError) {}
});*/
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                final long nbChildren = dataSnapshot.getChildrenCount();
                long ChildCount = 0;
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final long nbChild = ++ ChildCount ;
                    BeerFB beer = postSnapshot.getValue(BeerFB.class);
                    ContentValues cv = beer.retrieveContentValue();
                    final String key = postSnapshot.getKey();

                    String picPath = FB_PHOTO_PATH + key + ".jpg";
                    cv.put(PIC_PATH, picPath);

                    mStorageReference = mFirebaseStorage.getReferenceFromUrl(picPath);
                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            BeerFB beer = postSnapshot.getValue(BeerFB.class);
                            ContentValues cv = beer.retrieveContentValue();
                            String userID = beer.getUserID();
                            String fullUri = uri.toString();
                            cv.put(PIC_URI, fullUri);

                            // ajouter dans mCloudList
                            mCloudList.add(cv);
                            // ajouter dans adapter
                            if (!mLocal) {
                                mAdapter.addBeer(cv);
                            }

                            if (nbChild == nbChildren){
                                if (!mLocal) {
                                    mAdapter.setBeers(mCloudList);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            BeerFB beer = postSnapshot.getValue(BeerFB.class);
                            ContentValues cv = beer.retrieveContentValue();
                            // ajouter dans mCloudList
                            mCloudList.add(cv);
                            // ajouter dans adapter
                            if (!mLocal) {
                                mAdapter.addBeer(cv);
                            }

                            if (nbChild == nbChildren){
                                if (!mLocal) {
                                    mAdapter.setBeers(mCloudList);
                                }
                            }
                        }
                    });
                }

            // updateBd();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //mFirebaseReference.addListenerForSingleValueEvent(mValueEventListener);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                if(UserChange){
                    finish();
                    startActivity(getIntent());
                }
                FirebaseUser user = firebaseAuth.getCurrentUser();
               // mFirebaseReference.removeEventListener(mValueEventListener);
                mCloudList.clear();
                if (user != null) {
                    mNavigationView.getMenu().findItem(R.id.nav_login).setEnabled(false);
                    mNavigationView.getMenu().findItem(R.id.nav_logout).setEnabled(true);
                    mNavigationView.getMenu().findItem(R.id.nav_delete_account).setEnabled(true);
                    mFirebaseAuth = firebaseAuth;
                    mUserID = user.getUid();
                    // sync
                    firebasesync(mContext, mUserID, dir);


                } else {
                    mNavigationView.getMenu().findItem(R.id.nav_login).setEnabled(true);
                    mNavigationView.getMenu().findItem(R.id.nav_logout).setEnabled(false);
                    mNavigationView.getMenu().findItem(R.id.nav_delete_account).setEnabled(false);
                    mFirebaseAuth = null;
                    mUserID = "";

                }
                UserChange = true;
                mFirebaseReference.addListenerForSingleValueEvent(mValueEventListener);
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        updateBd();
    }

    private ArrayList<ContentValues> getLocalBeerTasting() {
        Cursor cursor = DataUtil.getAllBeersFordisplay(mDb);
        ArrayList arrayList = DataUtil.CursorToArray(cursor, dir);
        cursor.close();
        return arrayList;
    }

    private void updateLocal() {
        mLocalList = getLocalBeerTasting();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        sortItem = menu.findItem(R.id.action_sort);
        cloudItem = menu.findItem(R.id.action_cloud);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //votre code ici
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSearch = s;
                setAdapterList();
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_cloud:
                if (mLocal) {
                    mLocal = false;
                    cloudItem.setIcon(getResources().getDrawable(R.drawable.ic_action_cloud));
                    mItemTouchHelper.attachToRecyclerView(null);

                } else {
                    mLocal = true;
                    cloudItem.setIcon(getResources().getDrawable(R.drawable.ic_action_home));
                    mItemTouchHelper.attachToRecyclerView(mRecycler);
                }
                setAdapterList();
                return true;

            case R.id.action_sort:
                if (mSort == 0) {
                    mSort = 1;
                    sortItem.setIcon(getResources().getDrawable(R.drawable.ic_action_alpha));

                } else if (mSort == 1) {
                    mSort = 2;
                    sortItem.setIcon(getResources().getDrawable(R.drawable.ic_action_star));

                } else if (mSort == 2) {
                    mSort = 0;
                    sortItem.setIcon(getResources().getDrawable(R.drawable.ic_action_date));

                }
                setAdapterList();
                return true;
        }


        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Toast.makeText(mContext, "Vous êtes matenant deconnecté", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else if (id == R.id.nav_delete_account) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onClick(int ClickedItemIndex) {
        //Toast.makeText(this, "affiche detail bière num :"+ClickedItemIndex, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, BeerDetailActivity.class);
        //Cursor cursor = mAdapter.getItem(ClickedItemIndex);
        ContentValues cv = mAdapter.getItem(ClickedItemIndex);
        // path img
        String path ="";
        if(mLocal){
            long id = cv.getAsInteger(BeerTastingContract.BeerTastingEntry._ID);
            path = dir + "/" + String.valueOf(id) + ".jpg";
        }else{

        }

        cv.put("path", path);

        intent.putExtra("cv", cv);
        //int id = cursor.getInt(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry._ID));
        //int id = cv.getAsInteger(BeerTastingContract.BeerTastingEntry._ID);
        //intent.putExtra("id", id);
        startActivity(intent);
    }

    private void setAdapterList() {
        mProgressBar.setVisibility(View.VISIBLE);
        ArrayList<ContentValues> tempList = null;

        if (mLocal) {
            tempList = mLocalList;
        } else {
            tempList = mCloudList;
        }

        mAdapterList.clear();

        if (tempList != null) {
            for (int i = 0; i < tempList.size(); i++) {
                String beer;
                String brewery;

                ContentValues cv = tempList.get(i);
                beer = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                brewery = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY);

                if (beer.contains(mSearch) || brewery.contains(mSearch)) {
                    mAdapterList.add(cv);
                }
            }


            Collections.sort(mAdapterList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Float rating1 = ((ContentValues) o1).getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
                    Float rating2 = ((ContentValues) o2).getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
                    return rating2.compareTo(rating1);
                }
            });

            Collections.sort(mAdapterList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String name1 = ((ContentValues) o1).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                    String name2 = ((ContentValues) o2).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                    return name1.compareTo(name2);
                }
            });

            Collections.sort(mAdapterList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String date1 = ((ContentValues) o1).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
                    String date2 = ((ContentValues) o2).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
                    return date1.compareTo(date2);
                }
            });

            if (mSort == 0) {
                Collections.sort(mAdapterList, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String date1 = ((ContentValues) o1).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
                        String date2 = ((ContentValues) o2).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
                        return date1.compareTo(date2);
                    }
                });

            } else if (mSort == 1) {
                Collections.sort(mAdapterList, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String name1 = ((ContentValues) o1).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                        String name2 = ((ContentValues) o2).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                        return name1.compareTo(name2);
                    }
                });
            } else {
                Collections.sort(mAdapterList, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Float rating1 = ((ContentValues) o1).getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
                        Float rating2 = ((ContentValues) o2).getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
                        return rating2.compareTo(rating1);
                    }
                });
            }
        }

        mAdapter.setBeers(mAdapterList);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirebaseAuth != null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFirebaseAuth != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void updateBd() {
        mLocalList = getLocalBeerTasting();
        setAdapterList();
    }
}
