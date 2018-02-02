package com.davymoreau.android.beerbook.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.davymoreau.android.beerbook.constApp.FB_PHOTO_PATH;

/**
 * Created by davy on 10/12/2017.
 */

public class FBUtil {
    Uri mUri;

    public Uri GetPhotoUri(String key){

        FirebaseStorage firebaseStorage;
        StorageReference storageReference;
        firebaseStorage = FirebaseStorage.getInstance();

        String picPath = FB_PHOTO_PATH + key + ".jpg";

        try {
            storageReference = firebaseStorage.getReferenceFromUrl(picPath);
            OnSuccessListener successListener = new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mUri = uri;
                }
            };

            storageReference.getDownloadUrl().addOnSuccessListener(successListener);

        } catch (Exception e){
            mUri = null;
        }
        return mUri;
    }
}
