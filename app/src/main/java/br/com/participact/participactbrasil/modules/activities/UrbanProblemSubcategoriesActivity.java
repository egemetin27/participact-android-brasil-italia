package br.com.participact.participactbrasil.modules.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.location.BSLocationListener;
import com.bergmannsoft.location.BSLocationManager;
import com.bergmannsoft.media.MediaUtils;
import com.bergmannsoft.rest.Response;
import com.bergmannsoft.util.FileCache;
import com.bergmannsoft.util.LocationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.adapters.CategoriesAdapter;
import br.com.participact.participactbrasil.modules.db.UPFileDB;
import br.com.participact.participactbrasil.modules.db.UPFileDaoImpl;
import br.com.participact.participactbrasil.modules.dialog.ReportRecordAudioDialog;
import br.com.participact.participactbrasil.modules.dialog.ReportRecordPhotosDialog;
import br.com.participact.participactbrasil.modules.dialog.ReportRecordVideoDialog;
import br.com.participact.participactbrasil.modules.dialog.SendOmbudsmanDialog;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.ProfileRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemSendRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemSendResponse;
import br.com.participact.participactbrasil.modules.support.UPCategory;
import br.com.participact.participactbrasil.modules.support.UrbanProblem;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class UrbanProblemSubcategoriesActivity extends BaseActivity {

    private ListView listView;
    private UPCategory category;
    private UPCategory subcategory;
    private CategoriesAdapter adapter;

    private TextView audioCount;
    private TextView photoCount;
    private TextView videoCount;

    private File mAudioFile;
    private File mVideoFile;
    private List<File> mPhotoFiles = new ArrayList<>();

    private ReportRecordVideoDialog recordVideoDialog;
    private ReportRecordPhotosDialog recordPhotosDialog;

    private EditText comments;

    private double latitude;
    private double longitude;
    private Long reportId;

    private SendOmbudsmanDialog ombudsmanDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urban_problem_subcategories);
        listView = findViewById(R.id.listView);

        category = (UPCategory) getIntent().getSerializableExtra("category");
        if (category == null) {
            finish();
            return;
        }
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        final ImageView icon = findViewById(R.id.categoryIcon);
        TextView name = findViewById(R.id.categoryName);
        if (icon != null && category != null) {
            icon.setImageBitmap(category.icon(new FileCache.OnBitmapDownloadedListener() {
                @Override
                public void onDownloaded(Bitmap bmp) {
                    icon.setImageBitmap(bmp);
                }
            }));
        }
        name.setText(category.getName());

        List<List<UPCategory>> ccc = App.getInstance().getSubcategoriesForList(category);
        adapter = new CategoriesAdapter(this, ccc, new CategoriesAdapter.OnCategorySelectedListener() {
            @Override
            public void onSelected(UPCategory category) {
                subcategory = category;
                adapter.setSelected(category);
                adapter.notifyDataSetChanged();
            }
        });

        listView.setAdapter(adapter);

        audioCount = findViewById(R.id.textAudioCount);
        photoCount = findViewById(R.id.textPhotoCount);
        videoCount = findViewById(R.id.textVideoCount);

        audioCount.setVisibility(View.INVISIBLE);
        photoCount.setVisibility(View.INVISIBLE);
        videoCount.setVisibility(View.INVISIBLE);

        comments = findViewById(R.id.textComments);
        comments.setText("");

    }

    public void backClick(View view) {
        finish();
    }

    public void closeClick(View view) {
        App.getInstance().setCloseCategories(true);
        finish();
    }

    public void audioClick(View view) {
        new ReportRecordAudioDialog(this, mAudioFile, new ReportRecordAudioDialog.OnReportRecordAudioDialogListener() {
            @Override
            public void onUse(File file) {
                mAudioFile = file;
                if (file != null && file.exists()) {
                    audioCount.setVisibility(View.VISIBLE);
                } else {
                    audioCount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onDelete() {
                mAudioFile = null;
                audioCount.setVisibility(View.INVISIBLE);
            }
        }).show();
    }

    public void photoClick(View view) {
        recordPhotosDialog = new ReportRecordPhotosDialog(this, mPhotoFiles, new ReportRecordPhotosDialog.OnReportRecordPhotosDialogListener() {
            @Override
            public void onDone(List<File> files) {
                mPhotoFiles = files;
                photoCount.setText(String.valueOf(files.size()));
                photoCount.setVisibility(files.size() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        recordPhotosDialog.show();
    }

    public void videoClick(View view) {
        recordVideoDialog = new ReportRecordVideoDialog(this, mVideoFile, new ReportRecordVideoDialog.OnReportRecordVideoDialogListener() {
            @Override
            public void onUse(File file) {
                mVideoFile = file;
                if (file != null && file.exists()) {
                    videoCount.setVisibility(View.VISIBLE);
                } else {
                    videoCount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onDelete() {
                mVideoFile = null;
                videoCount.setVisibility(View.INVISIBLE);
            }
        });
        recordVideoDialog.show();
    }

    public void sendClick(View view) {
        if (subcategory == null) {
            AlertDialogUtils.showAlert(this, "Por favor, seleciona uma subcategoria.");
        } else if (comments.getText().toString().trim().length() == 0) {
            AlertDialogUtils.showAlert(this, "Por favor, digite um comentário.");
        } else if (!UserSettings.getInstance().hasNameAndEmail()) {
            AlertDialogUtils.createDialog(this, "Enviar à Ouvidoria", "Você gostaria de enviar esse problema para a ouvidoria do município?", "Sim", "Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Sim
                    if (ombudsmanDialog != null) {
                        ombudsmanDialog.dismiss();
                    }
                    ombudsmanDialog = new SendOmbudsmanDialog(UrbanProblemSubcategoriesActivity.this, new SendOmbudsmanDialog.OnOptionListener() {
                        @Override
                        public void onSend() {
                            showProgress();
                            UserSettings user = UserSettings.getInstance();
                            SessionManager.getInstance().saveProfile(new ProfileRequest(user.getName(), user.getAbout(), user.getEmail(), user.getEducation(), user.getPhone(), user.getAuthorizeContact(), user.getGender(), user.getAgeRange(), user.getFacebookUrl(), user.getGoogleUrl(), user.getTwitterUrl(), user.getInstagramUrl(), user.getYoutubeUrl()), new SessionManager.RequestCallback<Response>() {
                                @Override
                                public void onResponse(Response response) {
                                    doSend();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    doSend();
                                }
                            });
                            doSend();
                        }

                        @Override
                        public void onCancel() {
                            doSend();
                        }
                    });
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Não
                    doSend();
                }
            });
        } else {
            doSend();
        }
    }

    private void doSend() {
        showProgress();
        UrbanProblemSendRequest request = new UrbanProblemSendRequest(
                subcategory.getId(),
                comments.getText().toString(),
                latitude,
                longitude,
                LocationUtils.getAvailableProvider(this).toUpperCase()
        );
        String city = LocationUtils.getCity(latitude, longitude);
        request.setCity(city);
        request.setAddress(LocationUtils.getAddress(latitude, longitude));
        request.setUserName(UserSettings.getInstance().getName());
        request.setUserEmail(UserSettings.getInstance().getEmail());
        int filesCount = 0;
        if (mPhotoFiles != null) {
            filesCount += mPhotoFiles.size();
        }
        if (mAudioFile != null && mAudioFile.exists()) {
            filesCount++;
        }
        if (mVideoFile != null && mVideoFile.exists()) {
            filesCount++;
        }
        request.setFilesCount(filesCount);
        request.setGpsInfo(LocationUtils.getAvailableProvider(this).toLowerCase().equals("none") ? "GPS desligado" : "GPS ligado");
        SessionManager.getInstance().upSendReport(request, new SessionManager.RequestCallback<UrbanProblemSendResponse>() {
            @Override
            public void onResponse(UrbanProblemSendResponse response) {
                if (response.isSuccess()) {
                    reportId = response.getReportId();
                    if (reportId != null) {
                        uploadAudio();
                    } else {
                        AlertDialogUtils.showError(UrbanProblemSubcategoriesActivity.this, "Erro salvando problema urbano.");
                    }
                } else {
                    AlertDialogUtils.showError(UrbanProblemSubcategoriesActivity.this, response.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialogUtils.showError(UrbanProblemSubcategoriesActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void uploadAudio() {
        if (mAudioFile != null && mAudioFile.exists()) {
            UPFileDB entity = new UPFileDB();
            entity.setFilePath(mAudioFile.getAbsolutePath());
            entity.setReportId(reportId);
            entity.setDuration(MediaUtils.duration(this, mAudioFile));
            entity.setType("audio");
            new UPFileDaoImpl().save(entity);
        }
        uploadPhotos();
    }

    private void uploadPhotos() {
        if (mPhotoFiles != null && mPhotoFiles.size() > 0) {
            uploadPhoto(0);
        } else {
            uploadVideo();
        }
    }

    private void uploadPhoto(int index) {
        if (index < mPhotoFiles.size()) {
            File file = mPhotoFiles.get(index);
            if (file != null && file.exists()) {
                UPFileDB entity = new UPFileDB();
                entity.setFilePath(file.getAbsolutePath());
                entity.setReportId(reportId);
                entity.setType("photo");
                new UPFileDaoImpl().save(entity);
            }
            uploadPhoto(index + 1);
        } else {
            uploadVideo();
        }
    }

    private void uploadVideo() {
        if (mVideoFile != null && mVideoFile.exists()) {
            UPFileDB entity = new UPFileDB();
            entity.setFilePath(mVideoFile.getAbsolutePath());
            entity.setReportId(reportId);
            entity.setDuration(MediaUtils.duration(this, mVideoFile));
            entity.setType("video");
            new UPFileDaoImpl().save(entity);
        }
        dismissProgress();
        App.getInstance().onReportSent(reportId, category, subcategory);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (recordVideoDialog != null) {
            recordVideoDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (recordPhotosDialog != null) {
            recordPhotosDialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
