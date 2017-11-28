package com.davymoreau.android.beerbook.firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;
import com.davymoreau.android.beerbook.database.DataUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by davy on 19/11/2017.
 */

public class FirebaseUtil {
    public static final ArrayList FirebaseToArray(){
        return null;
    }

    public static final void firebasesync(Context context){
        FirebaseDatabase firebaseDatabase;
        DatabaseReference beersDatabaseReference;
        firebaseDatabase = FirebaseDatabase.getInstance();
        beersDatabaseReference = firebaseDatabase.getReference().child("beers");

        SQLiteDatabase db;
        BeerTastingHelper helper = new BeerTastingHelper(context);
        db = helper.getWritableDatabase();

        // delete DB&FB
        deleteFBandDB(db, beersDatabaseReference);
        //add DB -> FB
        AddDatabaseToFirebase(db, beersDatabaseReference);
        // add FB -> DB
        // use addListenerForSingleValueEvent



    }

    private static final void deleteFBandDB(SQLiteDatabase db, DatabaseReference databaseReference){
        String selection = BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL + "= 'TRUE'";
        Cursor cursor = db.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME,null, selection, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String key = cursor.getString(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID));
            String id = cursor.getString(cursor.getColumnIndex(BeerTastingContract.BeerTastingEntry._ID));
            // delete FB
            DatabaseReference databaseReferenceToDelete = databaseReference.child(key);
            databaseReferenceToDelete.removeValue();
            // delete DB
            db.delete(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, "_id =" + id, null);

        }
    }

    private static void AddDatabaseToFirebase(SQLiteDatabase sqLiteDatabase, DatabaseReference databaseReference){
        Cursor cursor = DataUtil.getAllNewBeers(sqLiteDatabase);
        ArrayList<ContentValues> list = DataUtil.CursorToArray(cursor);
        cursor.close();
        ContentValues cv;
        DatabaseReference newDatabaseReference;
        for (int i = 0; i < list.size(); i++) {
            cv = list.get(i);
            BeerFB beer = new BeerFB(cv, "davy");
            newDatabaseReference = databaseReference.push();
            newDatabaseReference.setValue(beer);
            String key = newDatabaseReference.getKey();
            // update database with key
            String strFilter = BeerTastingContract.BeerTastingEntry._ID+"=" + cv.getAsString(BeerTastingContract.BeerTastingEntry._ID);
            ContentValues args = new ContentValues();
            args.put(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID, key);
            sqLiteDatabase.update(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, args, strFilter, null);
        }


    }
}
