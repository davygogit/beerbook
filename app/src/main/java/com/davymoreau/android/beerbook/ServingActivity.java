package com.davymoreau.android.beerbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class ServingActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout layoutBottle;
    LinearLayout layoutDraft;
    LinearLayout layoutCan ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serving);

        layoutBottle = (LinearLayout)findViewById(R.id.layout_bottle);
        layoutBottle.setOnClickListener(this);

        layoutDraft = (LinearLayout)findViewById(R.id.layout_draft);
        layoutDraft.setOnClickListener(this);

        layoutCan = (LinearLayout)findViewById(R.id.layout_can);
        layoutCan.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = 0;

        switch (v.getId()){
            case R.id.layout_bottle:
                id = 0;
                break;
            case R.id.layout_draft:
                id = 1;
                break;
            case R.id.layout_can:
                id = 2;
                break;
        }

        Intent intent = getIntent();
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();

    }
}
