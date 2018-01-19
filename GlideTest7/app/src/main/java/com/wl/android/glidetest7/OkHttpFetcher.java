package com.wl.android.glidetest7;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by D22397 on 2018/1/5.
 */

public class OkHttpFetcher implements DataFetcher<InputStream> {

    private final OkHttpClient mClient;
    private final GlideUrl mUrl;

    private InputStream mStream;
    private ResponseBody mResponseBody;
    private volatile boolean mIsCancelled;

    public OkHttpFetcher(OkHttpClient client, GlideUrl url) {
        mClient = client;
        mUrl = url;
    }


    @Override
    public InputStream loadData(Priority priority) throws Exception {
        Request.Builder requestBuilder = new Request.Builder().url(mUrl.toStringUrl());
        for (Map.Entry<String, String> headerEntry : mUrl.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        Request request = requestBuilder.build();
        if (mIsCancelled) {
            return null;
        }
        Response response = mClient.newCall(request).execute();
        mResponseBody = response.body();
        if (!response.isSuccessful() || mResponseBody == null) {
            throw new IOException("Request failed with code:" + response.code());
        }
        mStream = ContentLengthInputStream.obtain(mResponseBody.byteStream(), mResponseBody.contentLength());

        return mStream;
    }

    @Override
    public void cleanup() {

        try {
            if (mStream != null) {
                mStream.close();
            }
            if (mResponseBody != null) {
                mResponseBody.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getId() {
        return mUrl.getCacheKey();
    }

    @Override
    public void cancel() {
        mIsCancelled = true;
    }
}
