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

import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;
import com.davymoreau.android.beerbook.util.DataUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeerListAdapter.BeerClickListener {

    SQLiteDatabase mDb;
    //BeerAdapter mAdapter;
    BeerListAdapter mAdapter;
    Context mContext;
    ArrayList mLocalList;
    ArrayList mCloudList;
    ArrayList mAdapterList;

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
        // fake data pour test
        // TestUtil.insertFakeData(mDb);

        // implementation du recyclerview
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_beers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        //mAdapter = new BeerAdapter(this);
        //recycler.setAdapter(mAdapter);

        mAdapter = new BeerListAdapter(this);
        recycler.setAdapter(mAdapter);

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
                                removeBeer(id);
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
    }

    // récupére la liste de toutes les bières
    // à faire, limiter les colonnes à celles nescessaires
    // ne pas afficher les bières à supprimer
    private Cursor getBeerTastings() {
        return mDb.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, null, null, null, null, null);
    }

    private ArrayList<ContentValues> getLocalBeerTasting() {
        Cursor cursor = getBeerTastings();
        ArrayList arrayList = DataUtil.CursorToArray(cursor);
        cursor.close();
        return arrayList;
    }

    private void updateLocal(){
        mLocalList = getLocalBeerTasting();
    }

    // mettre la valeur toDelete à true
    private boolean removeBeer(long id) {
        return mDb.delete(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, BeerTastingContract.BeerTastingEntry._ID + " = " + id, null) > 0;
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
            tempList = mLocalList;
        }

        mAdapterList.clear();

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

        if (mSort == 0 ){
            Collections.sort(mAdapterList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String date1 = ((ContentValues) o1).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
                    String date2 = ((ContentValues) o2).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
                    return date1.compareTo(date2);
                }
            });

        } else if (mSort == 1){
            Collections.sort(mAdapterList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String name1 = ((ContentValues) o1).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                    String name2 = ((ContentValues) o2).getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
                    return name1.compareTo(name2);
                }
            });
        }else {
            Collections.sort(mAdapterList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Float rating1 = ((ContentValues) o1).getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
                    Float rating2 = ((ContentValues) o2).getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
                    return rating2.compareTo(rating1);
                }
            });
        }


            mAdapter.setBeers(mAdapterList);
        mProgressBar.setVisibility(View.GONE);
    }


}
