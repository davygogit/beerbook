package com.davymoreau.android.beerbook.beersData;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.firebase.BeerFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import static com.davymoreau.android.beerbook.constApp.DIR;

/**
 * Created by davy on 21/11/2017.
 */

public class BeersData {

    public static int addBeer(SQLiteDatabase db, ContentValues cv, String auth){
        return addBeerWithKey(db, cv, auth, "");

    }
    public static int addBeerWithKey(SQLiteDatabase db, ContentValues cv, String auth, String key){
        //inserer dans firebase
        if (!auth.equals("")) {
            FirebaseDatabase firebaseDatabase;
            DatabaseReference databaseReference;
            DatabaseReference newDatabaseReference;

            BeerFB beer = new BeerFB(cv, auth);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("beers");
            newDatabaseReference = databaseReference.push();
            newDatabaseReference.setValue(beer);
            key = newDatabaseReference.getKey();
        }
        // Ajouter en base de données
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID, key);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL, "FALSE");
        int id =(int) (db.insert(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, cv));


        return id;
    }

    public static void removeBeer(SQLiteDatabase db, long id ){
        // updater todelete
        String strFilter = BeerTastingContract.BeerTastingEntry._ID+"=" + id;
        ContentValues args = new ContentValues();
        args.put(BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL, "TRUE");
        db.update(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, args, strFilter, null);
        // suppression image
        File file = new File(DIR , id + ".jpg");
        if(file.exists()){
            file.delete();
        }
    }
}
