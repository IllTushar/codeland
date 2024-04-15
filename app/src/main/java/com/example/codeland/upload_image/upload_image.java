package com.example.codeland.upload_image;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.codeland.R;
import com.example.codeland.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class upload_image extends AppCompatActivity {
    String gallery;
    ImageView imageView2;
    AppCompatButton uploadImage, viewImage;
    public static int REQUEST_CODE_ASK_PERMISSIONS = 101;
    public static int ACCESS_IMAGE = 123;
    Utils utils;

    private StorageReference mStorageRef;
    image_model img;
    ProgressDialog mProgressDialog;
    viewImages viewImages;

    ArrayList<image_model> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findId();
        uiFunction();
    }

    private void uiFunction() {
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryPress();
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img.getImageUri() != null) {
                    uploadImageOnFirebase(img.getImageUri());
                } else {
                    utils.toast(upload_image.this, "Please select again!!");
                }
            }
        });

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });
    }

    private void uploadImageOnFirebase(Uri imageUri) {
        if (imageUri != null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            UploadTask uploadTask = fileReference.putFile(imageUri);

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
                    mProgressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        utils.toast(upload_image.this, "Upload successful: " + downloadUri.toString());
                    } else {
                        // Handle failures
                        utils.toast(upload_image.this, "Upload failed: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            utils.toast(upload_image.this, "No file selected");
        }
    }

    ///GalleryPress wants runtime permission to access the gallery....
    private void GalleryPress() {
        gallery = "gal";
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(upload_image.this, "android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        } else if (Build.VERSION.SDK_INT > 28) {
            if (ActivityCompat.checkSelfPermission(upload_image.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(upload_image.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        selectImages();
    }

    private void selectImages() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), ACCESS_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle accordingly
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ACCESS_IMAGE);
            } else {
                // Permission denied, show a message or take appropriate action
                utils.toast(upload_image.this, "Permission Denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Get Image from Gallery
        if (requestCode == ACCESS_IMAGE && resultCode == RESULT_OK) {

            if (data == null) {
                // No data, some error occurred
                return;
            }

            // If single image is selected
            Uri uri = data.getData();
            if (uri != null) {
                Glide.with(upload_image.this)
                        .load(uri).into(imageView2);

                img.setImageUri(uri);
            }

        }
    }

    private void findId() {
        uploadImage = findViewById(R.id.uploadImage);
        viewImage = findViewById(R.id.viewImage);
        utils = new Utils(upload_image.this);
        imageView2 = findViewById(R.id.imageView2);
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        img = new image_model();
        viewImages = new viewImages(upload_image.this);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getImages() {
        ProgressDialog progressDialog = new ProgressDialog(upload_image.this);
        progressDialog.show();
        progressDialog.setMessage("Please wait..");
        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult != null && !listResult.getItems().isEmpty()) {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();
                                        if (uri != null) {

                                            Glide.with(upload_image.this).load(uri).into(imageView2);
                                        } else {
                                            utils.toast(upload_image.this, "There is no images");
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        utils.toast(upload_image.this, e.getMessage());
                                    }
                                });

                    }
                } else {
                    progressDialog.dismiss();
                    utils.toast(upload_image.this, "image folder is empty!!");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }


}