package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bergmannsoft.dialog.BProgressDialog;
import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.media.MediaUtils;
import com.bergmannsoft.support.ImagePicker;
import com.bergmannsoft.util.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.BuildConfig;
import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.activities.BaseActivity;

import static android.app.Activity.RESULT_OK;

public class ReportRecordPhotosDialog extends ColoredDialog implements View.OnClickListener {

    private List<File> files = new ArrayList<>();

    private ImagePicker imagePicker;

    public interface OnReportRecordPhotosDialogListener {
        void onDone(List<File> files);
    }

    private OnReportRecordPhotosDialogListener mListener;

    public ReportRecordPhotosDialog(Context context, List<File> files, OnReportRecordPhotosDialogListener listener) {
        super(context, null, R.layout.dialog_record_photos);

        this.files = files;
        mListener = listener;

        imagePicker = new ImagePicker(getActivity());

        view.findViewById(R.id.buttonAction).setOnClickListener(this);
        view.findViewById(R.id.buttonDone).setOnClickListener(this);

        view.findViewById(R.id.photo1).setOnClickListener(this);
        view.findViewById(R.id.photo2).setOnClickListener(this);
        view.findViewById(R.id.photo3).setOnClickListener(this);
        view.findViewById(R.id.photo4).setOnClickListener(this);

        updateUI();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAction:
                if (files.size() < 4) {
//                    File file = MediaUtils.newPhotoFile(context);
//                    if (file != null) {
//                        files.add(file);
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (intent.resolveActivity(context.getPackageManager()) != null) {
//                            Uri fileUri = Uri.fromFile(file);
//                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                fileUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
//                            }
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                            ((BaseActivity) context).startActivityForResult(intent, 1001);
//                        }
//                    }

                    imagePicker.pickUp(new ImagePicker.ImagePickerListener() {
                        @Override
                        public void onImage(File imageFile) {
                            BProgressDialog.hideProgressBar();
                            files.add(imageFile);
                            updateUI();
                        }

                        @Override
                        public void onCancel() {
                            BProgressDialog.hideProgressBar();
                        }

                        @Override
                        public void onProcessing() {
                            BProgressDialog.showProgressBar(activity, false);
                        }
                    });
                }
                break;
            case R.id.buttonDone:
                mListener.onDone(files);
                dismiss();
                break;
            case R.id.photo1:
                if (files.size() > 0) {
                    showPreview(files.get(0));
                }
                break;
            case R.id.photo2:
                if (files.size() > 1) {
                    showPreview(files.get(1));
                }
                break;
            case R.id.photo3:
                if (files.size() > 2) {
                    showPreview(files.get(2));
                }
                break;
            case R.id.photo4:
                if (files.size() > 3) {
                    showPreview(files.get(3));
                }
                break;
        }
    }

    private void showPreview(File file) {
        new ReportRecordPhotosPreviewDialog(context, file, new ReportRecordPhotosPreviewDialog.OnReportRecordPhotosPreviewDialogListener() {
            @Override
            public void onDelete(File file) {
                file.delete();
                files.remove(file);
                updateUI();
            }
        }).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == 1001) {
//            File file = files.get(files.size() - 1);
//            if (file.exists()) {
//                Bitmap bmp = ImageUtils.decodeFile(file, true);
//                Bitmap resized = ImageUtils.scale(bmp, 1024);
//                file.delete();
//                ImageUtils.saveBitmap(resized, file.getAbsolutePath());
//                bmp.recycle();
//                updateUI();
//            }
//        }
        imagePicker.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI() {
        int[] photos = new int[] {R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4};
        int i = 0;
        for (int photoId : photos) {
            ImageView imageView = view.findViewById(photoId);
            Bitmap bmp = null;
            if (i < files.size()) {
                bmp = ImageUtils.decodeFile(files.get(i), true);
            }
            if (bmp != null) {
                int w = bmp.getWidth();
                int h = bmp.getHeight();
                Log.d(TAG, "updateUI bmp w: " + w + ", h: " + h);
            }
            imageView.setImageBitmap(bmp);
            i++;
        }
    }

}
