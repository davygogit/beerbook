package com.davymoreau.android.beerbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class FoamActivity extends AppCompatActivity implements View.OnClickListener {


    LinearLayout layoutLow;
    LinearLayout layoutMedium;
    LinearLayout layoutHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foam);

        layoutLow = (LinearLayout) findViewById(R.id.layout_low_foam);
        layoutLow.setOnClickListener(this);

        layoutMedium = (LinearLayout) findViewById(R.id.layout_medium_foam);
        layoutMedium.setOnClickListener(this);

        layoutHigh = (LinearLayout) findViewById(R.id.layout_high_foam);
        layoutHigh.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = 0;
        switch (v.getId()) {
            case R.id.layout_low_foam:
                id = 0;
                break;
            case R.id.layout_medium_foam:
                id = 1;
                break;
            case R.id.layout_high_foam:
                id = 2;
        }

        Intent intent = getIntent();
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();

    }
}
