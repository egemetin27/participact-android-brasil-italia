package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.bergmannsoft.dialog.ColoredDialog;

import br.com.participact.participactbrasil.R;

public class VideoDialog extends ColoredDialog {

    VideoView mVideoView;

    public VideoDialog(Context context, String url) {
        super(context, null, R.layout.dialog_report_details_video);

        try {
            mVideoView = view.findViewById(R.id.videoView);
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            dismiss();
                        }
                    });
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.seekTo(1);
                        }
                    });

            mVideoView.setVideoURI(Uri.parse(url));
            mVideoView.start();
            mVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mVideoView.stopPlayback();
                    dismiss();
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
