package com.davymoreau.android.beerbook.firebase;

import android.content.ContentValues;

import com.davymoreau.android.beerbook.database.BeerTastingContract;

/**
 * Created by davy on 19/11/2017.
 */

public class BeerFB {

    BeerFB(){

    }

    public BeerFB(ContentValues cv, String author){
        auth    = author;
        name    = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
        Brewery = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY);
        date    = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
        rating  = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
        notes   = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NOTES);
        degree  = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE);
        color   = cv.getAsInteger(BeerTastingContract.BeerTastingEntry.COLUMN_COLOR);
        foam    = cv.getAsInteger(BeerTastingContract.BeerTastingEntry.COLUMN_FOAM);
        serving = cv.getAsInteger(BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE);
        style   = cv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_STYLE);
        acid    = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_ACID);
        bitter  = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_BITTER);
        sweet   = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_SWEET);
        cereal  = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL);
        toffee  = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE);
        coffee  = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE);
        herb    = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_HERB);
        fruit   = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT);
        spice   = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_SPICE);
        alcohol = cv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL);
    }

    public ContentValues retrieveContentValue(){
        ContentValues cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME,name);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY,Brewery);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_DATE,date);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING,rating);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NOTES,notes);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE,degree);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_COLOR,color);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_FOAM,foam);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE,serving);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_STYLE,style);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ACID,acid);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BITTER,bitter);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_SWEET,sweet);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL,cereal);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE,toffee);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE,coffee);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_HERB,herb);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT,fruit);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_SPICE,spice);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL,alcohol);

        return cv;

    }

    private String auth;
    private String name;
    private String Brewery;
    private String date;
    private float rating;
    private String notes;
    private float degree;
    private int color;
    private int foam;
    private int serving;
    private String style;
    private float acid;
    private float bitter;
    private float sweet;
    private float cereal;
    private float toffee;
    private float coffee;
    private float herb;
    private float fruit;
    private float spice;
    private float alcohol;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrewery() {
        return Brewery;
    }

    public void setBrewery(String brewery) {
        Brewery = brewery;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRatting() {
        return rating;
    }

    public void setRatting(float ratting) {
        this.rating = ratting;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getFoam() {
        return foam;
    }

    public void setFoam(int foam) {
        this.foam = foam;
    }

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public float getAcid() {
        return acid;
    }

    public void setAcid(float acid) {
        this.acid = acid;
    }

    public float getBitter() {
        return bitter;
    }

    public void setBitter(float bitter) {
        this.bitter = bitter;
    }

    public float getSweet() {
        return sweet;
    }

    public void setSweet(float sweet) {
        this.sweet = sweet;
    }

    public float getCereal() {
        return cereal;
    }

    public void setCereal(float cereal) {
        this.cereal = cereal;
    }

    public float getToffee() {
        return toffee;
    }

    public void setToffee(float toffee) {
        this.toffee = toffee;
    }

    public float getCoffee() {
        return coffee;
    }

    public void setCoffee(float coffee) {
        this.coffee = coffee;
    }

    public float getHerb() {
        return herb;
    }

    public void setHerb(float herb) {
        this.herb = herb;
    }

    public float getFruit() {
        return fruit;
    }

    public void setFruit(float fruit) {
        this.fruit = fruit;
    }

    public float getSpice() {
        return spice;
    }

    public void setSpice(float spice) {
        this.spice = spice;
    }

    public float getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(float alcohol) {
        this.alcohol = alcohol;
    }


}
