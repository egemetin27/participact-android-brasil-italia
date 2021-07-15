package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.util.FileCache;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.support.UPFile;
import br.com.participact.participactbrasil.modules.support.UrbanProblem;

public class ReportDetailsDialogImage extends ColoredDialog implements View.OnClickListener {

    public ReportDetailsDialogImage(Context context, UPFile file) {
        super(context, null, R.layout.dialog_report_details_photo);
        view.findViewById(R.id.buttonClose).setOnClickListener(this);
        view.findViewById(R.id.image).setOnClickListener(this);
        final ImageView image = view.findViewById(R.id.image);
        image.setImageBitmap(file.photo(new FileCache.OnBitmapDownloadedListener() {
            @Override
            public void onDownloaded(Bitmap bmp) {
                image.setImageBitmap(bmp);
            }
        }));
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

}
