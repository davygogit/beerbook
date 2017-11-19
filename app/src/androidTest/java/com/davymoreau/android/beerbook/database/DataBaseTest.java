package com.davymoreau.android.beerbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Created by davy on 12/08/2017.
 */

@RunWith(AndroidJUnit4.class)
public class DataBaseTest {

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final Class mDbHelperClass = BeerTastingHelper.class;

    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    @Test
    public void create_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME + "'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        assertEquals("Error: Your database was created without the expected tables.",
                BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, tableNameCursor.getString(0));

        /* Always close a cursor when you are done with it */
        tableNameCursor.close();

    }
    @Test
    public void insertSingleRecord_test() throws Exception {
        /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        /* Use WaitlistDbHelper to get access to a writable database */
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();

        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME, "beer");
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_PICTURE, "path");
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY, "brewery");
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_DATE, 0);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, 0.5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_NOTE, "cette bière est bonne");
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE, 4.7);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_COLOR, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_FOAM, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE, "verre");

        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_ACID, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_BITTER, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_SWEET, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_HERB, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_SPICE, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_BODY, 5);
        testValues.put(BeerTastingContract.BeerTastingEntry.COLUMN_LINGER, 5);

        /* Insert ContentValues into database and get first row ID back */
        long firstRowId = database.insert(
                BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME,
                null,
                testValues);

        /* If the insert fails, database.insert returns -1 */
        assertNotEquals("Unable to insert into the database", -1, firstRowId);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                /* Name of table on which to perform the query */
                BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from waitlist query";
        assertTrue(emptyQueryError,
                wCursor.moveToFirst());

        /* Close cursor and database */
        wCursor.close();
        dbHelper.close();
    }

    void deleteTheDatabase(){
        try {
            /* Use reflection to get the database name from the db helper class */
            Field f = mDbHelperClass.getDeclaredField("DB_NAME");
            f.setAccessible(true);
            mContext.deleteDatabase((String)f.get(null));
        }catch (NoSuchFieldException ex){
            fail("Make sure you have a member called DB_NAME ");
        }catch (Exception ex){
            fail(ex.getMessage());
        }

    }



}
