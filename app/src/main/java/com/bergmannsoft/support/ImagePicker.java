package com.bergmannsoft.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.util.ImageUtils;
import com.bergmannsoft.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import br.com.participact.participactbrasil.BuildConfig;
import br.com.participact.participactbrasil.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fabiobergmann on 11/01/17.
 */

public class ImagePicker {

    public static final int OPTION_CAMERA = 0;
    public static final int OPTION_PHOTOS = 1;
    private static final int CAMERA_REQUEST_CODE = 2000;
    private static final int PHOTOS_REQUEST_CODE = 2001;
    private int squaredSize;

    public interface ImagePickerListener {
        void onImage(File imageFile);

        void onCancel();

        void onProcessing();
    }

    private static ImagePicker instance;
    private Activity activity;

    private File imageFile;

    private ImagePickerListener listener;

    public ImagePicker(Activity activity) {
        this.activity = activity;
        this.squaredSize = -1;
    }

//    public static ImagePicker getInstance(Activity activity) {
//        if (instance == null) {
//            instance = new ImagePicker(activity);
//        }
//        return instance;
//    }

    public void pickUpSquared(ImagePickerListener listener, int size) {
        this.squaredSize = size;
        pickUp(listener);
    }

    public void pickUp(ImagePickerListener listener) {
        this.listener = listener;
        AlertDialogUtils.showOptionsDialog(
                activity,
                activity.getString(R.string.image_picker_dialog_title),
                Arrays.asList(
                        activity.getString(R.string.image_picker_option_camera),
                        activity.getString(R.string.image_picker_option_photos)
                ),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case OPTION_CAMERA:
                                openCamera();
                                break;
                            case OPTION_PHOTOS:
                                pickUpPhoto();
                                break;
                        }
                    }
                }
        );
    }

    private void openCamera() {
        imageFile = Utils.newPhotoFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = Uri.fromFile(imageFile);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoURI = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void pickUpPhoto() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        activity.startActivityForResult(
//                Intent.createChooser(
//                        intent,
//                        "Selecione"
//                ),
//                PHOTOS_REQUEST_CODE
//        );
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        activity.startActivityForResult(intent, PHOTOS_REQUEST_CODE);
    }

    public boolean isImagePickerCode(int requestCode) {
        return requestCode == CAMERA_REQUEST_CODE || requestCode == PHOTOS_REQUEST_CODE;
    }

    @SuppressLint("StaticFieldLeak")
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        if (resultCode == RESULT_OK && (requestCode == CAMERA_REQUEST_CODE || requestCode == PHOTOS_REQUEST_CODE)) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onProcessing();
                    }
                }
            });

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {

                    String path = imageFile != null ? imageFile.getAbsolutePath() : null;

                    if (path == null) {
                        if (data == null || data.getData() == null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.onImage(null);
                                    }
                                }
                            });
                            imageFile = null;
                            return null;
                        }
                        path = Utils.getFilePath(data.getData(), activity.getApplicationContext());
                        if (path != null)
                            imageFile = new File(path);
                    }

                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;

                    Bitmap bmp = BitmapFactory.decodeFile(path, options);
                    if (bmp == null) {
                        if (data == null || data.getData() == null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.onImage(null);
                                    }
                                }
                            });
                            imageFile = null;
                            return null;
                        }
                        InputStream is = null;
                        try {
                            is = activity.getContentResolver().openInputStream(data.getData());
                            bmp = BitmapFactory.decodeStream(is);
                            path = Utils.newPhotoFile().getAbsolutePath();
                            imageFile = new File(path);
                            Utils.saveBitmap(bmp, path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return null;
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

                    bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                    if (bmp.getWidth() > 1024 || bmp.getHeight() > 1024) {
                        Bitmap resized = ImageUtils.scale(bmp, 1024);
                        bmp.recycle();
                        imageFile.delete();
                        imageFile = Utils.newPhotoFile();
                        ImageUtils.saveBitmap(resized, imageFile.getAbsolutePath());
                    }


                    int rotation = 0;

                    if (exif != null) {

                        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                            rotation = 90;
                        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                            rotation = 270;
                        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                            rotation = 180;
                        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")) {
                            rotation = 90;
                        }

                    }

                    if (squaredSize > 0) {
                        bmp = ImageUtils.decodeFile(imageFile);
                        Bitmap squared = ImageUtils.squared(bmp, squaredSize);
                        bmp.recycle();
                        bmp = squared;
                        if (rotation != 0) {
                            Bitmap rotated = ImageUtils.rotate(bmp, rotation);
                            bmp.recycle();
                            bmp = rotated;
                        }
                        imageFile.delete();
                        imageFile = Utils.newPhotoFile();
                        ImageUtils.saveBitmap(bmp, imageFile.getAbsolutePath());
                        squaredSize = -1;
                    } else if (rotation != 0) {
                        bmp = ImageUtils.decodeFile(imageFile);
                        Bitmap rotated = ImageUtils.rotate(bmp, rotation);
                        bmp.recycle();
                        bmp = rotated;
                        imageFile.delete();
                        imageFile = Utils.newPhotoFile();
                        ImageUtils.saveBitmap(bmp, imageFile.getAbsolutePath());
                    }


                    switch (requestCode) {
                        case CAMERA_REQUEST_CODE:
                        case PHOTOS_REQUEST_CODE:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.onImage(imageFile);
                                    }
                                    imageFile = null;
                                }
                            });
                            break;
                    }

                    return null;
                }
            }.execute();


        } else {
            if (imageFile != null && imageFile.exists())
                imageFile.delete();
            if (listener != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCancel();
                    }
                });
            imageFile = null;
            squaredSize = -1;
        }

    }

}
