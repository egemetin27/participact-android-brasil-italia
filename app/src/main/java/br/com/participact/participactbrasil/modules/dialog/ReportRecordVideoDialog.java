package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.media.MediaUtils;
import com.bergmannsoft.util.Helpers;

import java.io.File;
import java.util.Arrays;

import br.com.participact.participactbrasil.BuildConfig;
import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.activities.BaseActivity;

import static android.app.Activity.RESULT_OK;

public class ReportRecordVideoDialog extends ColoredDialog implements View.OnClickListener {

    private File mFile;

    private Button action;
    private Button use;

    private boolean isVideoFromGallery = false;

    public interface OnReportRecordVideoDialogListener {
        void onUse(File file);

        void onDelete();
    }

    private OnReportRecordVideoDialogListener mListener;

    public ReportRecordVideoDialog(Context context, File file, OnReportRecordVideoDialogListener listener) {
        super(context, null, R.layout.dialog_record_video);

        mFile = file;
        mListener = listener;

        action = view.findViewById(R.id.buttonAction);
        use = view.findViewById(R.id.buttonUse);

        action.setText("ADICIONAR");
        action.setTextColor(Color.parseColor("#0096FF"));

        if (mFile != null && mFile.exists()) {
            action.setText("EXCLUIR");
            action.setTextColor(Color.parseColor("#FF2600"));
        }

        view.findViewById(R.id.buttonAction).setOnClickListener(this);
        view.findViewById(R.id.buttonDone).setOnClickListener(this);
        view.findViewById(R.id.buttonPlay).setOnClickListener(this);

        updateUI();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAction:
                if (mFile != null && mFile.exists()) {
                    if (!isVideoFromGallery) {
                        mFile.delete();
                    }
                    mFile = null;
                    mListener.onDelete();
                    dismiss();
                } else {
                    mFile = MediaUtils.newVideoFile(context);
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(context.getPackageManager()) != null) {

                        AlertDialogUtils.showOptionsDialog(getActivity(), activity.getString(R.string.image_picker_dialog_title), Arrays.asList(
                                activity.getString(R.string.image_picker_option_camera),
                                activity.getString(R.string.image_picker_option_videos)
                        ), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                                    Uri fileUri = Uri.fromFile(mFile);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        fileUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", mFile);
                                    }
                                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                    ((BaseActivity) context).startActivityForResult(takeVideoIntent, 1000);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("video/*");
                                    ((BaseActivity) context).startActivityForResult(intent, 1001);
                                }
                            }
                        });


                    }
                }
                break;
            case R.id.buttonDone:
                mListener.onUse(mFile);
                dismiss();
                break;
            case R.id.buttonPlay:
                if (mFile != null && mFile.exists()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mFile.getAbsolutePath()));
                    intent.setDataAndType(Uri.parse(mFile.getAbsolutePath()), "video/mp4");
                    context.startActivity(intent);
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                isVideoFromGallery = false;
                if (mFile != null && mFile.exists()) {
                    Log.d(TAG, "Video file: " + mFile.length());
                    action.setText("EXCLUIR");
                    action.setTextColor(Color.parseColor("#FF2600"));
                }
            } else if (requestCode == 1001) {
                isVideoFromGallery = true;
                Uri fileUri = data.getData();
                mFile = MediaUtils.newVideoFile(context);
                String filePath = Helpers.getFilePath(fileUri, getActivity(), mFile);
                if (!filePath.equalsIgnoreCase(mFile.getAbsolutePath())) {
                    mFile.delete();
                    mFile = new File(filePath);
                }
                action.setText("EXCLUIR");
                action.setTextColor(Color.parseColor("#FF2600"));
            }
            updateUI();
        }
    }

    private void updateUI() {
        if (mFile != null && mFile.exists()) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            ImageButton imageButton = view.findViewById(R.id.buttonPlay);
            imageButton.setImageBitmap(thumb);
        }
    }

}
