package com.davymoreau.android.beerbook.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davy on 12/08/2017.
 */

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if (db == null){
            return;
        }

        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 5);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY, "Pélican");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME, "Bock");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 5);
        list.add(cv);

        cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 5.5);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,"Lancelot");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,"Bonnet rouge");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 4);
        list.add(cv);

        cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 6);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,"Bourbon");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,"Bourbon");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 7);
        list.add(cv);

        cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 12.5);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,"Chimay");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,"Chimay rouge");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 2.6);
        list.add(cv);

        cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 10);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,"Achouffe");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,"Chouffe");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 4.5);
        list.add(cv);

        cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 8.4);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,"Grimbergen");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,"Grimbergen");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 3.3);
        list.add(cv);

        cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 5.4);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,"Kekette");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,"Kekette blonde");
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 2.1);
        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }
    }
}
