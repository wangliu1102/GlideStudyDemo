package com.wl.android.glidetest6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    public void loadImage(View view) {
        String url = "http://guolin.tech/book.png";
//        Glide.with(this)
//                .load(url)
//                .into(mImageView);

        Glide.with(this)
                .load(url)
                .skipMemoryCache(true) // 禁用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用硬盘缓存
                .into(mImageView);
    }
}
