package com.davymoreau.android.beerbook.beersData;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.firebase.BeerFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

//import static com.davymoreau.android.beerbook.constApp.DIR;

/**
 * Created by davy on 21/11/2017.
 */

public class BeersData {

    public static int addBeer(SQLiteDatabase db, ContentValues cv, String auth, File dir){
        return addBeerWithKey(db, cv, auth, dir);

    }
    public static int addBeerWithKey(SQLiteDatabase db, ContentValues cv, String auth, final File dir){
        String key = "";
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

        // Ajout photo local
        File photoCrop = new File(dir, "crop.jpg");
        File dest =  new File(dir, String.valueOf(id) + ".jpg");
        if (!photoCrop.renameTo(dest)){
            //Toast.makeText(get, "merde lors du renomage", Toast.LENGTH_SHORT).show();
        }
        // Ajout photo firebase
        if(!auth.equals("")){
            FirebaseStorage firebaseStorage;
            StorageReference storageReference;
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child("photos");

            StorageReference photoRef = storageReference.child(key+".jpg");
            photoRef.putFile(Uri.fromFile(dest));
        }


        return id;
    }

    public static int addBeerFromFB(SQLiteDatabase db, ContentValues cv, String key){
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID, key);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL, "FALSE");
        int id =(int) (db.insert(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, cv));

        return id;

    }


    public static void removeBeer(SQLiteDatabase db, long id, File dir ){
        // updater todelete
        String strFilter = BeerTastingContract.BeerTastingEntry._ID+"=" + id;
        ContentValues args = new ContentValues();
        args.put(BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL, "TRUE");
        db.update(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, args, strFilter, null);
        // suppression image
        File file = new File(dir , id + ".jpg");
        if(file.exists()){
            file.delete();
        }
    }
}
