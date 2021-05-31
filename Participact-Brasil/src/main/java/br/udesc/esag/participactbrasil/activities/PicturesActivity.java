package br.udesc.esag.participactbrasil.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.adapters.PicturesListAdapter;
import br.udesc.esag.participactbrasil.domain.local.ImageDescriptor;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.support.ImageDescriptorUtility;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderPhotoPreferences;

public class PicturesActivity extends AppCompatActivity {

    private ActionFlat actionFlat;
    private Button btSave;
    private FloatingActionButton fabNewPicture;
    private static final Logger logger = LoggerFactory.getLogger(PicturesActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_picture);

        this.actionFlat = (ActionFlat) getIntent().getSerializableExtra("action");

        fabNewPicture = (FloatingActionButton) findViewById(R.id.fabNewPicture);
        fabNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        btSave = (Button) findViewById(R.id.btSavePictures);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUploaderPhotoPreferences.getInstance(PicturesActivity.this).setPhotoUpload(true);
                finish();
            }
        });

        adjustToolbar();
        loadDescription();
        loadPicturesView();

    }

    private void takePicture() {
        DataUploaderPhotoPreferences.getInstance(this).setPhotoUpload(false);

        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
        String imageFileName = "Photo_" + timeStamp;

        File image = null;
        image = new File(getExternalFilesDir(null), imageFileName + ".jpg");

        ImageDescriptor imgDescriptor = new ImageDescriptor();
        imgDescriptor.setTaskId(actionFlat.getTask().getId());
        imgDescriptor.setActionId(actionFlat.getId());
        imgDescriptor.setSampleTimestamp(date.getTime());
        imgDescriptor.setImageName(imageFileName);
        imgDescriptor.setImagePath(image.getAbsolutePath());
        imgDescriptor.setUploaded(false);

        ImageDescriptorUtility.persistImageDescriptor(this, "temp.ids", imgDescriptor);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));

        logger.info("Taking Photo {} of task {} action id {}.", imgDescriptor.getImageName(), actionFlat.getTask().getId(), actionFlat.getId());
        startActivityForResult(takePictureIntent, ParticipActConfiguration.PHOTO_REQUEST_CODE);
    }

    private void loadDescription() {
        if (actionFlat.getNumeric_threshold() != null) {
           // numPhotosLabetText.getInputWidgetText().append("" + actionFlat.getNumeric_threshold());
        }

        TextView txtDescription = (TextView) findViewById(R.id.txtViewDescription);
        txtDescription.setText(actionFlat.getName());
    }

    private void adjustToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(actionFlat.getName().toUpperCase());
    }

    private void loadPicturesView() {
        List<String> photosPathList = new ArrayList<>();
        File[] files = ImageDescriptorUtility.getImageDescriptors(this);
        for (File file : files) {
            ImageDescriptor imgDescr = ImageDescriptorUtility.loadImageDescriptor(this, file.getName());
            if (imgDescr.getTaskId().equals(actionFlat.getTask().getId()) && imgDescr.getActionId().equals(actionFlat.getId())) {
               photosPathList.add(imgDescr.getImagePath());
            }
        }

        if(photosPathList.size() >= actionFlat.getNumeric_threshold()){
            fabNewPicture.setVisibility(View.GONE);
            btSave.setText("OK");
        }

        PicturesListAdapter adapter = new PicturesListAdapter(this, photosPathList);
        GridView gridView = (GridView) findViewById(R.id.gridViewPictures);
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ParticipActConfiguration.PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ImageDescriptor imgD = ImageDescriptorUtility.loadImageDescriptor(this, "temp.ids");
                if (imgD != null) {
                    ImageDescriptorUtility.renameFile(this, "temp.ids", imgD.getImageName() + ".ids");
                    StateUtility.incrementPhotoProgress(this, imgD.getTaskId(), imgD.getActionId());
                    logger.info("Successfully stored new photo {} of taskId {} and actionId {}.",
                            imgD.getImageName(), imgD.getTaskId(),
                            imgD.getActionId());
                }
                loadPicturesView();
            } else {
                ImageDescriptor imgD = ImageDescriptorUtility.loadImageDescriptor(this, "temp.ids");
                ImageDescriptorUtility.deleteImageDescriptor(this, "temp.ids");
                logger.info("Failed to store photo {} of taskId {} and actionId {}.",
                        imgD.getImageName(), imgD.getTaskId(),
                        imgD.getActionId());
            }
        }
    }

}
