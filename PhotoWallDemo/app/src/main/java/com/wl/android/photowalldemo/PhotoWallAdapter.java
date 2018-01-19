package com.wl.android.photowalldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by D22397 on 2018/1/2.
 */

public class PhotoWallAdapter extends ArrayAdapter<String> implements AbsListView.OnScrollListener {

    // 记录所有正在下载或等待下载的任务
    private Set<BitmapWorkerTask> mTaskCollection;

    // 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
    private LruCache<String, Bitmap> mMemoryCache;

    // GridView 的实例
    private GridView mPhotoWall;

    // 第一张可见图片的下标
    private int mFristVisibleItem;

    // 屏幕一页有多少张图片可见
    private int mVisibleItemCount;

    // 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题
    private boolean mIsFirstEnter = true;

    public PhotoWallAdapter(Context context, int textViewResourceId, String[] objects, GridView photoWall) {
        super(context, textViewResourceId, objects);
        mPhotoWall = photoWall;
        mTaskCollection = new HashSet<>();

        // 获取应用程序最大可用内存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // 单位：KB
        // 使用最大可用内存值的1/8作为缓存的大小
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        mPhotoWall.setOnScrollListener(this); // 滚动监听
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String url = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout, parent, false);
        } else {
            view = convertView;
        }
        ImageView photoImage = view.findViewById(R.id.photo);
        // 给ImageView设置一个Tag，保证异步加载图片时不会乱序
        photoImage.setTag(url);
        setImageView(url, photoImage);
        return view;
    }

    /*********************滚动监听************************/

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
        if (scrollState == SCROLL_STATE_IDLE) {
            loadBitmaps(mFristVisibleItem, mVisibleItemCount);
        } else {
            cancleAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFristVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
        // 因此在这里为首次进入程序开启下载任务。
        if (mIsFirstEnter && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            mIsFirstEnter = false;
        }
    }

    /**********************滚动监听***********************/


    /**
     * 取消所有正在下载或等待下载的任务。
     */
    public void cancleAllTasks() {
        if (mTaskCollection != null) {
            for (BitmapWorkerTask task : mTaskCollection) {
                task.cancel(true);
            }
        }
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片
     *
     * @param fristVisibleItem 第一个可见的ImageView的下标
     * @param visibleItemCount 屏幕中总共可见的元素数
     */
    private void loadBitmaps(int fristVisibleItem, int visibleItemCount) {
        for (int i = fristVisibleItem; i < fristVisibleItem + visibleItemCount; i++) {
            String imageUrl = Images.imageThumbUrls[i];
            Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
            if (bitmap == null) {
                BitmapWorkerTask task = new BitmapWorkerTask();
                mTaskCollection.add(task);
                task.execute(imageUrl);
            } else {
                ImageView imageView = mPhotoWall.findViewWithTag(imageUrl);
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
     * 就给ImageView设置一张默认图片
     *
     * @param url       图片的URL地址，用于作为LruCache的键
     * @param imageView 用于显示图片的控件
     */
    private void setImageView(String url, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(url);
        if ((bitmap != null)) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.loading);
        }
    }

    /**
     * 将一张图片存储到LruCache中
     *
     * @param key    LruCache的键，这里传入图片的URL地址
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null
     *
     * @param key LruCache的键，这里传入图片的URL地址
     * @return 对应传入键的Bitmap对象，或者null
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 异步下载图片的任务
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        // 图片的URL地址
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            // 在后台开始下载图片
            Bitmap bitmap = downloadBitMap(imageUrl);

            if (bitmap != null) {
                //  // 图片下载完成后缓存到LrcCache中
                addBitmapToMemoryCache(imageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来
            ImageView imageView = mPhotoWall.findViewWithTag(imageUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTaskCollection.remove(this);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象
         *
         * @param imageUrl 图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private Bitmap downloadBitMap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(imageUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                if (conn != null) {
//                    conn.disconnect();
//                }
            }
            return bitmap;
        }

    }
}
