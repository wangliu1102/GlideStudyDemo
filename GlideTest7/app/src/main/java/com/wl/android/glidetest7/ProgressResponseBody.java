package com.wl.android.glidetest7;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by D22397 on 2018/1/5.
 */

public class ProgressResponseBody extends ResponseBody {

    private static final String TAG = "ProgressResponseBody";

    private BufferedSource mBufferedSource;

    private ResponseBody mResponseBody;

    private ProgressListener mListener;

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        mResponseBody = responseBody;
        mListener = ProgressInterceptor.LISTENER_MAP.get(url);
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(new ProgressSource(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private class ProgressSource extends ForwardingSource {

        long totalBytesRead = 0;

        int currentProgress;

        public ProgressSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            long fullLength = mResponseBody.contentLength();
            if (bytesRead == -1) {
                totalBytesRead = fullLength;
            } else {
                totalBytesRead += bytesRead;
            }
            int progress = (int) (100f * totalBytesRead / fullLength);
            Log.d(TAG, "download progress is : " + progress);
            if (mListener != null && progress != currentProgress) {
                mListener.onProgress(progress);
            }
            if (mListener != null && totalBytesRead == fullLength) {
                mListener = null;
            }
            currentProgress = progress;
            return bytesRead;
        }
    }
}
