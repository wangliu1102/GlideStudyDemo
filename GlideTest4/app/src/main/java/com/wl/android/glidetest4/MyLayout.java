package com.wl.android.glidetest4;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * Created by D22397 on 2018/1/3.
 */

public class MyLayout extends LinearLayout {

    private ViewTarget<MyLayout,GlideDrawable> mViewTarget;

    public MyLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mViewTarget = new ViewTarget<MyLayout, GlideDrawable>(this) {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                MyLayout myLayout = getView();
                myLayout.setImageAsBackground(resource);
            }
        };
    }

    public ViewTarget<MyLayout,GlideDrawable> getTarget(){
        return mViewTarget;
    }

    private void setImageAsBackground(GlideDrawable resource) {
        setBackground(resource);
    }
}
