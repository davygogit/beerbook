package com.davymoreau.android.beerbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.spiderchart.SpiderChartView;

import java.io.File;
import java.util.ArrayList;

public class BeerDetailActivity extends AppCompatActivity {

    long mId;
    boolean mModifiable = false;
    Context mContext;
    ContentValues mCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        /*View appBar =  findViewById(R.id.app_bar);
        appBar.getLayoutParams().height = appBar.getWidth();
        appBar.requestLayout();*/

        // récupération cv
        //ContentValues mCv = null;
        Intent myItent = getIntent();
        if (myItent.hasExtra("cv")) {
            mCv = myItent.getParcelableExtra("cv");
        }
        // récupération id
        if (mCv.containsKey(BeerTastingContract.BeerTastingEntry._ID)) {
            mId = mCv.getAsLong(BeerTastingContract.BeerTastingEntry._ID);
            mModifiable = true;
        }

        String path = mCv.getAsString("path");
        // nom bière
        String beerName = mCv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
        // brasserie
        String brewery = mCv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY);
        // date
        String date = mCv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_DATE);
        // rating
        float rating = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
        // notes
        String notes = mCv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NOTES);
        // degree
        float degree = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE);
        // color
        Integer color = mCv.getAsInteger(BeerTastingContract.BeerTastingEntry.COLUMN_COLOR);
        // foam
        Integer foam = mCv.getAsInteger(BeerTastingContract.BeerTastingEntry.COLUMN_FOAM);
        // serving
        Integer serving = mCv.getAsInteger(BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE);
        // style
        String style = mCv.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_STYLE);
        // flavours
        float acid = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_ACID);
        float bitter = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_BITTER);
        float sweet = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_SWEET);
        float cereal = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL);
        float toffee = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE);
        float coffee = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE);
        float herb = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_HERB);
        float fruit = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT);
        float spice = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_SPICE);
        float alcohol = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL);
        float body = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_BODY);
        float linger = mCv.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_LINGER);


        // maj image
        ImageView ivBeer = (ImageView) findViewById(R.id.ivct_beer);
        //ivBeer.getLayoutParams().height = ivBeer.getWidth();
        //ivBeer.requestLayout();
        File fileImg = new File(path);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_detail);
        progress.setVisibility(View.VISIBLE);
        progress.bringToFront();
        if (fileImg.exists()) {
            Uri uri = Uri.fromFile(fileImg);

            Glide.with(this)
                    .load(uri)
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_detail);

                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivBeer);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.biere);
            ivBeer.setImageDrawable(drawable);
            progress.setVisibility(View.GONE);
        }

        this.setTitle(beerName);
        // brasserie
        TextView tvBrewery = (TextView) findViewById(R.id.tv_detail_Brewery);
        tvBrewery.setText(brewery);
        // notes
        TextView tvNotes = (TextView) findViewById(R.id.tv_detail_notes);
        tvNotes.setText(notes);
        // date
        TextView tvDate = (TextView) findViewById(R.id.tv_detail_date);
        tvDate.setText(date);
        // style
        TextView tvStyle = (TextView) findViewById(R.id.tv_detail_style);
        tvStyle.setText(style);
        // ratting
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar_detail);
        ratingBar.setRating(rating);
        // chart
        ArrayList<ContentValues> cvList = new ArrayList<>();
        ContentValues cv = new ContentValues();


        cv.put("label", getString(R.string.acidity));
        cv.put("value", acid);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.bitterness));
        cv.put("value", bitter);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.sweetness));
        cv.put("value", sweet);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.cereal));
        cv.put("value", cereal);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.toffee));
        cv.put("value", toffee);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.coffee));
        cv.put("value", coffee);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.herb));
        cv.put("value", herb);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.fruit));
        cv.put("value", fruit);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.spice));
        cv.put("value", spice);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.alcohol));
        cv.put("value", alcohol);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.body));
        cv.put("value", body);
        cvList.add(cv);
        cv = new ContentValues();
        cv.put("label", getString(R.string.linger));
        cv.put("value", linger);
        cvList.add(cv);

        SpiderChartView spiderChartView = (SpiderChartView) findViewById(R.id.spider);

        spiderChartView.setData(cvList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_beer_detail);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //TODO: Write your logic here
                if (menuItem.getItemId() == R.id.action_edit) {
                    Intent intent = new Intent(mContext, AddBeerActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("cv", mCv);
                    startActivity(intent);
                }
                return true;
            }
        });

        return true;
    }
}
