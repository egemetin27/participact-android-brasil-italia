package com.bergmannsoft.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import br.com.participact.participactbrasil.R;

/**
 * Created by fabiobergmann on 1/17/16.
 */
public class Utils {

    private static final String TAG = "Utils";

    @SuppressWarnings("deprecation")
    public static boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copy", text);
                clipboard.setPrimaryClip(clip);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public static String getFilePath(Uri uri, Context context) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null,
                null, null);
        if (cursor == null)
            return uri.toString();
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static String getFileName(Uri uri, Context context) {
        String filename = null;
        try {
            String filepath = getFilePath(uri, context);
            filename = filepath.substring(filepath.lastIndexOf("/") + 1);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return filename;
    }

    public static String getCameraRollDir() {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (path.exists()) {
            File test1 = new File(path, "Camera/");
            if (test1.exists()) {
                path = test1;
            } else {
                File test2 = new File(path, "100ANDRO/");
                if (test2.exists()) {
                    path = test2;
                } else {
                    File test3 = new File(path, "100MEDIA/");
                    if (!test3.exists()) {
                        test3.mkdirs();
                    }
                    path = test3;
                }
            }
        } else {
            path = new File(path, "Camera/");
            path.mkdirs();
        }

        return path.getAbsolutePath();
    }

    public static String getMonthShortByNumber(Context context, int month) {
        try {
            switch (month) {
                case 1:
                    return context.getString(R.string.jan);
                case 2:
                    return context.getString(R.string.feb);
                case 3:
                    return context.getString(R.string.mar);
                case 4:
                    return context.getString(R.string.abr);
                case 5:
                    return context.getString(R.string.mai);
                case 6:
                    return context.getString(R.string.jun);
                case 7:
                    return context.getString(R.string.jul);
                case 8:
                    return context.getString(R.string.ago);
                case 9:
                    return context.getString(R.string.set);
                case 10:
                    return context.getString(R.string.out);
                case 11:
                    return context.getString(R.string.nov);
                case 12:
                    return context.getString(R.string.dez);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean saveToCameraRoll(String path, Context context) {
        String filename = getFileName(Uri.parse(path), context);
        String toPath = getCameraRollDir() + "/" + filename;
        return copyFile(path, toPath);
    }

    public static boolean saveToCameraRoll(Bitmap source, String title, String description, Context context) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(), source, title, description);
//        insertImage(source, title, description, context);
        return true;
    }

    public static final String insertImage(Bitmap source, String title, String description, Context context) {

        ContentResolver cr = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = getFilePath(url, context);
        }

        return stringUrl;
    }

    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
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

    // yyyy-MM-dd HHmmss

    public static String dateToString(String format, Date date) {
        String ts = new SimpleDateFormat(format, Locale.US).format(date);
        return ts;
    }

    public static Date stringToDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, null, e);

        }
        return null;
    }

    public static String getMonth(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Fev";
            case 2:
                return "Mar";
            case 3:
                return "Abr";
            case 4:
                return "Mai";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Ago";
            case 8:
                return "Set";
            case 9:
                return "Out";
            case 10:
                return "Nov";
            case 11:
                return "Dez";
        }
        return "";
    }

    public static String getMonthComplete(int month) {
        switch (month) {
            case 0:
                return "JANEIRO";
            case 1:
                return "FEVEREIRO";
            case 2:
                return "MARÇO";
            case 3:
                return "ABRIL";
            case 4:
                return "MAIO";
            case 5:
                return "JUNHO";
            case 6:
                return "JULHO";
            case 7:
                return "AGOSTO";
            case 8:
                return "SETEMBRO";
            case 9:
                return "OUTUBRO";
            case 10:
                return "NOVEMBRO";
            case 11:
                return "DEZEMBRO";
        }
        return "";
    }

    public static String getMonthFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getMonth(cal.get(Calendar.MONTH));
    }

    public static String getMonthFromString(String date, String format) {
        return getMonthFromDate(stringToDate(date, format));
    }

    public static String getDayAndMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthFromDate(dt);
    }

    public static String getDayAndMonthFromString(String date, String format) {
        Date dt = stringToDate(date, format);
        return getDayAndMonth(dt);
    }

    public static String getTimeFromString(String date, String format) {
        Date dt = stringToDate(date, format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return (hour < 10 ? "0" + hour : String.valueOf(hour)) + ":" + (min < 10 ? "0" + min : String.valueOf(min));
    }

    public static String getYear(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public static String getYearFromString(String date, String format) {
        Date dt = stringToDate(date, format);
        return getYear(dt);
    }

    public static boolean isBeforeOrToday(String date, String format) {
        Date dt = stringToDate(date, format);
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar os = new GregorianCalendar();
        os.setTime(dt);

        if (isLate(date, format)) {
            return true;
        } else if (now.get(GregorianCalendar.YEAR) == os.get(GregorianCalendar.YEAR)
                && now.get(GregorianCalendar.MONTH) == os.get(GregorianCalendar.MONTH)
                && now.get(GregorianCalendar.DAY_OF_MONTH) == os.get(GregorianCalendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static String getWhenFromString(String date, String format) {
        Date dt = stringToDate(date, format);
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar os = new GregorianCalendar();
        os.setTime(dt);

        if (isLate(date, format)) {
            return "HOJE";
        } else if (now.get(GregorianCalendar.YEAR) == os.get(GregorianCalendar.YEAR)
                && now.get(GregorianCalendar.MONTH) == os.get(GregorianCalendar.MONTH)
                && now.get(GregorianCalendar.DAY_OF_MONTH) == os.get(GregorianCalendar.DAY_OF_MONTH)) {
            return "HOJE";
        } else if (now.get(GregorianCalendar.YEAR) == os.get(GregorianCalendar.YEAR)
                && now.get(GregorianCalendar.MONTH) == os.get(GregorianCalendar.MONTH)
                && now.get(GregorianCalendar.DAY_OF_MONTH) + 1 == os.get(GregorianCalendar.DAY_OF_MONTH)) {
            return "AMANHÃ";
        } else {
            switch (os.get(GregorianCalendar.DAY_OF_WEEK)) {
                case 0:
                    return "DOMINGO";
                case 1:
                    return "SEGUNDA-FEIRA";
                case 2:
                    return "TERÇA-FEIRA";
                case 3:
                    return "QUARTA-FEIRA";
                case 4:
                    return "QUINTA-FEIRA";
                case 5:
                    return "SEXTA-FEIRA";
                case 6:
                    return "SÁBADO";
            }
        }
        return "";
    }

    public static boolean isToday(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month && cal.get(Calendar.DAY_OF_MONTH) == day) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(Date a, Date b) {
        GregorianCalendar aa = new GregorianCalendar();
        aa.setTime(a);
        GregorianCalendar bb = new GregorianCalendar();
        bb.setTime(b);

        return aa.get(GregorianCalendar.YEAR) == bb.get(GregorianCalendar.YEAR)
                && aa.get(GregorianCalendar.MONTH) == bb.get(GregorianCalendar.MONTH)
                && aa.get(GregorianCalendar.DAY_OF_MONTH) == bb.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public static boolean isTomorrowOrLate(String date, String format) {
        Date dt = stringToDate(date, format);
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar os = new GregorianCalendar();
        os.setTime(dt);
        return os.get(GregorianCalendar.YEAR) >= now.get(GregorianCalendar.YEAR)
                && os.get(GregorianCalendar.MONTH) >= now.get(GregorianCalendar.MONTH)
                && os.get(GregorianCalendar.DAY_OF_MONTH) > now.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public static boolean isLate(String date, String format) {
        Date dt = stringToDate(date, format);
        Date now = new Date();
        return (dt.getTime() - now.getTime()) < 0;
    }

    public static String getExternalStorageAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public static boolean requiresRestartForWritePermission() {
        String path = getExternalStorageAbsolutePath() + "/" + "call2action" + "/";
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                return true;
            }
        }
        String extension = ".jpg";

        File ff = new File(path, "call2action_" + getTimeStamp() + extension);
        try {
            ff.createNewFile();
            ff.delete();
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public static File newPhotoFile() {
        String path = getExternalStorageAbsolutePath() + "/" + "call2action" + "/";
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                return null;
            }
        }
        String extension = ".jpg";

        return new File(path, "call2action_" + getTimeStamp() + extension);
    }

    public static String getTimeStamp() {
        return getTimeStamp("yyyyMMdd_HHmmss");
    }

    public static String getTimeStamp(String format) {
        String ts = new SimpleDateFormat(format, Locale.US).format(new Date());
        return ts;
    }

    public static boolean hasCameraPermission(Context context) {
        return hasPermission(context, android.Manifest.permission.CAMERA);
    }

    public static boolean hasAudioPermission(Context context) {
        return hasPermission(context, android.Manifest.permission.RECORD_AUDIO);
    }

    public static boolean hasLocationPermission(Context context) {
        return hasPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean hasCallPermission(Context context) {
        return hasPermission(context, android.Manifest.permission.CALL_PHONE);
    }

    public static boolean hasAllNecessaryPermissions(Context context) {
        return Utils.hasCameraPermission(context) && Utils.hasLocationPermission(context) && Utils.hasWriteExternalStoragePermission(context) && Utils.hasAudioPermission(context)/* && Utils.hasCallPermission(context)*/;
    }

    public static boolean hasWriteExternalStoragePermission(Context context) {
        return hasPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private static boolean hasPermission(Context context, String perm) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(perm));
        else
            return true;
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default resume activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }

    public static boolean isValidNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    public final static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    public static List<String> getAllStates() {
        List<String> states = new ArrayList<>();
        states.add("AC");
        states.add("AL");
        states.add("AP");
        states.add("AM");
        states.add("BA");
        states.add("CE");
        states.add("DF");
        states.add("ES");
        states.add("GO");
        states.add("MA");
        states.add("MT");
        states.add("MS");
        states.add("MG");
        states.add("PA");
        states.add("PA");
        states.add("PR");
        states.add("PE");
        states.add("PI");
        states.add("RJ");
        states.add("RN");
        states.add("RS");
        states.add("RO");
        states.add("RR");
        states.add("SC");
        states.add("SP");
        states.add("SE");
        states.add("TO");
        return states;
    }

    public static void debugSize(Camera.Size size) {
        Log.d(TAG, "size width: " + size.width + " height: " + size.height);
    }

    public interface DownloadImageCallback {
        void onDownloadDone(Bitmap bitmap);
    }

    public static void downloadImage(final String url, final DownloadImageCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    if (url == null || url.length() == 0)
                        return null;

                    //from web
                    URL imageUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(3000); //seems to high
                    conn.setReadTimeout(3000);
                    conn.setInstanceFollowRedirects(true);
                    if (conn.getResponseCode() != 200) {
                        Log.e(TAG, "Invalid Link:" + url);
                        return null;
                    }

                    InputStream is = null;

                    is = conn.getInputStream();


                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565; //ARGB_8888;
                    //options.inSampleSize = 2;

                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                    //bitmap.recycle();//test this
                    if (bitmap == null) {
                        return null;
                    }
                    callback.onDownloadDone(bitmap);

                } catch (IOException e) {
                    Log.e(TAG, "url: " + url, e);
                } catch (Exception e) {
                    Log.e("FileCachey", null, e);
                }
                return null;
            }
        }.execute();
    }

    public static void setBackgroundDrawable(Context context, View view, int id) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(context.getResources().getDrawable(id));
        } else {
            view.setBackgroundDrawable(context.getResources().getDrawable(id));
        }
    }

    public static String removeAccents(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }

    @SuppressWarnings("deprecation")
    public static boolean isSize16x9(Camera.Size size) {
        double height = size.height;
        double result = (height * 1.7777778);
        int expectedWidth = (int) result;
        int width = size.width;
        return width == expectedWidth;
    }
}
