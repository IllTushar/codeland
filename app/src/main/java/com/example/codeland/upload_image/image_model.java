package com.example.codeland.upload_image;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.codeland.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class image_model {
    Uri imageUri;
   Context context;
   Utils utils;
    public image_model(Context context) {
        this.context =context;
        utils = new Utils(context);
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


    public void uploadImageOnFirebase(Context context,Uri imageUri, ProgressDialog mProgressDialog,StorageReference mStorageRef) {
        if (imageUri != null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            UploadTask uploadTask = fileReference.putFile(imageUri);

            ProgressDialog finalMProgressDialog = mProgressDialog;
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    finalMProgressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        utils.toast(context, "Upload successful: " + downloadUri.toString());
                    } else {
                        // Handle failures
                        utils.toast(context, "Upload failed: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            utils.toast(context, "No file selected");
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void getImages(Context context, ImageView imageView2, StorageReference mStorageRef) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // ArrayList to store download URLs
        List<Uri> downloadUrls = new ArrayList<>();

        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult != null && !listResult.getItems().isEmpty()) {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (uri != null) {
                                    downloadUrls.add(uri);
                                }

                                // Check if this is the last item
                                if (downloadUrls.size() == listResult.getItems().size()) {
                                    progressDialog.dismiss();
                                    loadLastImageIntoImageView(context, imageView2, downloadUrls);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                utils.toast(context, e.getMessage());
                            }
                        });
                    }
                } else {
                    progressDialog.dismiss();
                    utils.toast(context, "Image folder is empty!!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                utils.toast(context, e.getMessage());
            }
        });
    }

    // Method to load the last image from the list into ImageView using Glide
    private void loadLastImageIntoImageView(Context context, ImageView imageView, List<Uri> downloadUrls) {
        if (downloadUrls.isEmpty()) {
            utils.toast(context, "There are no images");
            return;
        }

        // Load the last image from the list
        Uri lastImageUrl = downloadUrls.get(downloadUrls.size() - 1);
        Glide.with(context)
                .load(lastImageUrl)
                .into(imageView);
    }


}
