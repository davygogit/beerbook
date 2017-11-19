package com.davymoreau.android.beerbook;

/**
 * Created by davy on 11/10/2017.
 */

public class Color  {

    private String mName;
    private int mHexCode;

    public Color( String name, int code){
        mHexCode = code;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public int getHexCode() {
        return mHexCode;
    }

    @Override
    public String toString() { return mName; }
}
