package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.content.Intent;
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
import com.bergmannsoft.support.Share;
import com.bergmannsoft.util.FileCache;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.support.UPFile;
import br.com.participact.participactbrasil.modules.support.UrbanProblem;

public class ReportDetailsDialog extends ColoredDialog implements View.OnClickListener {

    private final Callback callback;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer mediaPlayerTimer;
    private boolean mediaPlayerPlaying = false;

    private ProgressBar audioProgress;

    private final UrbanProblem ticket;

    public interface Callback {
        void showVideo(String url);
    }

    private ReportAbuseDialog abuseDialog;

    public ReportDetailsDialog(Context context, final UrbanProblem ticket, Callback callback) {
        super(context, null, R.layout.dialog_report_details);

        this.callback = callback;
        this.ticket = ticket;

        final ImageView iconCategory = view.findViewById(R.id.iconCategory);
        TextView categoryName = view.findViewById(R.id.textCategoryName);
        TextView subcategoryName = view.findViewById(R.id.textSubcategoryName);
        TextView dateTime = view.findViewById(R.id.textDateTime);
        TextView comments = view.findViewById(R.id.textComments);

        if (ticket.getSubcategory() != null && ticket.getSubcategory().getCategory() != null) {
            iconCategory.setImageBitmap(ticket.getSubcategory().getCategory().icon(new FileCache.OnBitmapDownloadedListener() {
                @Override
                public void onDownloaded(Bitmap bmp) {
                    iconCategory.setImageBitmap(bmp);
                }
            }));
            categoryName.setText(ticket.getSubcategory().getCategory().getName());
        }
        subcategoryName.setText(ticket.getSubcategory() != null ? ticket.getSubcategory().getName() : "");
        dateTime.setText(ticket.getDateTimeFormatted() + " - #" + ticket.getId());
        comments.setText(ticket.getComment());

        view.findViewById(R.id.buttonClose).setOnClickListener(this);
        view.findViewById(R.id.buttonAudio).setOnClickListener(this);
        view.findViewById(R.id.buttonVideo).setOnClickListener(this);

        view.findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Share.url(getActivity(), "http://www.participact.com.br/home/home-2/?id=" + ReportDetailsDialog.this.ticket.getId(), "Compartilhar", "ParticipAct");
            }
        });

        view.findViewById(R.id.buttonReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abuseDialog = new ReportAbuseDialog(getActivity(), ticket.getId());
                abuseDialog.show();
            }
        });

        audioProgress = view.findViewById(R.id.progressAudio);
        audioProgress.setProgress(0);

        ImageView image1 = view.findViewById(R.id.image1);
        ImageView image2 = view.findViewById(R.id.image2);
        ImageView image3 = view.findViewById(R.id.image3);
        ImageView image4 = view.findViewById(R.id.image4);

        ProgressBar imageProgress1 = view.findViewById(R.id.imageProgress1);
        ProgressBar imageProgress2 = view.findViewById(R.id.imageProgress2);
        ProgressBar imageProgress3 = view.findViewById(R.id.imageProgress3);
        ProgressBar imageProgress4 = view.findViewById(R.id.imageProgress4);

        ProgressBar[] progresses = new ProgressBar[]{imageProgress1, imageProgress2, imageProgress3, imageProgress4};
        for (ProgressBar bar : progresses) {
            bar.setVisibility(View.INVISIBLE);
        }

        ImageView[] images = new ImageView[]{image1, image2, image3, image4};
        for (ImageView im : images) {
            im.setImageBitmap(null);
        }
        int ii = 0;
        if (ticket.getFiles() != null) {
            for (UPFile file : ticket.getFiles()) {
                if (file.isPhoto()) {
                    final ImageView image = images[ii];
                    final ProgressBar bar = progresses[ii];
                    image.setTag(file);
                    image.setOnClickListener(this);
                    Bitmap bmp = file.photo(new FileCache.OnBitmapDownloadedListener() {
                        @Override
                        public void onDownloaded(Bitmap bmp) {
                            image.setImageBitmap(bmp);
                            bar.setVisibility(View.INVISIBLE);
                        }
                    });
                    if (bmp == null) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    image.setImageBitmap(bmp);
                    ii++;
                    if (ii >= images.length) {
                        break;
                    }
                }
            }
        }

        ImageButton audio = view.findViewById(R.id.buttonAudio);
        ImageButton video = view.findViewById(R.id.buttonVideo);

        audio.setBackgroundResource(ticket.hasAudio() ? R.drawable.bg_report_details_button_enabled : R.drawable.bg_report_details_button_disabled);
        audio.setColorFilter(ticket.hasAudio() ? Color.parseColor("#FFFFFF") : Color.parseColor("#bebdbd"));

        video.setBackgroundResource(ticket.hasVideo() ? R.drawable.bg_report_details_button_enabled : R.drawable.bg_report_details_button_disabled);
        video.setColorFilter(ticket.hasVideo() ? Color.parseColor("#FFFFFF") : Color.parseColor("#bebdbd"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonClose:
                dismiss();
                break;
            case R.id.buttonAudio:
                if (ticket.hasAudio()) {
                    playAudio();
                }
                break;
            case R.id.buttonVideo:
                if (ticket.hasVideo()) {
                    playVideo();
                }
                break;
            case R.id.image1:
            case R.id.image2:
            case R.id.image3:
            case R.id.image4:
                new ReportDetailsDialogImage(getActivity(), (UPFile) view.getTag()).show();
                break;
        }
    }

    private void playAudio() {
        if (mediaPlayerPlaying) {
            try {
                mediaPlayer.stop();
                if (mediaPlayerTimer != null)
                    mediaPlayerTimer.cancel();
                audioProgress.setProgress(0);
            } catch (Exception e) {
                Log.e(TAG, null, e);
            }
            return;
        }
        String file = null;
        if (ticket.getFiles() != null) {
            for (UPFile f : ticket.getFiles()) {
                if (f.isAudio()) {
                    file = f.getUrl();
                    break;
                }
            }
        }
        if (file != null) {
            try {
                mediaPlayer.reset();
                String url = file;
//                Uri uri = Uri.parse("http://media.participact.com.br/100254_I1533758766115_e45qw191361w0jtd0hovjbdc2__encoded.mp3");
                Log.d(TAG, "Playing: " + url);
                Uri uri = Uri.parse(url);
                mediaPlayer.setDataSource(getContext(), uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        final int duration = mediaPlayer.getDuration();
                        if (mediaPlayerTimer != null) {
                            mediaPlayerTimer.cancel();
                        }
                        mediaPlayerTimer = new Timer();
                        mediaPlayerTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                int current = mediaPlayer.getCurrentPosition();
                                audioProgress.setProgress(current * 100 / duration);
                            }
                        }, 1000, 1000);
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        audioProgress.setProgress(0);
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayerPlaying = true;
            } catch (Exception e) {
                Log.e(TAG, null, e);
            }
        }
    }

    private void playVideo() {
        String file = null;
        if (ticket.getFiles() != null) {
            for (UPFile f : ticket.getFiles()) {
                if (f.isVideo()) {
                    file = f.getUrl();
                    break;
                }
            }
        }
        if (file != null) {
            String url = file;
//            callback.showVideo("http://media.participact.com.br/100254_I1533758772740_e53xjqjwm7ymihkbsf7qwyfyr__encoded.mp4");
            callback.showVideo(url);
            dismiss();
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath()));
//            intent.setDataAndType(Uri.parse(file.getAbsolutePath()), "video/mp4");
//            context.startActivity(intent);
        }
    }

}
