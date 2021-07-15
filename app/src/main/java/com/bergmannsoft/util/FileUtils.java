package com.bergmannsoft.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by fabiobergmann on 17/01/17.
 */

public class FileUtils {

    @Nullable
    public static String fileToBase64(File file) {
        byte[] b = loadFileData(file);
        if (b != null) {
            String encodedFile = Base64.encodeToString(b, Base64.DEFAULT);
            return encodedFile;
        }
        return null;
    }

//    public static File base64ToFile(String base64) {
//        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
//    }

    public static byte[] loadFileData(File file) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 10];
            baos = new ByteArrayOutputStream();
            int read;
            while ((read = fis.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean isImage(String filePath) {
        return filePath != null && (filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg") || filePath.toLowerCase().endsWith(".bmp"));
    }

    public static boolean isPDF(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(".pdf");
    }

    public static File saveFileContent(Uri uri, ContentResolver resolver) {
        InputStream in = null;
        FileOutputStream fos = null;
        try {
            String mimeType = resolver.getType(uri);
            Log.v("FileUtils", mimeType);
            Cursor returnCursor = resolver.query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            File file = newFile(name, "." + mimeType.substring(mimeType.lastIndexOf("/") + 1));
            fos = new FileOutputStream(file);
            in = resolver.openInputStream(uri);
            byte[] buffer = new byte[1024 * 100];
            int read;
            while ((read = in.read(buffer, 0, buffer.length)) > 0) {
                fos.write(buffer, 0, read);
            }
            fos.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File getCloudDir() {
        String path = Utils.getExternalStorageAbsolutePath() + "/" + "cloud" + "/";
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                return null;
            }
        }

        return f;
    }

    public static File newFile(String name, String extension) {
        File file = new File(getCloudDir(), name + extension);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
