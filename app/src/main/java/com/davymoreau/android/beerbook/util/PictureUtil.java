package com.davymoreau.android.beerbook.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static com.davymoreau.android.beerbook.constApp.PIC_PATH;
import static com.davymoreau.android.beerbook.constApp.PIC_URI;

/**
 * Created by davy on 10/02/2018.
 */

public class PictureUtil {
    public static final void displayPic(ContentValues cv, final ImageView imageView, final View progress, final Drawable defaultPic, final Context context){

        progress.setVisibility(View.VISIBLE);

        if (cv.containsKey(PIC_URI)){

            FirebaseStorage firebaseStorage;
            StorageReference storageReference;
            firebaseStorage = FirebaseStorage.getInstance();
            String pic = cv.getAsString(PIC_URI);
            try {
                storageReference = firebaseStorage.getReferenceFromUrl(pic);
            } catch (Exception e) {
                storageReference = null;
            }

            if(storageReference!=null) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("aie aie aie aie", uri.toString() + "---" + context.toString());
                        Glide.with(context)
                                .load(uri)
                                .listener(new RequestListener<Uri, GlideDrawable>() {

                                    @Override
                                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        Log.e("aie aie aie aie", "-----------------------------exp---------------------------");
                                        progress.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        Log.e("aie aie aie aie", "-------------------------------OK--------------------------");
                                        progress.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageView.setImageDrawable(defaultPic);
                        progress.setVisibility(View.GONE);
                    }
                });
            }

        } else if(cv.containsKey(PIC_PATH)) {
            String pic = cv.getAsString(PIC_PATH);
            File file = new File(pic);
            Uri uri;
            if (file.exists()) {
                uri = Uri.fromFile(file);
            } else {
                uri = Uri.parse(pic);
            }
            Glide.with(context)
                    .load(uri)
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);

        } else {
            imageView.setImageDrawable(defaultPic);
            progress.setVisibility(View.GONE);
        }

    }
}
