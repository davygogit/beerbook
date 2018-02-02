package com.davymoreau.android.beerbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;

import java.io.File;

public class addtestActivity extends AppCompatActivity {

    TextView tvTest;
    ImageView myView;
    File fileTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtest);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        tvTest = (TextView) findViewById(R.id.tv_test);
        myView = (ImageView)findViewById(R.id.ivTest);

        int id = 0;
        Intent myItent = getIntent();
        if (myItent.hasExtra("id")) {
            id = myItent.getIntExtra("id", 0);
        }

        // cnx base

        BeerTastingHelper helper = new BeerTastingHelper(this);
        SQLiteDatabase mDb = helper.getWritableDatabase();
        Cursor cursor = mDb.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, BeerTastingContract.BeerTastingEntry._ID + " = " + id, null, null, null, null);
        //Cursor cursor = mDb.query(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getColumnCount(); i++) {
            String ColumnName = cursor.getColumnName(i);
            String value = cursor.getString(i);
            tvTest.setText(tvTest.getText().toString() + "\n" + ColumnName + " : " + value);

        }
        cursor.close();

        fileTest = new File(Environment.getExternalStorageDirectory(), String.valueOf(id)+".jpg");
        Uri uri = Uri.fromFile(fileTest);
        Glide.with(this)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(myView);
    }
}
