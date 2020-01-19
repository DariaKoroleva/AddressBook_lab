package com.example.addressbook_lab;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImagePreview extends AppCompatActivity {
    public static final String PREVIEW_KEY = "PREVIEW_KEY";

    Bitmap image;
    ImageView imageView;
    Button bt_exit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        imageView = findViewById(R.id.imageView);
        bt_exit = findViewById(R.id.bt_exit);

        //получаем данные, переданные в активность
        image = (Bitmap) getIntent().getParcelableExtra(PREVIEW_KEY);

        setImage();

    }

    public void setBt_exit(View v){
        this.finish();
    }


    private void setImage(){
        imageView.setImageBitmap(image);
    }
}
