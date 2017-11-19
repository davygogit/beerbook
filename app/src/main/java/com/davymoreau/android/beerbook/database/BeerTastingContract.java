package com.davymoreau.android.beerbook.database;

import android.provider.BaseColumns;

/**
 * Created by davy on 11/08/2017.
 */

public class BeerTastingContract {
    public static final class BeerTastingEntry implements BaseColumns {

        public static final String TABLE_BEER_NAME = "beer";

        public static final String COLUMN_FBASE_ID  = "firebaseId";
        public static final String COLUMN_WAIT_DEL  = "waitForDeletion";

        public static final String COLUMN_NAME      = "name";
        public static final String COLUMN_PICTURE   = "picture";
        public static final String COLUMN_BREWERY   = "brewery";
        public static final String COLUMN_DATE      = "date";
        public static final String COLUMN_RATING    = "rating";
        public static final String COLUMN_NOTE      = "note";
        public static final String COLUMN_DEGREE    = "degree";
        public static final String COLUMN_COLOR     = "color";
        public static final String COLUMN_FOAM      = "foam";
        public static final String COLUMN_SERVICE   = "serving";
        public static final String COLUMN_STYLE     = "style";

        public static final String COLUMN_ACID      = "acid";
        public static final String COLUMN_BITTER    = "bitter";
        public static final String COLUMN_SWEET     = "sweet";
        public static final String COLUMN_CEREAL    = "cereal";
        public static final String COLUMN_TOFFEE    = "toffee";
        public static final String COLUMN_COFFEE    = "cofe";
        public static final String COLUMN_HERB      = "herb";
        public static final String COLUMN_FRUIT     = "fruit";
        public static final String COLUMN_SPICE     = "spice";
        public static final String COLUMN_ALCOHOL   = "alcohol";
        public static final String COLUMN_BODY      = "body";
        public static final String COLUMN_LINGER    = "linger";
    }
}
