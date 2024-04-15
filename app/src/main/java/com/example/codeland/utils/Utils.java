package com.example.codeland.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class Utils {
    Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void logs(String tags, String message) {
        Log.d(tags, message);
    }

    public void intent(Context context, Class<?> TargetClass) {
        context.startActivity(new Intent(context, TargetClass));
    }

    public void setStatusBarColor(Context context, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.setStatusBarColor(ContextCompat.getColor(context, colorResId));
        }
    }
}
