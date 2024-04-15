package com.example.codeland.login_module;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeland.R;
import com.example.codeland.upload_image.upload_image;
import com.example.codeland.utils.Utils;

public class login_module extends AppCompatActivity {
    AppCompatButton loginButton;
    Utils utils;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_module);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findIds();
        uiFunction();

    }

    private void uiFunction() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.intent(login_module.this, upload_image.class);
            }
        });
    }

    public void findIds() {
        loginButton = findViewById(R.id.loginButton);
        utils = new Utils(login_module.this);

    }
}