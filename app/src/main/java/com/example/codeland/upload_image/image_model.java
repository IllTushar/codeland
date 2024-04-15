package com.example.codeland.upload_image;

import android.net.Uri;

public class image_model {
    Uri imageUri;

    public image_model() {
    }

    public image_model(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
