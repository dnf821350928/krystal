package com.xlh.krystal.littlejoker;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * 这是那个用来专门看大图的view
 */
public class HDImageActivity extends AppCompatActivity {

    private ImageView hd_iv;//看大图的view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdimage);
        init();
        setImageResource();
    }

    /**
     * 获取图片资源，并设置给view
     */
    private void setImageResource() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Picasso.with(this).load(url).into(hd_iv);
    }

    private void init() {
        hd_iv = (ImageView) findViewById(R.id.hd_iv);
    }
}
