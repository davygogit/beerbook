package com.davymoreau.android.beerbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.davymoreau.android.beerbook.util.FileUtil;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import static com.davymoreau.android.beerbook.constApp.STYLES_FILE;
import static com.davymoreau.android.beerbook.firebase.SyncUtil.firebasesync;

public class SplashActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBeersDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 123;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // tester si première connection
        mFirebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains("first")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first", false);
            editor.commit();
            // affiher le dialogbox pour login// 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.first_login_text)
                    .setTitle("login");

            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            });
            builder.setNegativeButton("later", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    syncAndFinish(dir);
                }
            });

// 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();

        } else {
            syncAndFinish(dir);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            syncAndFinish(dir);
        }
    }


    private void syncAndFinish(File dir) {
        String uid = "";
        FirebaseUser fbUser = mFirebaseAuth.getCurrentUser();
        if (fbUser != null) {
            uid = fbUser.getUid();
            firebasesync(this, uid, dir);
        }

        InputStream test = getResources().openRawResource(R.raw.beertypes);

        File crop = new File(getExternalCacheDir(), "crop.jpg");
        crop.delete();

        // si fichier type de bière n'existe pas le créer.
        File file = new File(STYLES_FILE);
        if (!file.exists())
            FileUtil.rawFileToInternalStorage(this, R.raw.beertypes, STYLES_FILE);
        // synchro fire base
        // Lancer la main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}



