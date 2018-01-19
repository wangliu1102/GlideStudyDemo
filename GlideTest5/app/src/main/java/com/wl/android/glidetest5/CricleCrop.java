package com.wl.android.glidetest5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by D22397 on 2018/1/4.
 */

public class CricleCrop extends BitmapTransformation {

    public CricleCrop(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int diameter = Math.min(toTransform.getWidth(),toTransform.getHeight());

        Bitmap toReuse = pool.get(outWidth,outHeight, Bitmap.Config.ARGB_8888);
        Bitmap result;
        if(toReuse != null){
            result = toReuse;
        }else {
            result = Bitmap.createBitmap(diameter,diameter, Bitmap.Config.ARGB_8888);
        }

        int dx = (toTransform.getWidth() - diameter) /2;
        int dy = (toTransform.getHeight() - diameter) /2;
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(toTransform,BitmapShader.TileMode.CLAMP,BitmapShader.TileMode.CLAMP);
        if(dx != 0 || dy != 0){
            Matrix matrix = new Matrix();
            matrix.setTranslate(-dx,-dy);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float radius = diameter /2f;
        canvas.drawCircle(radius,radius,radius,paint);

        if (toReuse != null && !pool.put(toReuse)){
            toReuse.recycle();
        }
        return result;
    }

    @Override
    public String getId() {
        return "com.wl.android.glidetest5.CricleCrop";
    }
}
