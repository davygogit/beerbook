package com.davymoreau.android.beerbook.util;

import android.content.ContentValues;
import android.database.Cursor;

import com.davymoreau.android.beerbook.constApp;
import com.davymoreau.android.beerbook.database.BeerTastingContract;

import java.util.ArrayList;

/**
 * Created by davy on 13/11/2017.
 */

public class DataUtil {

    public static final ArrayList CursorToArray(Cursor cursor) {
        ArrayList<ContentValues> list = new ArrayList<>();
        if (cursor == null) return list;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContentValues cv = new ContentValues();
            // id
            String idEntry = BeerTastingContract.BeerTastingEntry._ID;
            long id = cursor.getLong(cursor.getColumnIndex(idEntry));
            cv.put(idEntry, id);
            // path picture
            cv.put("picpath", constApp.DIR.getPath() + "/" + String.valueOf(id)+ ".jpg");
            // name
            String nameEntry = BeerTastingContract.BeerTastingEntry.COLUMN_NAME;
            String name = cursor.getString(cursor.getColumnIndex(nameEntry));
            cv.put(nameEntry, name);
            // date
            String dateEntry = BeerTastingContract.BeerTastingEntry.COLUMN_DATE;
            String date = cursor.getString(cursor.getColumnIndex(dateEntry));
            cv.put(dateEntry, date);
            // brewery name
            String breweryEntry = BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY;
            String brewery = cursor.getString(cursor.getColumnIndex(breweryEntry));
            cv.put(breweryEntry, brewery);
            // degree alcohol
            String degreeEntry = BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE;
            float degree = cursor.getFloat(cursor.getColumnIndex(degreeEntry));
            cv.put(degreeEntry, degree);
            // rating
            String ratingEntry = BeerTastingContract.BeerTastingEntry.COLUMN_RATING;
            float rating = cursor.getFloat(cursor.getColumnIndex(ratingEntry));
            cv.put(ratingEntry, rating);
            // notes
            String notesEntry = BeerTastingContract.BeerTastingEntry.COLUMN_NOTE;
            String note = cursor.getString(cursor.getColumnIndex(notesEntry));
            cv.put(notesEntry, note);
            // color
            String colorEntry = BeerTastingContract.BeerTastingEntry.COLUMN_COLOR;
            Integer color = cursor.getInt(cursor.getColumnIndex(colorEntry));
            cv.put(colorEntry, color);
            // foam
            String foamEntry = BeerTastingContract.BeerTastingEntry.COLUMN_FOAM;
            Integer foam = cursor.getInt(cursor.getColumnIndex(foamEntry));
            cv.put(foamEntry, foam);
            // serving
            String servingEntry = BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE;
            Integer serving = cursor.getInt(cursor.getColumnIndex(servingEntry));
            cv.put(servingEntry, serving);
            // style
            String styleEntry = BeerTastingContract.BeerTastingEntry.COLUMN_STYLE;
            String style = cursor.getString(cursor.getColumnIndex(styleEntry));
            cv.put(styleEntry, style);
            // flavour
            String acidEntry = BeerTastingContract.BeerTastingEntry.COLUMN_ACID;
            float acid = cursor.getFloat(cursor.getColumnIndex(acidEntry));
            cv.put(acidEntry, acid);

            String bitterEntry = BeerTastingContract.BeerTastingEntry.COLUMN_BITTER;
            float bitter = cursor.getFloat(cursor.getColumnIndex(bitterEntry));
            cv.put(bitterEntry, bitter);

            String sweetEntry = BeerTastingContract.BeerTastingEntry.COLUMN_SWEET;
            float sweet = cursor.getFloat(cursor.getColumnIndex(sweetEntry));
            cv.put(sweetEntry, sweet);

            String cerealEntry = BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL;
            float cereal = cursor.getFloat(cursor.getColumnIndex(cerealEntry));
            cv.put(cerealEntry, cereal);

            String toffeeEntry = BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE;
            float toffee = cursor.getFloat(cursor.getColumnIndex(toffeeEntry));
            cv.put(toffeeEntry, toffee);

            String coffeeEntry = BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE;
            float coffee = cursor.getFloat(cursor.getColumnIndex(coffeeEntry));
            cv.put(coffeeEntry, coffee);

            String herbEntry = BeerTastingContract.BeerTastingEntry.COLUMN_HERB;
            float herb = cursor.getFloat(cursor.getColumnIndex(herbEntry));
            cv.put(herbEntry, herb);

            String fruitEntry = BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT;
            float fruit = cursor.getFloat(cursor.getColumnIndex(fruitEntry));
            cv.put(fruitEntry, fruit);

            String spiceEntry = BeerTastingContract.BeerTastingEntry.COLUMN_SPICE;
            float spice = cursor.getFloat(cursor.getColumnIndex(spiceEntry));
            cv.put(spiceEntry, spice);

            String alcoholEntry = BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL;
            float alcohol = cursor.getFloat(cursor.getColumnIndex(alcoholEntry));
            cv.put(alcoholEntry, alcohol);

            String bodyEntry = BeerTastingContract.BeerTastingEntry.COLUMN_BODY;
            float body = cursor.getFloat(cursor.getColumnIndex(bodyEntry));
            cv.put(bodyEntry, body);

            String lingerEntry = BeerTastingContract.BeerTastingEntry.COLUMN_LINGER;
            float linger = cursor.getFloat(cursor.getColumnIndex(lingerEntry));
            cv.put(lingerEntry, linger);

            list.add(cv);

            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }
}
