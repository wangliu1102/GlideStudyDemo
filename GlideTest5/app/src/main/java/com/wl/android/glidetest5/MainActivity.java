package com.wl.android.glidetest5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_view);
        Log.d("TAG", "ImageView scaletype is : " + mImageView.getScaleType());
    }

    public void loadImage(View view) {
        String url = "https://www.baidu.com/img/bd_logo1.png";
//        Glide.with(this)
//                .load(url)
////                .dontTransform() //不进行图片变换,调用这个方法之后，所有的图片变换操作就全部失效了，
//                // 加载时调用override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)就可以恢复其他图片变换了
//                .override(500,500)
//                .centerCrop() //对原图的中心区域进行裁剪,配合override()方法可以实现更加丰富的效果，比如指定图片裁剪的比例
//                .into(mImageView);

//        Glide.with(this)
//                .load(url)
//                .transform(new CricleCrop(this)) // 自定义图片变换，继承自BitmapTransformation
//                .into(mImageView);

        // 使用图片变换开源库
        Glide.with(this)
                .load(url)
//                .bitmapTransform(new BlurTransformation(this)) //图片模糊
//                .bitmapTransform(new GrayscaleTransformation(this)) // 图片黑白化
                .into(mImageView);

    }
}
