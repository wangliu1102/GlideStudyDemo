package com.wl.android.glidetest;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wl.android.glidetest.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    // 图片缓存
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_view);

        // 将任意一张图片压缩成100*100的缩略图，并在ImageView上展示
//        mImageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.loading, 100, 100));

        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("TAG", "Max memory is " + maxMemory + "KB");
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitMapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitMapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private void loadBitmap(int resId, ImageView imageView) {
        String imageKey = String.valueOf(resId);
        Bitmap bitmap = getBitMapFromMemoryCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.loading);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private ImageView view;

        public BitmapWorkerTask(ImageView imageView) {
            this.view = imageView;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), params[0], 100, 100);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            view.setImageBitmap(bitmap);
        }
    }


    //通过设置BitmapFactory.Options中inSampleSize的值就可以实现图片的压缩
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的宽和高
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比例
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = widthRatio > heightRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources resources, int resId,
                                                  int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    public void loadImage(View view) {

        loadBitmap(R.drawable.loading, mImageView);

//        lodeBingPic();

        // gif
//        loadGif();
    }

    private void loadGif() {
        String url = "http://p1.pstatp.com/large/166200019850062839d3";
        Glide.with(this)
                .load(url)
//                .asBitmap() // 只允许加载静态图片，不需要Glide去帮我们自动进行图片格式的判断
                .asGif() // 加载动态图片
                .placeholder(R.drawable.loading) // 占位图
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用掉Glide的缓存功能
                .override(100, 100) // 将图片加载成100*100像素的尺寸
                .into(mImageView);
    }

    private void lodeBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this)
                                .load(bingPic)
//                                .asGif() // 静态图片使用asGif()加载会加载失败
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.error)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .override(100, 100) // 将图片加载成100*100像素的尺寸
                                .into(mImageView);
                    }
                });
            }
        });
    }
}
