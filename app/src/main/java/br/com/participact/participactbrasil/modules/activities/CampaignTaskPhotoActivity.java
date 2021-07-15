package br.com.participact.participactbrasil.modules.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.dialog.BProgressDialog;
import com.bergmannsoft.support.ImagePicker;
import com.bergmannsoft.util.ImageUtils;
import com.bergmannsoft.util.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Photo;
import br.com.participact.participactbrasil.modules.db.PhotoDaoImpl;
import br.com.participact.participactbrasil.modules.db.PhotoWrapper;
import br.com.participact.participactbrasil.modules.dialog.ReportRecordPhotosPreviewDialog;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.IPDataResponse;

public class CampaignTaskPhotoActivity extends CampaignTaskBaseActivity {

    private List<File> photos = new ArrayList<>();

    ImagePicker imagePicker = new ImagePicker(this);

    TextView textPhotosCount;

    List<Photo> entities;

    Button send;

    int maxPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_task_photo);

        createUI();

    }

    @Override
    protected void createUI() {
        textPhotosCount = findViewById(R.id.textPhotosCount);
        send = findViewById(R.id.buttonSend);
        send.setVisibility(View.GONE);

        if (action.getMinimum() != null) {
            maxPhotos = action.getMinimum();
        }
        if (question != null && question.getPhoto() != null && question.getPhoto()) {
            maxPhotos = question.getNumberPhotos() != null ? question.getNumberPhotos() : 1;
        }

        if (!isValid())
            return;

        setupDescriptionHeader();

        photos.clear();
        PhotoDaoImpl dao = new PhotoDaoImpl();
        if (question != null) {
            entities = dao.findByQuestionId(question.getId());
        } else {
            entities = dao.findByCampaignId(campaign.getId());
        }
        for (Photo photo : entities) {
            photos.add(PhotoWrapper.wrap(photo).getFile());
        }

        updateUI();
    }

    private Object locationLocker = new Object();

    public void takePhotoClick(View view) {
        if (!Utils.hasCameraPermission(this)) {
            showAlert("O ParticipACT não tem permissão para utilizar a camera do aparelho. Você pode ter negado essa permissão. Acesse as configurações do aparelho e dê permissão de acesso à camera para o ParticipACT. Caso o erro persista, desinstale o app e instale novamente a partir da loja de aplicativos. Ao abrir, o app solicitará as permissões novamente. " + "[" + Build.VERSION.SDK_INT + "]");
            return;
        }
        if (photos.size() >= maxPhotos) {
            Toast.makeText(this, "Você já tirou a quantidade de fotos necessárias para essa campanha.", Toast.LENGTH_LONG).show();
            return;
        }
        imagePicker.pickUp(new ImagePicker.ImagePickerListener() {
            @Override
            public void onImage(File imageFile) {
                BProgressDialog.hideProgressBar();
                if (imageFile != null && imageFile.exists()) {
                    photos.add(imageFile);
                    final Photo photo = new Photo();
                    photo.setFilename(imageFile.getAbsolutePath());
                    photo.setUploaded(false);
                    photo.setActionId(action.getId());
                    photo.setCampaignId(campaign.getId());
                    photo.setReadyToUpload(true);
                    if (question != null && question.getPhoto() != null && question.getPhoto()) {
                        photo.setQuestionId(question.getId());
                        photo.setReadyToUpload(false);
                    }
                    final long photoId = new PhotoDaoImpl().commit(photo);
                    try {

                        SessionManager.getInstance().getIPData(new SessionManager.RequestCallback<IPDataResponse>() {
                            @Override
                            public void onResponse(IPDataResponse response) {
                                System.out.println();
                                synchronized (locationLocker) {
                                    photo.setId(photoId);
                                    if (response != null && photo.getLatitude() == null) {
                                        photo.setLatitude(response.getLatitude());
                                        photo.setLongitude(response.getLongitude());
                                        photo.setProvider("IP");
                                    }
                                    if (response != null) {
                                        photo.setIpAddress(response.getIp());
                                    }
                                    new PhotoDaoImpl().commit(photo);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                t.printStackTrace();
                            }
                        });

                        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                synchronized (locationLocker) {
                                    if (location != null) {
                                        photo.setId(photoId);
                                        photo.setLatitude(location.getLatitude());
                                        photo.setLongitude(location.getLongitude());
                                        photo.setProvider("GPS");
                                        new PhotoDaoImpl().commit(photo);
                                    }
                                }
                            }
                        });
                    } catch (SecurityException e) {
                        Log.d(TAG, null, e);
                    }
                }
                updateUI();
            }

            @Override
            public void onCancel() {
                try {
                    BProgressDialog.hideProgressBar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProcessing() {
                try {
                    BProgressDialog.showProgressBar(CampaignTaskPhotoActivity.this, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (question != null && question.getPhoto() != null && question.getPhoto()) {
                    send.setVisibility(View.VISIBLE);
                    if (question.getRequired() != null && !question.getRequired()) {
                        send.setText("PULAR FOTO");
                    }
                }
                textPhotosCount.setText(photos.size() + "/" + maxPhotos);
                if (photos.size() > 0) {
                    send.setText("ENVIAR");
                }
                LinearLayout linearLayoutPhotos = findViewById(R.id.linearLayoutPhotos);
                linearLayoutPhotos.removeAllViews();
                int i = 0;
                View view = null;
                List<ImageView> imageViews = new ArrayList<>();
                for (File file : photos) {
                    if (view == null || i == 3) {
                        view = App.getInstance().getLayoutInflater().inflate(R.layout.item_campaign_task_photo, null);
                        linearLayoutPhotos.addView(view);
                        imageViews.clear();
                        imageViews.add((ImageView) view.findViewById(R.id.photo1));
                        imageViews.add((ImageView) view.findViewById(R.id.photo2));
                        imageViews.add((ImageView) view.findViewById(R.id.photo3));
                        for (ImageView imageView : imageViews) {
                            imageView.setImageBitmap(null);
                        }
                        i = 0;
                    }
                    ImageView imageView = imageViews.get(i);
                    imageView.setImageBitmap(ImageUtils.decodeFile(file));
                    imageView.setTag(file);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File file = (File) view.getTag();
                            new ReportRecordPhotosPreviewDialog(CampaignTaskPhotoActivity.this, file, new ReportRecordPhotosPreviewDialog.OnReportRecordPhotosPreviewDialogListener() {
                                @Override
                                public void onDelete(File file) {
                                    file.delete();
                                    photos.remove(file);
                                }
                            }).show();
                        }
                    });
                    i++;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagePicker.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Used when it's a photo question
     *
     * @param view - The View
     */
    public void sendClick(View view) {
        if (question != null && question.getPhoto() != null && question.getPhoto()) {
            if (photos.size() == 0 && (question.getRequired() != null && question.getRequired())) {
                showAlert("Por favor, tire pelo menos uma foto.");
            } else {
                if (photos.size() == 0) {
                    question.setSkipped(true);
                } else {
                    new PhotoDaoImpl().makePhotosReadyForQuestionId(question.getId());
                }
                prepareToSend();
            }
        }
    }
}
