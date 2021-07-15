package com.bergmannsoft.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by fabiobergmann on 11/01/17.
 */

public class ImageUtils {

    public static String imageToBase64(File file) {
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bmp == null)
            return "";
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w > 1280) {

            int nw = w > h ? 1280 : 720;
            int nh = w > h ? 720 : 1280;

            Bitmap resized = Bitmap.createScaledBitmap(bmp, nw, nh, true);
            bmp.recycle();
            bmp = resized;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp.recycle();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    public static Bitmap base64ToImage(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static Bitmap squared(Bitmap src, int size) {
        int w = src.getWidth();
        int h = src.getHeight();
        int ss = size;

        double ratio = ((double)w) / ((double)h);

        int rh = size;
        int rw = (int)(rh * ratio);
        if (src.getHeight() > src.getWidth()) {
            rw = size;
            rh = (int)(rw / ratio);
        }
        Bitmap resized = resize(src, rw, rh);

        w = rw;
        h = rh;

        int offsetX = w > h ? ((w - ss) / 2) : 0;
        int offsetY = h > w ? ((h - ss) / 2) : 0;

        return Bitmap.createBitmap(resized, offsetX, offsetY, ss, ss);
    }

    public static Bitmap scale(Bitmap src, int size) {
        if (src == null) {
            new InvalidParameterException("bitmap is null");
        }
        double ratio = ((double)src.getWidth()) / ((double)src.getHeight());
        int w = size;
        int h = (int)(size / ratio);
        if (ratio < 1) {
            // is portrait
            h = size;
            w = (int)(size * ratio);
        }
        return resize(src, w, h);
    }

    public static Bitmap resize(Bitmap src, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(src, newWidth, newHeight, false);
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Bitmap decodeFile(String path) {
        return decodeFile(new File(path), false);
    }

    public static Bitmap decodeFile(File file) {
        return decodeFile(file, false);
    }

    public static Bitmap decodeFile(String path, boolean wait) {
        return decodeFile(new File(path), wait);
    }

    public static Bitmap decodeFile(File file, boolean wait) {
        if (!wait && (file == null || !file.exists())) {
            return null;
        }
        if (file == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        String path = file.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        if (bmp == null && wait) {
            int times = 0;
            while ((bmp = BitmapFactory.decodeFile(path, options)) == null) {
                if (times > 32) {
                    break;
                }
                times++;
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }

    public static boolean saveBitmap(Bitmap src, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            src.compress(Bitmap.CompressFormat.PNG, 90, out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
