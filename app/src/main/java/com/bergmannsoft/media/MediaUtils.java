package com.bergmannsoft.media;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.bergmannsoft.util.Helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import br.com.participact.participactbrasil.R;

public class MediaUtils {

    private static final String TAG = MediaUtils.class.getSimpleName();

    public static File newAudioFile(Context context) {
        return createFile(getDir(context, "audio"), "m4a");
    }

    public static File newVideoFile(Context context) {
        return createFile(getDir(context, "video"), "mp4");
    }

    public static File newPhotoFile(Context context) {
        return createFile(getDir(context, "photos"), "jpg");
    }

    private static File getDir(Context context, String folder) {
        File dir = new File(
                String.format(
                        "%s/%s/%s/",
                        Helpers.getExternalStorageAbsolutePath(),
                        context.getString(R.string.app_name
                        ).replaceAll(" ", "_").toLowerCase(),
                        folder
                )
        );
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Error creating directory '" + dir.getAbsolutePath() + "'");
                return null;
            }
        }
        return dir;
    }

    private static File createFile(File dir, String extension) {
        if (dir == null) {
            return null;
        }
        File file = new File(dir, String.format("%s.%s", Helpers.getTimeStamp(), extension));
        if (file.exists()) {
            Log.i(TAG, "File exists. Deleting...");
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating file '" + file.getAbsolutePath() + "'");
            return null;
        }
        return file;
    }

    /**
     *
     * @param url file path or whatever suitable URL you want.
     * @return mime type
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static int duration(Context context, File file) {
        return duration(context, file.getAbsolutePath());
    }

    public static int duration(Context context, String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,Uri.parse(path));
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        return millSecond / 1000;
    }

//    public static Bitmap getVideoThumbnail(Uri uri, Activity a) {
//        ContentResolver cr = a.getContentResolver();
//
//        String[] projection = {BaseColumns._ID, MediaStore.MediaColumns.DATA};
//
//        Cursor cursor = cr.query(uri, projection, null, null, null);
//        Bitmap thumbnail = null;
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
//            int id = cursor.getInt(columnIndex);
//
//            ContentResolver crThumb = cr;
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 1;
//            thumbnail = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id,
//                    MediaStore.Video.Thumbnails.MICRO_KIND, options);
//        } else {
//            String filePath = Helpers.getFilePath(uri, a);
//            if (filePath == null) {
//                filePath = uri.toString();
//            }
//
//            thumbnail = ThumbnailUtils.createVideoThumbnail(filePath,
//                    MediaStore.Images.Thumbnails.MICRO_KIND);
//        }
//        return thumbnail;
//    }
//
//    public static String getFilePath(Uri uri, Context context) {
//        String[] projection = {MediaStore.Video.Media.DATA};
//        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//        String s = null;
//        if (cursor == null) {
//            s = uri.toString();
//        }
//        if (s != null && new File(s).exists()) {
//            return s;
//        }
//        if (cursor != null) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            s = cursor.getString(column_index);
//            cursor.close();
//        }
//        if (s == null || (s != null && !new File(s).exists())) {
//            InputStream is = null;
//            try {
//                is = context.getContentResolver().openInputStream(uri);
//                s = newFileName("v", Constant.MEDIA_TYPE_VIDEO);
//                saveStreamFile(is, s);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return e.toString();
//            } finally {
//                if (is != null) {
//                    try {
//                        is.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return s;
//    }

}
