package com.example.codeland.upload_image;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeland.R;
import com.example.codeland.upload_image.ImageAdapter.Adapter;

import java.util.ArrayList;

public class viewImages {
    Context context;
    Adapter adapter;
    image_model imageModel;

    public viewImages(Context context) {
        this.context = context;
        imageModel = new image_model();
    }

    public void dialogBox(Context context, ArrayList<image_model> list) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_box);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // Create and set the adapter
        Adapter adapter = new Adapter(context,list);
        recyclerView.setAdapter(adapter);

        dialog.show();
    }

}
