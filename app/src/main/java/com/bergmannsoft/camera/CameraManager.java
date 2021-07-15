package com.bergmannsoft.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bergmannsoft.util.Helpers;
import com.bergmannsoft.util.ImageUtils;
import com.bergmannsoft.util.StorageHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by fabiobergmann on 05/01/18.
 */

@SuppressWarnings("deprecation")
public class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();
    protected Camera mCamera;
    protected CameraPreview mPreview;
    protected MediaRecorder mMediaRecorder;

    File mFile;

    protected String log = "";
    protected String logActions = "";

    public interface BSCameraManagerListener {

        void willSavePhoto();

        void onPhotoSaved(File file);

        File nextPhotoFile();

        void onZoomChanged(int zoom);

        void onException(Exception e);
    }

    public enum FlashMode {

        on("on"),
        off("off"),
        auto("auto");

        private final String mode;

        FlashMode(final String mode) {
            this.mode = mode;
        }

        @Override
        public String toString() {
            return mode;
        }
    }

    Activity mContext;
    BSCameraManagerListener mListener;
    FlashMode mFlashMode;
    ViewGroup mPreviewView;

    public CameraManager(Activity context, FlashMode flashMode, ViewGroup preview, BSCameraManagerListener listener) {
        mContext = context;
        mListener = listener;
        mFlashMode = flashMode;
        mPreviewView = preview;
    }

    @SuppressWarnings("deprecation")
    protected void setupCamera() {

        if (mFile == null) {
            mFile = mListener.nextPhotoFile();
        }

        log = "";

        if (mCamera != null) {
            releaseCamera();
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        if (mCamera == null) {
            mListener.onException(new RuntimeException("Camera is null."));
            return;
        }

        logAction("Camera instance: " + mCamera);

        final Camera.Parameters params = mCamera.getParameters();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(mContext, mCamera,
                new CameraPreview.CameraPreviewListener() {

                    @Override
                    public void onException(Exception e) {
                        mListener.onException(e);
                    }
                });

        String flashMode = mFlashMode.toString();
        if (flashMode == null) {
            flashMode = params.getFlashMode();
        }
        try {
            params.setFlashMode(flashMode);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }

        // Focus
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes != null) {
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }

        mCamera.setParameters(params);

        FrameLayout preview = (FrameLayout) mPreviewView;
        preview.removeAllViews();
        preview.addView(mPreview);

    }

    public void takePicture() {
        try {
            mCamera.takePicture(null, null, mPicture);
            mCamera.stopPreview();
        } catch (Exception e) {
            Toast.makeText(mContext,
                    "Error trying to take a picture",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setFlashMode(FlashMode mFlashMode) {
        this.mFlashMode = mFlashMode;
        setFlash();
    }

    private void setFlash() {

        Camera.Parameters params = mCamera.getParameters();

        switch (mFlashMode) {
            case auto:
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case on:
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
            case off:
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
        }
        mCamera.setParameters(params);
    }


    private void logAction(String action) {
        logActions += action + "\n";
    }

    protected int currentZoomLevel;

    protected Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.willSavePhoto();
                }
            });

            try {
                FileOutputStream fos = new FileOutputStream(mFile);
                fos.write(data);
                fos.flush();
                fos.close();
                mFile = getWithProperSize();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onPhotoSaved(mFile);
                        mFile = mListener.nextPhotoFile();
                        if (mFile != null) {
                            mCamera.startPreview();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
//                Crashlytics.log(Log.ERROR, TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
//                Crashlytics.log(Log.ERROR, TAG, "Error accessing file: " + e.getMessage());
            }
        }

        private File getWithProperSize() {
            long t1 = System.currentTimeMillis();
            Bitmap bmp = ImageUtils.decodeFile(mFile, true);
            long diff = System.currentTimeMillis() - t1;
            if (bmp == null) {
                if (mFile == null) {
                    mListener.onException(new Exception("Error saving photo to a null file path."));
                } else {
                    mListener.onException(
                            new Exception(
                                    "Error saving photo to \"" + mFile.getAbsolutePath() + "\"." +
                                            "\nAvailable storage: " + StorageHelper.bytesToHuman(StorageHelper.getFreeExternalMemory()) +
                                            "\nFile exists: " + mFile.exists() +
                                            "\nFile size: " + StorageHelper.bytesToHuman(mFile.length()) +
                                            "\nTime processing: " + (diff / 1000) + " seconds"
                            )
                    );
                }
                return mFile;
            }
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            if (w > 1024 || h > 1024) {

                File resizedFile = new File(mFile.getAbsolutePath().replace(".jpg", "_res.jpg"));

                int nw = w > h ? 1024 : 768;
                int nh = w > h ? 768 : 1024;

                Bitmap resized = Bitmap.createScaledBitmap(bmp, nw, nh, true);
                bmp.recycle();
                try {
                    Helpers.deleteFile(mFile.getAbsolutePath());
                    FileOutputStream out = new FileOutputStream(resizedFile);
                    resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    resized.recycle();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!Helpers.moveFile(resizedFile.getAbsolutePath(), mFile.getAbsolutePath())) {
                    Log.d(TAG, "Error moving file.");
                    mListener.onException(new Exception("Error moving file."));
                }
            }
            return mFile;
        }

    };

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            mListener.onException(e);
        }
        return c; // returns null if camera is unavailable
    }

    public void onPause() {
        releaseMediaRecorder(); // if you are using MediaRecorder, release it first
        releaseCamera(); // release the camera immediately on pause event
    }

    public void onResume() {
        setupCamera();
    }

    protected void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset(); // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            if (mCamera != null) {
                mCamera.lock(); // lock camera for later use
            }
        }
    }

    protected void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    // region Zoom

    float mDist;

    public boolean onTouch(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                //handleFocus(event, params);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mListener.onZoomChanged(zoom);
        mCamera.setParameters(params);
    }

    /*public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }*/

    //Determine the space between the first two fingers
    @SuppressWarnings("deprecation")
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // endregion

}