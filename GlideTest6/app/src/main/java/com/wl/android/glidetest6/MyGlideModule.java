package com.wl.android.glidetest6;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by D22397 on 2018/1/4.
 */

public class MyGlideModule implements GlideModule {

    private static final int DISK_CACHE_SIZE = 500 * 1024 * 1024;

    // 更改和配置Glide
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 所有Glide加载的图片都会缓存到SD卡上,默认硬盘缓存大小都是250M,这里设置为500M
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context,DISK_CACHE_SIZE));
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);// 默认格式是RGB_565,这里设置为ARGB_8888
    }

    // 替换Glide组件
    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class, new OkhttpGlideUrlLoader.Factory());
    }
}
