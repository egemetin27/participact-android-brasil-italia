package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.media.AudioRecorder;

import java.io.File;

import br.com.participact.participactbrasil.R;

public class ReportRecordAudioDialog extends ColoredDialog implements View.OnClickListener {

    private AudioRecorder audioRecorder;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private File mFile;

    private Button cancel;
    private Button use;

    public interface OnReportRecordAudioDialogListener {
        void onUse(File file);
        void onDelete();
    }

    private OnReportRecordAudioDialogListener mListener;

    public ReportRecordAudioDialog(Context context, File file, OnReportRecordAudioDialogListener listener) {
        super(context, null, R.layout.dialog_record_audio);

        mFile = file;
        mListener = listener;

        audioRecorder = new AudioRecorder(context, new AudioRecorder.OnRecordListener() {
            @Override
            public void onRecording(String time) {
                TextView textTime = view.findViewById(R.id.textTime);
                textTime.setText(time);
            }

            @Override
            public void onCompletion(File audio) {
                if (mFile != null && mFile.exists()) {
                    mFile.delete();
                }
                mFile = audio;
                cancel.setText("EXCLUIR");
                use.setVisibility(View.VISIBLE);
                view.findViewById(R.id.buttonPlay).setVisibility(View.VISIBLE);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, null, e);
            }
        });

        cancel = view.findViewById(R.id.buttonCancel);
        use = view.findViewById(R.id.buttonUse);

        cancel.setText("CANCELAR");
        use.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.buttonPlay).setVisibility(View.INVISIBLE);

        if (mFile != null && mFile.exists()) {
            cancel.setText("EXCLUIR");
            use.setVisibility(View.VISIBLE);
            view.findViewById(R.id.buttonPlay).setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.buttonCancel).setOnClickListener(this);
        view.findViewById(R.id.buttonUse).setOnClickListener(this);
        view.findViewById(R.id.buttonPlay).setOnClickListener(this);
        view.findViewById(R.id.buttonRecord).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        audioRecorder.start();
                        return true;
                    case MotionEvent.ACTION_UP:
                        audioRecorder.stop();
                        return true;
                }
                return false;
            }
        });



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCancel:
                if (mFile != null && mFile.exists()) {
                    mFile.delete();
                    mListener.onDelete();
                }
                dismiss();
                break;
            case R.id.buttonUse:
                mListener.onUse(mFile);
                dismiss();
                break;
            case R.id.buttonPlay:
                try {
                    mediaPlayer.setDataSource(mFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
