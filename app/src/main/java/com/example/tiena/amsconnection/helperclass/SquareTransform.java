package com.example.tiena.amsconnection.helperclass;

/**
 * Created by tiena on 11/09/2017.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class SquareTransform implements Transformation {
    int hiddenImageCount=0;
    public SquareTransform(int hiddenImageCount){
        this.hiddenImageCount = hiddenImageCount;
    }

    public SquareTransform(){

    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        if(hiddenImageCount>0){
            return BitmapHelper.drawTextAndTint(squaredBitmap,"+"+hiddenImageCount);
        }
        else {
            return squaredBitmap;
        }
    }



    @Override
    public String key() {
        return "square";
    }
}