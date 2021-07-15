package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.util.ImageUtils;

import java.io.File;

import br.com.participact.participactbrasil.R;

public class ReportRecordPhotosPreviewDialog extends ColoredDialog {

    public interface OnReportRecordPhotosPreviewDialogListener {
        void onDelete(File file);
    }

    private OnReportRecordPhotosPreviewDialogListener mListener;

    public ReportRecordPhotosPreviewDialog(Context context, final File file, OnReportRecordPhotosPreviewDialogListener listener) {
        super(context, null, R.layout.dialog_record_photos_preview);

        mListener = listener;

        ImageView imageView = view.findViewById(R.id.photo1);
        imageView.setImageBitmap(ImageUtils.decodeFile(file, true));

        view.findViewById(R.id.buttonDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDelete(file);
                dismiss();
            }
        });

        view.findViewById(R.id.buttonDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

}
