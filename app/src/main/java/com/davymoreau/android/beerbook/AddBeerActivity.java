package com.davymoreau.android.beerbook;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davymoreau.android.beerbook.database.BeerTastingContract;
import com.davymoreau.android.beerbook.database.BeerTastingHelper;
import com.davymoreau.android.beerbook.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.R.attr.name;
import static com.davymoreau.android.beerbook.constApp.STYLES_FILE;

public class AddBeerActivity extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase mDb;

    EditText edAlcohol;
    EditText edName;
    EditText edBrewery;
    AutoCompleteTextView edStyle;
    Spinner spColor;
    ImageView imageView;
    EditText edNote;
    TextView tvFoam;
    TextView tvServing;
    RatingBar rbRating;


    TextView tvAcidity;
    SeekBar sbAcidity;
    TextView tvBitter;
    SeekBar sbBitter;
    TextView tvSweet;
    SeekBar sbSweet;
    TextView tvCereal;
    SeekBar sbCereal;
    TextView tvToffee;
    SeekBar sbToffee;
    TextView tvCoffee;
    SeekBar sbCoffee;
    TextView tvHerb;
    SeekBar sbHerb;
    TextView tvFruit;
    SeekBar sbFruit;
    TextView tvSpice;
    SeekBar sbSpice;
    TextView tvAlcohol;
    SeekBar sbAlcohol;
    TextView tvBody;
    SeekBar sbBody;
    TextView tvLinger;
    SeekBar sbLinger;


    Spinner spinner;

    File photoTemp;
    File photoCrop;
    File dir;

    int foamId = 0;
    int servingId = 0;
    int DbId;

    ArrayList<String> stylesList;

    private Uri tempUri;
    private Uri cropUri;

    private static final int TAKE_PICTURE = 2;
    private static final int CROP_PIC = 3;
    private static final int FOAM = 4;
    private static final int SERVING = 5;

    int foam = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        photoCrop.delete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beer);

        // récupère la liste des styles
        stylesList = FileUtil.InternalStorageToArray(this, STYLES_FILE);
        Collections.sort(stylesList, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return 0;
            }
        });
        String[] styles = stylesList.toArray(new String[stylesList.size()]);

        // nom de la bière
        edName = (EditText) findViewById(R.id.ed_beer_name);
        // nom de la brasseri
        edBrewery = (EditText) findViewById(R.id.ed_brewery);
        // degree d'alcool
        edAlcohol = (EditText) findViewById(R.id.ed_degree);
        // type de bière
        edStyle = (AutoCompleteTextView) findViewById(R.id.ed_style);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, styles);
        edStyle.setThreshold(1);
        edStyle.setAdapter(adapter);
        // couleur
        spColor = (Spinner) findViewById(R.id.spinner);
        // photo
        imageView = (ImageView) findViewById(R.id.ivAddBeer);
        // notes sur la bière
        edNote = (EditText) findViewById(R.id.ed_note);
        // réduit la taille de l'edit text lors de la perte du focus
        edNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edNote.setMaxLines(100);
                } else {
                    edNote.setMaxLines(3);
                    edNote.setSelection(0);
                }
            }
        });
        // mousse
        tvFoam = (TextView) findViewById(R.id.ed_foam);
        tvFoam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foamIntent = new Intent(AddBeerActivity.this, FoamActivity.class);
                startActivityForResult(foamIntent, FOAM);
            }

        });

        tvFoam.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent foamIntent = new Intent(AddBeerActivity.this, FoamActivity.class);
                    startActivityForResult(foamIntent, FOAM);
                }
            }
        });

        updateFoam();
        // type de service
        tvServing = (TextView) findViewById(R.id.ed_serving);

        tvServing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent servIntent = new Intent(AddBeerActivity.this, ServingActivity.class);
                startActivityForResult(servIntent, SERVING);
            }
        });

        tvServing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent servIntent = new Intent(AddBeerActivity.this, ServingActivity.class);
                    startActivityForResult(servIntent, SERVING);
                }
            }
        });

        updateServing();

        // rating
        rbRating = (RatingBar)findViewById(R.id.ratting);

        // acidité
        tvAcidity = (TextView) findViewById(R.id.tv_acid);
        sbAcidity = (SeekBar) findViewById(R.id.sb_acid);
        sbAcidity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvAcidity, seekBar, R.string.acidity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvAcidity, sbAcidity, R.string.acidity);
        // amertume
        tvBitter = (TextView) findViewById(R.id.tv_bitter);
        sbBitter = (SeekBar) findViewById(R.id.sb_bitter);
        sbBitter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvBitter, seekBar, R.string.bitterness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvBitter, sbBitter, R.string.bitterness);
        // sucré
        tvSweet = (TextView) findViewById(R.id.tv_sweet);
        sbSweet = (SeekBar) findViewById(R.id.sb_sweet);
        sbSweet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvSweet, seekBar, R.string.sweetness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvSweet, sbSweet, R.string.sweetness);


        // cereale
        tvCereal = (TextView) findViewById(R.id.tv_cereal);
        sbCereal = (SeekBar) findViewById(R.id.sb_ceral);
        sbCereal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvCereal, seekBar, R.string.cereal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvCereal, sbCereal, R.string.cereal);

        // caramel
        tvToffee = (TextView) findViewById(R.id.tv_toffee);
        sbToffee = (SeekBar) findViewById(R.id.sb_toffee);
        sbToffee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvToffee, seekBar, R.string.toffee);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvToffee, sbToffee, R.string.toffee);

        // café
        tvCoffee = (TextView) findViewById(R.id.tv_coffee);
        sbCoffee = (SeekBar) findViewById(R.id.sb_coffee);
        sbCoffee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvCoffee, seekBar, R.string.coffee);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvCoffee, sbCoffee, R.string.coffee);

        // herbe
        tvHerb = (TextView) findViewById(R.id.tv_herb);
        sbHerb = (SeekBar) findViewById(R.id.sb_herb);
        sbHerb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvHerb, seekBar, R.string.herb);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvHerb, sbHerb, R.string.herb);

        // fruit
        tvFruit = (TextView) findViewById(R.id.tv_fruit);
        sbFruit = (SeekBar) findViewById(R.id.sb_fruit);
        sbFruit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvFruit, seekBar, R.string.fruit);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvFruit, sbFruit, R.string.fruit);

        // épice
        tvSpice = (TextView) findViewById(R.id.tv_spice);
        sbSpice = (SeekBar) findViewById(R.id.sb_spice);
        sbSpice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvSpice, seekBar, R.string.spice);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvSpice, sbSpice, R.string.spice);

        // alcool
        tvAlcohol = (TextView) findViewById(R.id.tv_alcohol_taste);
        sbAlcohol = (SeekBar) findViewById(R.id.sb_alcohol_taste);
        sbAlcohol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvAlcohol, seekBar, R.string.alcohol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvAlcohol, sbAlcohol, R.string.alcohol);

        // corps
        tvBody= (TextView) findViewById(R.id.tv_body);
        sbBody= (SeekBar) findViewById(R.id.sb_body);
        sbBody.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvBody, seekBar, R.string.body);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvBody, sbBody, R.string.body);

        // persistance
        tvLinger= (TextView) findViewById(R.id.tv_linger);
        sbLinger= (SeekBar) findViewById(R.id.sb_linger);
        sbLinger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(tvLinger, seekBar, R.string.linger);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSeekBar(tvLinger, sbLinger, R.string.linger);

       dir = Environment.getExternalStorageDirectory();

        photoTemp = new File(dir, "temp.jpg");
        tempUri = Uri.fromFile(photoTemp);
        photoCrop = new File(dir, "crop.jpg");
        cropUri = Uri.fromFile(photoCrop);
        if (photoCrop.exists()) {
            Glide.with(this)
                    .load(cropUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                startActivityForResult(intent, TAKE_PICTURE);


            }
        });


        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(getString(R.string.pale_straw), 0xfff0c566));
        colors.add(new Color(getString(R.string.straw), 0xffe9ad3f));
        colors.add(new Color(getString(R.string.pale_gold), 0xffe19726));
        colors.add(new Color(getString(R.string.deep_gold), 0xffd1730c));
        colors.add(new Color(getString(R.string.pale_amber), 0xffb74d00));
        colors.add(new Color(getString(R.string.medium_amber), 0xff9f3400));
        colors.add(new Color(getString(R.string.deep_amber), 0xff882300));
        colors.add(new Color(getString(R.string.amber_brown), 0xff741800));
        colors.add(new Color(getString(R.string.brown), 0xff681200));
        colors.add(new Color(getString(R.string.ruby_brown), 0xff540b00));
        colors.add(new Color(getString(R.string.deep_brown), 0xff3d0500));
        colors.add(new Color(getString(R.string.black), 0xff240100));

        ColorArrayAdapter colorAdapter = new ColorArrayAdapter(this, colors);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(colorAdapter);


        Button btAdd = (Button) findViewById(R.id.bt_add);


        BeerTastingHelper helper = new BeerTastingHelper(this);
        mDb = helper.getWritableDatabase();

        btAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        String style = edStyle.getText().toString();

        saveData();

        if (!stylesList.contains(style)) {
            stylesList.add(style);
            FileUtil.ArrayToInternalStorage(this, stylesList, STYLES_FILE);
        }
        Toast.makeText(AddBeerActivity.this, "bière " + name + " ajoutée", Toast.LENGTH_SHORT).show();
        photoCrop.delete();
        finish();
    }

    private void saveData(){
        Date date = new Date();
        date.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);
        String style = edStyle.getText().toString();

        String name = edName.getText().toString();
        String brewery = edBrewery.getText().toString();
        String strAlcohol = edAlcohol.getText().toString();
        if (strAlcohol.isEmpty()) {
            strAlcohol = "0";
        }
        double alcohol = Double.valueOf(strAlcohol);

        int color = (int)(spColor.getSelectedItemId());

        float rating = rbRating.getRating();

        String note = edNote.getText().toString();

        int serving = servingId;


        Log.d("debug !!!!!", "nom: " + name + " brasserie: " + brewery + " alcohol: " + alcohol + " style: " + style + " date: " + today);
        ContentValues cv = new ContentValues();
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NAME, name);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY, brewery);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE, alcohol);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_STYLE, style);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_DATE, today);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_COLOR, color);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_RATING, rating);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_NOTE, note);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_FOAM, foamId);
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_SERVICE, servingId);

        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ACID, sbAcidity.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BITTER, sbBitter.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_SWEET, sbSweet.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_CEREAL, sbCereal.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_TOFFEE, sbToffee.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_COFFEE, sbCoffee.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_HERB, sbHerb.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_FRUIT, sbFruit.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_SPICE, sbSpice.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_ALCOHOL, sbAlcohol.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_BODY, sbBody.getProgress());
        cv.put(BeerTastingContract.BeerTastingEntry.COLUMN_LINGER, sbLinger.getProgress());
        DbId =(int) (mDb.insert(BeerTastingContract.BeerTastingEntry.TABLE_BEER_NAME, null, cv));

        // save picture

        File dest =  new File(dir, String.valueOf(DbId) + ".jpg");
        if (!photoCrop.renameTo(dest)){
            Toast.makeText(this, "merde lors du renomage", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {

                        performCrop();

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to crop", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
                break;
            case CROP_PIC: {
                try {

                    Glide.with(this)
                            .load(cropUri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);

                    photoTemp.delete();


                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
            }
            break;
            case FOAM:
                if (resultCode == Activity.RESULT_OK) {
                    foamId = data.getIntExtra("id", 0);
                    updateFoam();
                }
                break;
            case SERVING:
                if (resultCode == Activity.RESULT_OK) {
                    servingId = data.getIntExtra("id", 0);
                    updateServing();
                }
                break;
        }
    }

    /**
     * this function does the crop operation.
     */
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(tempUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);

            cropIntent.putExtra("output", cropUri);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void updateFoam() {
        String sfoam = "";
        switch (foamId) {
            case 0:
                sfoam = getString(R.string.low_foam);
                break;
            case 1:
                sfoam = getString(R.string.medium_foam);
                break;
            case 2:
                sfoam = getString(R.string.high_foam);
                break;
        }

        tvFoam.setText(sfoam);
    }

    private void updateServing() {
        String sServ = "";
        switch (servingId) {
            case 0:
                sServ = getString(R.string.bottle);
                break;
            case 1:
                sServ = getString(R.string.draft);
                break;
            case 2:
                sServ = getString(R.string.can);
                break;
        }

        tvServing.setText(sServ);
    }


    private void updateSeekBar(TextView tv, SeekBar sb, int name) {
        int progress = sb.getProgress();
        String st = "";
        if (progress < 3) st = getString(R.string.low);
        else if (progress > 7) st = getString(R.string.high);
        else st = getString(R.string.medium);

        st = getString(name) + " " + st + " " + "(" + progress + "/" + sb.getMax() + ")";
        tv.setText(st);

    }

    public void test(View view){
        saveData();
        Intent intent = new Intent(this, addtestActivity.class);
        intent.putExtra("id", DbId);
        startActivity(intent);
    }

    private void deleteCrop(){
        File crop = new File(dir, "crop.jpg");
        crop.delete();
    }
}



