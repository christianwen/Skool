package com.example.tiena.amsconnection.helperclass;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by tiena on 11/09/2017.
 */

public class BitmapHelper {
    public static Bitmap drawTextAndTint(Bitmap bitmap, String text){

        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(48);
        paint.setTextSize(90);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        Paint paint_tint = new Paint();
        paint_tint.setARGB(100,0,0,0);

        canvas.drawPaint(paint_tint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText(text, (bitmap.getWidth()-90)/2, (bitmap.getHeight()+60)/2, paint);
        return bitmap;
    }
}
