package com.davymoreau.android.beerbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.davymoreau.android.beerbook.util.FileUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.InputStream;

import static com.davymoreau.android.beerbook.constApp.STYLES_FILE;
import static com.davymoreau.android.beerbook.firebase.FirebaseUtil.firebasesync;

public class SplashActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBeersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebasesync(this);

        InputStream test = getResources().openRawResource(R.raw.beertypes);

        File crop = new File(getExternalCacheDir(), "crop.jpg");
        crop.delete();

        // si fichier type de bière n'existe pas le créer.
        File file = new File(STYLES_FILE);
        if (!file.exists()) FileUtil.rawFileToInternalStorage(this, R.raw.beertypes, STYLES_FILE);
        // synchro fire base
        // Lancer la main activity
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}



