package com.davymoreau.android.beerbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
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

import com.davymoreau.android.beerbook.beersData.BeersData;
import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;
import com.davymoreau.android.beerbook.database.DataUtil;
import com.davymoreau.android.beerbook.firebase.BeerFB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeerListAdapter.BeerClickListener {

    //sqlite
    SQLiteDatabase mDb;
    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseReference;
    //private FirebaseAuth mFirebaseAuth;
    //private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ChildEventListener mChildEventListener;


    //BeerAdapter mAdapter;
    BeerListAdapter mAdapter;
    Context mContext;
    ArrayList mLocalList;
    ArrayList mCloudList;
    ArrayList mAdapterList;
    LinearLayoutManager mLayoutManager;
    boolean FBhasListener = false;

    int limitFB = 8;

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

    @Override
    protected void onStart() {
        super.onStart();
        updateBd();
    }

    private void updateBd() {
        mLocalList = getLocalBeerTasting();
        setAdapterList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapterList = new ArrayList();
        mSearch = "";

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mtvNoData = (TextView) findViewById(R.id.tv_nodata);

        mContext = MainActivity.this;

        mCloudList = new ArrayList();


        // Ajout nouvelle bière
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddBeerActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // cnx base de données
        BeerTastingHelper helper = new BeerTastingHelper(this);
        mDb = helper.getWritableDatabase();

        // implementation du recyclerview
        final RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_beers);
        mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setHasFixedSize(true);
        //mAdapter = new BeerAdapter(this);
        //recycler.setAdapter(mAdapter);

        mAdapter = new BeerListAdapter(this);
        recycler.setAdapter(mAdapter);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mLocal) return;
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    limitFB += 50;
                    mFirebaseReference.removeEventListener(mChildEventListener);
                    mCloudList.clear();
                    mAdapter.setBeers(mCloudList);
                    mFirebaseReference.limitToFirst(limitFB).addChildEventListener(mChildEventListener);
                }
            }

        });

        //recycler.setAdapter(mAdapter);

        // gestion du swipe pour suppression
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
                                BeersData.removeBeer(mDb, id);
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
        itemTouchHelper.attachToRecyclerView(recycler);

        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseDatabase.getReference().

                child("beers");

        mChildEventListener = new

                ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // récuperer CV
                        BeerFB beer = dataSnapshot.getValue(BeerFB.class);
                        ContentValues cv = beer.retrieveContentValue();
                        String key = dataSnapshot.getKey();
                        // tester ni à ajouter
                        String selection = BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID + " = ?";
                        Cursor cursor = mDb.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, selection, new String[]{key}, null, null, null);
                        if (cursor.getCount() == 0) {
                            // ajouter biere dans bd
                            BeersData.addBeerWithKey(mDb, cv, "davy", key);
                        }
                        cursor.close();

                        // ajouter dans mCloudList
                        mCloudList.add(cv);
                        // ajouter dans adapter
                        if (!mLocal) {
                            mAdapter.addBeer(cv);
                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }

        ;

        mFirebaseReference.limitToFirst(limitFB).

                addChildEventListener(mChildEventListener);

        FBhasListener = true;
    }


    private ArrayList<ContentValues> getLocalBeerTasting() {
        Cursor cursor = DataUtil.getAllBeersFordisplay(mDb);
        ArrayList arrayList = DataUtil.CursorToArray(cursor);
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
                } else {
                    mLocal = true;
                    cloudItem.setIcon(getResources().getDrawable(R.drawable.ic_action_home));
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

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


}
