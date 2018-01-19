package com.wl.android.glidetest7;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by D22397 on 2018/1/5.
 */

public class OkHttpGlideUrlLoader implements ModelLoader<GlideUrl,InputStream> {

    private OkHttpClient mOkHttpClient;

    public OkHttpGlideUrlLoader(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl,InputStream>{

        private OkHttpClient client;

        public Factory() {
        }

        public Factory(OkHttpClient client) {
            this.client = client;
        }

        public synchronized OkHttpClient getOkHttpClient(){
            if (client == null){
                client = new OkHttpClient();
            }
            return client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new OkHttpGlideUrlLoader(getOkHttpClient());
        }

        @Override
        public void teardown() {

        }
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        return new OkHttpFetcher(mOkHttpClient,model);
    }
}
