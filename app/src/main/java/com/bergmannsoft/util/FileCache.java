package com.bergmannsoft.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by fabiobergmann on 10/7/15.
 */
public class FileCache {

    private static final String TAG = "FileCache";
    private DiskLruCache mDiskCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 10MB
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mCompressQuality = 100;

    public FileCache(Context context) {
        Log.d("CATCHCACHE", "FileCache " + new Date(System.currentTimeMillis()));
        try {
            final File diskCacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE);
            Log.d("CATCHCACHE", "FileCache2 " + new Date(System.currentTimeMillis()));
        } catch (IOException e) {
            Log.e(TAG, null, e);
        }
    }

    public interface OnBitmapDownloadedListener {
        void onDownloaded(Bitmap bmp);
    }

    public interface FileCacheCallback {
        void onDownloadDone(String key, Bitmap bmp);
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        Log.d("CATCHCACHE", "getDiskCacheDir " + new Date(System.currentTimeMillis()));
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();
        Log.d("CATCHCACHE", "getDiskCacheDir2 " + new Date(System.currentTimeMillis()));
        return new File(cachePath + File.separator + uniqueName);
    }

    public Bitmap getBitmap(String key) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                {
                    final BufferedInputStream buffIn =
                            new BufferedInputStream(in);


                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    //options.
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    bitmap = BitmapFactory.decodeStream(buffIn, null, options);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, null, e);
        } catch (Exception e) {
            Log.e("FileCachex", null, e);
            //throw e; disable throw
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

//        if (BuildConfig.DEBUG) {
//            Log.d("cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
//        }

        return bitmap;

    }

    @SuppressLint("StaticFieldLeak")
    public void downloadAsync(final String key, final String url, final FileCacheCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    if (url == null || url.length() == 0) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDownloadDone(key, null);
                            }
                        });
                        return null;
                    }

                    Log.d("CATCHCACHE", "downloadAsync " + new Date(System.currentTimeMillis()));
                    //from web
                    URL imageUrl = new URL(url);
                    Log.d("CATCHCACHE", "downloadAsync1 " + new Date(System.currentTimeMillis()));
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(3000); //seems to high
                    conn.setReadTimeout(3000);
                    conn.setInstanceFollowRedirects(true);
                    if (conn.getResponseCode() != 200) {
                        Log.e(TAG, "Invalid Link:" + url);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDownloadDone(key, null);
                            }
                        });
                        return null;
                    }

                    Log.d("CATCHCACHE", "downloadAsync2 " + conn.getResponseCode() + new Date(System.currentTimeMillis()));
                    InputStream is = null;

                    is = conn.getInputStream();


                    // Log.d("CATCHCACHE", "downloadAsync3 " + new Date(System.currentTimeMillis()));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565; //ARGB_8888;
//                    options.inSampleSize = 2;

                    final Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                    //bitmap.recycle();//test this
                    if (bitmap == null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDownloadDone(key, null);
                            }
                        });
                        return null;
                    }
                    put(key, bitmap);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDownloadDone(key, bitmap);
                        }
                    });
                    //  Log.d("CATCHCACHE", "downloadAsync4 " + new Date(System.currentTimeMillis()));

                } catch (IOException e) {
                    Log.e(TAG, "url: " + url, e);
                } catch (Exception e) {
                    Log.e("FileCachey", null, e);
                }
                return null;
            }
        }.execute();
    }

    public void downloadVideoThumbAsync(final String key, final String url, final String vimeo, final FileCacheCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(vimeo == null ? url : vimeo, MediaStore.Video.Thumbnails.MINI_KIND);
                    //bitmap.recycle();//test this
                    if (bitmap == null) {
                        return null;
                    }
                    put(key, bitmap);
                    callback.onDownloadDone(key, bitmap);
                    //  Log.d("CATCHCACHE", "downloadAsync4 " + new Date(System.currentTimeMillis()));

                } catch (Exception e) {
                    Log.e(TAG, null, e);
                }
                return null;
            }
        }.execute();
    }

    public void put(String key, Bitmap data) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskCache.flush();
                editor.commit();
//                if (BuildConfig.DEBUG) {
//                    Log.d("cache_test_DISK_", "image put on disk cache " + key);
//                }
            } else {
                editor.abort();
//                if (BuildConfig.DEBUG) {
//                    Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
//                }
            }
        } catch (IOException e) {
//            if (BuildConfig.DEBUG) {
//                Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
//            }
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0));
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public boolean containsKey(String key) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            contained = snapshot != null;
        } catch (IOException e) {
            Log.e(TAG, null, e);
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
//        if (BuildConfig.DEBUG) {
//            Log.d("cache_test_DISK_", "disk cache CLEARED");
//        }
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            Log.e(TAG, null, e);
        }
    }

    public void remove(String key) {
        try {
            mDiskCache.remove(key);
        } catch (IOException e) {
            Log.e(TAG, null, e);
        }
    }

}