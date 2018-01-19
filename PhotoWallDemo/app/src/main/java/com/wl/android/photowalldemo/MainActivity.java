package com.wl.android.photowalldemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private GridView mPhotoWall;
    private PhotoWallAdapter mPhotoWallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotoWall = (GridView) findViewById(R.id.photo_wall);
        mPhotoWallAdapter = new PhotoWallAdapter(this, 0, Images.imageThumbUrls, mPhotoWall);
        mPhotoWall.setAdapter(mPhotoWallAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  退出程序时结束所有的下载任务
        mPhotoWallAdapter.cancleAllTasks();
    }


}
