package com.wl.android.glidetest4;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.wl.android.glidetest4.util.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    MyLayout mMyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mMyLayout = (MyLayout) findViewById(R.id.background);

    }

    SimpleTarget<GlideDrawable> mSimpleTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource,
                                    GlideAnimation<? super GlideDrawable> glideAnimation) {
            mImageView.setImageDrawable(resource);
        }
    };

    public void loadImage(View view) {

//        loadBingPic();

        // downloadOnly后可以快速加载
//        String url = "https://www.baidu.com/img/bd_logo1.png";
//        Glide.with(this)
//                .load(url)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(mImageView);

        // listener
        String url = "https://www.baidu.com/img/bd_logo1.png";
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(mImageView);
    }

    public void loadImage2(View view) {
        loadBingPic2(); // 预加载后调用
    }

    // downloadOnly(int width, int height)
    public void downLoadIamge(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://www.baidu.com/img/bd_logo1.png";
                    final Context context = getApplicationContext();
                    FutureTarget<File> target = Glide.with(context)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    final File imageFile = target.get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, imageFile.getPath(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    // downloadOnly(Y target)
    public void downLoadIamge2(View view) {
        String url = "https://www.baidu.com/img/bd_logo1.png";
        Glide.with(this)
                .load(url)
                .downloadOnly(new DownLoadImageTarget());
    }

    private void loadBingPic() {
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
//                        Glide.with(MainActivity.this)
//                                .load(bingPic)
//                                .into(mSimpleTarget);

//                        Glide.with(MainActivity.this)
//                                .load(bingPic)
//                                .into(mMyLayout.getTarget());

                        Glide.with(MainActivity.this)
                                .load(bingPic)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .preload(); // 不带参数预加载原始图片大小，DiskCacheStrategy.SOURCE结合使用
                    }
                });
            }
        });
    }

    private void loadBingPic2() {
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
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(mImageView);
                    }
                });
            }
        });
    }


}
