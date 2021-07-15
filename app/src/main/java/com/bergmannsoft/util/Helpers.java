package com.bergmannsoft.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Surface;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helpers {

    private static final String TAG = Helpers.class.getName();

    public static String getFilePath(Uri uri, Context context, File output) {
        try {
            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            String s = null;
            if (cursor == null) {
                s = uri.toString();
            }
            if (s != null && new File(s).exists()) {
                return s;
            }
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                s = cursor.getString(column_index);
                cursor.close();
            }
            if (s == null || (s != null && !new File(s).exists())) {
                InputStream is = null;
                try {
                    is = context.getContentResolver().openInputStream(uri);
                    s = output.getAbsolutePath();
                    saveStreamFile(is, s);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.toString();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return s;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }
    }

    public static float dpToPixel(Context context, int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    public static void saveStreamFile(InputStream is, String path) throws IOException {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            byte[] buffer = new byte[1024 * 10];
            int read;
            while ((read = is.read(buffer, 0, buffer.length)) > 0) {
                fos.write(buffer, 0, read);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }



    public static boolean deleteFile(String path) {
        if (path != null) {
            Log.d(TAG, path);
            String cameraRoll = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            Log.d(TAG, cameraRoll);
            if (path.contains(cameraRoll)) {
                Log.d(TAG, "video is from camera - keep it");
                return true;
            }
            File f = new File(path);
            if (f.exists()) {
                f.delete();
                return true;
            }
        }
        return false;
    }

    public static String getExternalStorageAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getTimeStamp() {
        return getTimeStamp("yyyyMMdd_HHmmssS");
    }

    public static String getTimeStamp(String format) {
        String ts = new SimpleDateFormat(format, Locale.US).format(new Date());
        return ts;
    }

    // public static Bitmap crop(Bitmap src, int newWidth, int newHeight) {
    // if (src == null)
    // return null;
    // Log.d(TAG, "original size " + src.getWidth() + "x" + src.getHeight());
    // Log.d(TAG, "new size " + newWidth + "x" + newHeight);
    // int width = newWidth;
    // int height;
    // height = (int) (width * src.getHeight()) / src.getWidth();
    // int yOffset = 0;
    // // if (height != newHeight) {
    // // yOffset = (Math.max(src.getHeight(), newHeight) - Math.min(
    // // src.getHeight(), newHeight)) / 2;
    // // }
    // return Bitmap.createBitmap(src, 0, yOffset, width, newHeight);
    // }

    public static void debugSize(Size size) {
        Log.d(TAG, "size width: " + size.width + " height: " + size.height);
    }

    /**
     * Retrieve display metrics. displaymetrics.heightPixels and
     * displaymetrics.widthPixels shows display size.
     *
     * @param ctx
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplaySize(Context ctx) {
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;

    }

    public static int getHeightProportional(int originalWidth,
                                            int originalHeigt, int newWidth) {
        int newHeight = (int) (newWidth * originalHeigt) / originalWidth;
        return newHeight;
    }

    public static boolean moveFile(String from, String to) {
        File file = new File(from);
        return file.renameTo(new File(to));
    }

    public static boolean copyFile(String from, String to) {

        File source = new File(from);
        File dest = new File(to);

        InputStream is = null;
        OutputStream os = null;
        boolean copied = true;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception e) {
            copied = false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
            }
        }
        return copied;
    }

    public static void openDefaultMailClient(Context context, String toMail,
                                             String subject, String text) {
        toMail = toMail == null ? "" : toMail;
        String uri = "mailto:" + toMail + "?subject=" + subject + "&body="
                + text;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data = Uri.parse(uri);
        intent.setData(data);
        context.startActivity(intent);
    }

    public static String stringArrayToLine(String[] array) {
        String line = "";
        for (String item : array) {
            if (line.length() > 0) line += " ";
            line += item;
        }
        return line;
    }

    public static String getBuildNumber(Context context) {

        PackageInfo pInfo;

        try {

            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName + " build " + pInfo.versionCode;

            return version;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public static boolean isInPortrait(Activity activity) {
        int ori = getScreenOrientation(activity);
        return ori == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || ori == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public static void openDefaultMailClientForHtml(Context context, String toMail,
                                                    String subject, String text, String html) {
        if (toMail == null) {
            toMail = "";
        }
        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + toMail));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(text)
        );

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, html);
//        }

        context.startActivity(shareIntent);
    }

    public static void openDefaultMailClientForHtml2(Context context, String toMail,
                                                    String subject, String text, String html) {
        if (toMail == null) {
            toMail = "";
        }
        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + toMail));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(text + html)
        );

        context.startActivity(shareIntent);
    }

    public static void openDefaultSmsClient(Context context, String to, String text) {
        if (to == null) {
            to = "";
        }
        String uri = "smsto:" + to;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        intent.putExtra("sms_body", text);
        intent.putExtra("compose_mode", true);
        context.startActivity(intent);
    }

//    public static boolean fileExistsAtUri(Uri uri, Activity a) {
//        return fileExistsAtPath(Helpers.getFilePath(uri, a));
//    }

    public static boolean fileExistsAtPath(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static void clearAOVFolder() {
        String path = getExternalStorageAbsolutePath() + "/" + "aov"
                + "/";
        clearAOVFolder(path);
        Log.d(TAG, "clearAOVFolder done.");
    }

    private static void clearAOVFolder(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        clearAOVFolder(file.getPath());
                        Log.d(TAG, "Deleting folder: " + file.getName());
                        if (!file.getName().equals("aov"))
                            file.delete();
                    } else {
                        Log.d(TAG, "Deleting file: " + file.getName());
                        if (!file.getName().equals("vk.log"))
                            file.delete();
                    }
                }
            }
        }
    }

    public static void deleteDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getPath());
                    if (!file.getName().equals("aov"))
                        file.delete();
                } else {
                    if (!file.getName().equals("vk.log"))
                        file.delete();
                }
            }
        }
    }
}