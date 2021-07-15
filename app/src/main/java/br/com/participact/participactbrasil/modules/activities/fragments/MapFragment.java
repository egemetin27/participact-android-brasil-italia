package br.com.participact.participactbrasil.modules.activities.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bergmannsoft.application.BaseFragment;
import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.dialog.BProgressDialog;
import com.bergmannsoft.location.BSLocationListener;
import com.bergmannsoft.location.BSLocationManager;
import com.bergmannsoft.social.Social;
import com.bergmannsoft.social.SocialUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.ProfileCreateActivity;
import br.com.participact.participactbrasil.modules.activities.UrbanProblemCategoriesActivity;
import br.com.participact.participactbrasil.modules.controller.MapController;
import br.com.participact.participactbrasil.modules.dialog.CreateAccountDialog;
import br.com.participact.participactbrasil.modules.dialog.FilterDialog;
import br.com.participact.participactbrasil.modules.support.DataUploader;
import br.com.participact.participactbrasil.modules.support.MessageType;
import br.com.participact.participactbrasil.modules.support.PA;
import br.com.participact.participactbrasil.modules.support.PAProfile;
import br.com.participact.participactbrasil.modules.support.UPCategory;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class MapFragment extends PABaseFragment {

    MapController mapController;

    FilterDialog filterDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateLayout(R.layout.fragment_map, inflater, container);
    }

    @Override
    public void onShow() {
        super.onShow();
        mapController.onResume();
        if (App.getInstance().hasReportSent()) {
            App.getInstance().setHasReportSent(false);
            mapController.refreshUrbanProblems();
            UPCategory category = App.getInstance().getCategory();
            UPCategory subcategory = App.getInstance().getSubcategory();
            TextView categorySubcategory = view.findViewById(R.id.textCategorySubcategory);
            categorySubcategory.setText(category.getName() + " - " + subcategory.getName());
            view.findViewById(R.id.popupView).setVisibility(View.VISIBLE);
            DataUploader.getInstance().uploadAll();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view.findViewById(R.id.popupView).setVisibility(View.GONE);
        view.findViewById(R.id.textUploadProgress).setVisibility(View.GONE);
        mapController = new MapController((AppCompatActivity) getActivity(), this);
        mapController.init(savedInstanceState);

        view.findViewById(R.id.buttonDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.popupView).setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.popupView).setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.buttonMyLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.goToMyLocation();
            }
        });
        view.findViewById(R.id.buttonReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng target = mapController.getLastLocation();
                if (target != null) {
                    report(target);
                } else {
                    BProgressDialog.showProgressBar(getActivity(), false);
                    BSLocationManager.getInstance(getActivity()).requestLocationUpdates(new MapLocationListener());
                }
            }
        });
        view.findViewById(R.id.buttonFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterDialog != null) {
                    filterDialog.dismiss();
                }
                filterDialog = new FilterDialog(getActivity(), new FilterDialog.OnFilterListener() {
                    @Override
                    public void onSelected(int days) {
                        UserSettings.getInstance().setUrbanProblemsRangeDays(days);
                        mapController.refreshUrbanProblems(days);
                    }
                });
                filterDialog.show();
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MessageType.DATA_UPLOADER_UP_FILE_PROGRESS:
                DataUploader.UPFileProgress progress = (DataUploader.UPFileProgress) msg.obj;
                TextView uploadProgress = view.findViewById(R.id.textUploadProgress);
                String type = progress.getType();
                switch (progress.getType()) {
                    case "audio":
                        type = "áudio";
                        break;
                    case "photo":
                        type = "foto";
                        break;
                }
                Log.d(TAG, String.format("Enviando %s %d%%", type, progress.getPercentage()));
                uploadProgress.setText(String.format("Enviando %s %d%%", type, progress.getPercentage()));
                uploadProgress.setVisibility(progress.getPercentage() >= 99 ? View.GONE : View.VISIBLE);
                if (progress.getPercentage() >= 99) {
                    mapController.refreshUrbanProblems();
                }
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public void report(final LatLng latLng) {
        BProgressDialog.hideProgressBar();
        if (mapController.getGoogleMap() == null) {
            Log.e(TAG, "GoogleMap is null");
            return;
        }
        if (mapController.getGoogleMap().getCameraPosition().zoom < 17) {
            Toast.makeText(getActivity(), "Aproxime mais para ter certeza que está postando no local correto.", Toast.LENGTH_LONG).show();
            return;
        }

        if (UserSettings.getInstance().isIdentified()) {
            Intent i = new Intent(getActivity(), UrbanProblemCategoriesActivity.class);
            i.putExtra("latitude", latLng.latitude);
            i.putExtra("longitude", latLng.longitude);
            getActivity().startActivity(i);
        } else {
            // Show dialog to login with Facebook
            Log.d(TAG, "User not identified.");
            new CreateAccountDialog(getActivity(), new CreateAccountDialog.OnSignListener() {
                @Override
                public void onFacebook() {
                    PA.getProfile().facebookLogin(getActivity(), new PAProfile.SocialCompletion() {
                        @Override
                        public void completion(boolean success, Bitmap profileImage) {
                            if (success) {
                                updateHeader();
                                report(latLng);
                            }
                        }
                    });
                }

                @Override
                public void onGooglePlus() {
                    PA.getProfile().googleLogin(getActivity(), new PAProfile.SocialCompletion() {
                        @Override
                        public void completion(boolean success, Bitmap profileImage) {
                            if (success) {
                                updateHeader();
                                report(latLng);
                            } else {
                                AlertDialogUtils.showError(getActivity(), PA.getProfile().lastGoogleSigninError != null ? PA.getProfile().lastGoogleSigninError.getMessage() : "Erro desconhecido logando com Google.");
                            }
                        }
                    });
                }

                @Override
                public void onSignUp() {
                    present(ProfileCreateActivity.class);
                }
            }).show();
        }
    }

    public void showVideo(String url) {
        try {
            final VideoView mVideoView = view.findViewById(R.id.videoView);
            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mVideoView.setVisibility(View.GONE);
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
                    mVideoView.setVisibility(View.GONE);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MapLocationListener extends BSLocationListener {

        @Override
        public void onLocationChanged(Location location) {
            report(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        @Override
        public boolean shouldContinueUpdating() {
            return false;
        }
    }

}
