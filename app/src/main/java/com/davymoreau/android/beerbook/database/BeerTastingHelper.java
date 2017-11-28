package com.davymoreau.android.beerbook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by davy on 11/08/2017.
 */

public class BeerTastingHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "beerTasting.db";
    public static final int  DB_VERSION = 1;

    public BeerTastingHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

         final String SQL_CREATE_BEER_TABLE = "CREATE TABLE " + BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME + " (" +
                BeerTastingContract.BeerTastingEntry._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_FBASE_ID + " TEXT, " + // ID FireBase, pour syncro
                BeerTastingContract.BeerTastingEntry.COLUMN_WAIT_DEL + " TEXT, " + // attente de suppression
                BeerTastingContract.BeerTastingEntry.COLUMN_NAME     + " TEXT, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY  + " TEXT, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_STYLE    + " TEXT, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_DATE     + " TEXT, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_RATING   + " REAL, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_NOTES    + " TEXT, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE   + " REAL, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_COLOR    + " INTEGER, " + // id de la couleur
                BeerTastingContract.BeerTastingEntry.COLUMN_FOAM     + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE  + " INTEGER, " +
                // champs suivants  : roue des saveurs
                BeerTastingContract.BeerTastingEntry.COLUMN_ACID     + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_BITTER   + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_SWEET    + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL   + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE   + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE   + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_HERB     + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT    + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_SPICE    + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL  + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_BODY     + " INTEGER, " +
                BeerTastingContract.BeerTastingEntry.COLUMN_LINGER   + " INTEGER " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_BEER_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME);
        onCreate(sqLiteDatabase);
    }
}
