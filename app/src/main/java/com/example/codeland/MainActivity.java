package com.example.codeland;

import static java.lang.Thread.sleep;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeland.login_module.login_module;
import com.example.codeland.utils.Utils;

public class MainActivity extends AppCompatActivity {
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findID();
        uiFunctions();
        utils.setStatusBarColor(MainActivity.this, R.color.theme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiFunctions();
    }

    private void uiFunctions() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    utils.intent(MainActivity.this, login_module.class);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    private void findID() {
        utils = new Utils(MainActivity.this);
    }
}