package com.bergmannsoft.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bergmannsoft.util.Utils;

import java.util.List;

/**
 * A basic Camera preview class
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private CameraPreviewListener listener;

    public interface CameraPreviewListener {
        public void onException(Exception e);
    }

    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera,
                         CameraPreviewListener listener) {
        super(context);
        mCamera = camera;
        this.listener = listener;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage(), e);
            listener.onException(e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        try {
            boolean sizeFound = false;
            Parameters params = mCamera.getParameters();
            List<Size> sizes = params.getSupportedPreviewSizes();
            Size bigger = null;
            for (Size size : sizes) {
                Utils.debugSize(size);
                if (bigger == null || bigger.width < size.width || bigger.height < bigger.height) {
                    bigger = size;
                }
            }
            params.setPictureSize(bigger.width, bigger.height);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview", e);
            listener.onException(e);
        }
    }
}