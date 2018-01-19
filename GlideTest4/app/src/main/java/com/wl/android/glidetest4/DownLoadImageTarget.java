package com.wl.android.glidetest4;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by D22397 on 2018/1/4.
 */

public class DownLoadImageTarget implements Target<File>{

    private static final String TAG = "DownLoadImageTarget";

    @Override
    public void onLoadStarted(Drawable placeholder) {

    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
        Log.d(TAG,resource.getPath());
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {

    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        cb.onSizeReady(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
    }

    @Override
    public void setRequest(Request request) {

    }

    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
