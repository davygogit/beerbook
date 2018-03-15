package com.davymoreau.android.beerbook.firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.davymoreau.android.beerbook.beersData.BeersData;
import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;
import com.davymoreau.android.beerbook.database.DataUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

//import static com.davymoreau.android.beerbook.constApp.DIR;
import static com.davymoreau.android.beerbook.constApp.FB_PHOTO_PATH;

/**
 * Created by davy on 19/11/2017.
 */

public class SyncUtil {
    public static final ArrayList FirebaseToArray() {
        return null;
    }

    public static final void firebasesync(Context context, String uid, final File dir) {
        FirebaseDatabase firebaseDatabase;
        DatabaseReference beersDatabaseReference;
        firebaseDatabase = FirebaseDatabase.getInstance();
        beersDatabaseReference = firebaseDatabase.getReference().child("beers");

        SQLiteDatabase db;
        BeerTastingHelper helper = new BeerTastingHelper(context);
        db = helper.getWritableDatabase();

        // delete DB&FB
        deleteFBandDB(db, beersDatabaseReference, dir);
        //add DB -> FB
        addDatabaseToFirebase(db, beersDatabaseReference, uid, dir);
        // del FB -> DB
        deleteDBFromFB(db, beersDatabaseReference, dir);
        // add DB->FB
        addFirebaseToDB(db, beersDatabaseReference, uid);
    }

    private static void addFirebaseToDB(final SQLiteDatabase sqLiteDatabase, DatabaseReference databaseReference, String uid){
        databaseReference.orderByChild("userID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String key = postSnapshot.getKey();
                    BeerFB beer = postSnapshot.getValue(BeerFB.class);
                    ContentValues cv = beer.retrieveContentValue();
                    String selection = BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID + " = ?";
                    Cursor cursor = sqLiteDatabase.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, selection, new String[]{key}, null, null, null);
                    if (cursor.getCount() == 0) {
                        // ajouter biere dans bd
                        BeersData.addBeerFromFB(sqLiteDatabase, cv, key);
                         boolean NewBeer = true;
                    }
                    cursor.close();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private static void deleteDBFromFB(final SQLiteDatabase db, DatabaseReference databaseReference, final File dir) {

        String selection = BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID + "<> ''";
        Cursor cursor = db.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, selection, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String key = cursor.getString(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID));
            final String id = cursor.getString(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry._ID));
            final String[] args = new String[]{id.toString()};
            DatabaseReference databaseReferenceToDelete = databaseReference.child(key);
            databaseReferenceToDelete.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        int nb = db.delete(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, BeerTastingContract.BeerTastingEntry._ID + " = " + id, null);
                        Log.d("", "onDataChange: " + nb);
                        // effacer img
                        File file = new File(dir, id + ".jpg");
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private static final void deleteFBandDB(SQLiteDatabase db, DatabaseReference databaseReference, final  File dir) {
        String selection = BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL + "= 'TRUE'";
        Cursor cursor = db.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, selection, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String key = cursor.getString(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID));
            String id = cursor.getString(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry._ID));
            // delete FB
            DatabaseReference databaseReferenceToDelete = databaseReference.child(key);
            databaseReferenceToDelete.removeValue();
            // effacer photo FB
            String photoURL = FB_PHOTO_PATH + key + ".jpg";

            FirebaseStorage firebaseStorage;
            StorageReference storageReference;
            firebaseStorage = FirebaseStorage.getInstance();
            try {
                storageReference = firebaseStorage.getReferenceFromUrl(photoURL);
            } catch (Exception e) {
                storageReference = null;
            }

            if (storageReference != null) {
                storageReference.delete();
            }

            // delete DB
            db.delete(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, "_id =" + id, null);
            // effacer photo DB
            File file = new File(dir, id + ".jpg");
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private static void addDatabaseToFirebase(SQLiteDatabase sqLiteDatabase, DatabaseReference databaseReference, String uid, File dir) {
        Cursor cursor = DataUtil.getAllNewBeers(sqLiteDatabase);
        ArrayList<ContentValues> list = DataUtil.CursorToArray(cursor, dir);
        cursor.close();
        ContentValues cv;
        DatabaseReference newDatabaseReference;
        for (int i = 0; i < list.size(); i++) {
            cv = list.get(i);
            String id = cv.getAsString(BeerTastingContract.BeerTastingEntry._ID);
            BeerFB beer = new BeerFB(cv, uid);
            newDatabaseReference = databaseReference.push();
            newDatabaseReference.setValue(beer);
            String key = newDatabaseReference.getKey();
            // add pic
            File pic = new  File(dir, id + ".jpg" );
            if (pic.exists()){
                FirebaseStorage firebaseStorage;
                StorageReference storageReference;
                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference().child("photos");

                StorageReference photoRef = storageReference.child(key+".jpg");
                photoRef.putFile(Uri.fromFile(pic));
            }

            // update database with key
            String strFilter = BeerTastingContract.BeerTastingEntry._ID + "=" + id;
            ContentValues args = new ContentValues();
            args.put(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID, key);
            sqLiteDatabase.update(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, args, strFilter, null);
        }
    }

 public Uri GetUri(String key){
        return null;
    }
}
