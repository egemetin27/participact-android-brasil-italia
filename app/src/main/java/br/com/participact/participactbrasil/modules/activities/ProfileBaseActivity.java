package br.com.participact.participactbrasil.modules.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.dialog.BProgressDialog;
import com.bergmannsoft.rest.Response;
import com.bergmannsoft.retrofit.ProgressRequestBody;
import com.bergmannsoft.social.Facebook;
import com.bergmannsoft.social.Google;
import com.bergmannsoft.support.ImagePicker;
import com.bergmannsoft.util.FileCache;
import com.bergmannsoft.util.ImageUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.PhotoDaoImpl;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.ProfileRequest;
import br.com.participact.participactbrasil.modules.network.requests.StorageFilePhotoProfileRequest;
import br.com.participact.participactbrasil.modules.support.DataUploader;
import br.com.participact.participactbrasil.modules.support.MessageType;
import br.com.participact.participactbrasil.modules.support.PALists;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class ProfileBaseActivity extends BaseActivity {

    ImageView profileImage;
    boolean authorize = UserSettings.getInstance().getAuthorizeContact();

    ImagePicker imagePicker = new ImagePicker(this);
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateData();
    }

    protected void updateData() {
        UserSettings user = UserSettings.getInstance();

        profileImage = findViewById(R.id.profile_image);
        profileImage.setImageBitmap(user.picture(new FileCache.OnBitmapDownloadedListener() {
            @Override
            public void onDownloaded(Bitmap bmp) {
                profileImage.setImageBitmap(bmp);
            }
        }));

        setEditTextValue(R.id.name, user.getName());
        setEditTextValue(R.id.about, user.getAbout());
        setEditTextValue(R.id.education, user.getEducation());
//        setEditTextValue(R.id.university, user.getUniversity());
//        setEditTextValue(R.id.course, user.getCourse());
//        setEditTextValue(R.id.job, user.getJob());
        setEditTextValue(R.id.email, user.getEmail());
        setEditTextValue(R.id.phone, user.getPhone());
        setEditTextValue(R.id.ageRange, user.getAgeRange());

        ImageView authorizeContact = findViewById(R.id.imageAuthorizeContact);
        authorizeContact.setImageResource(authorize ? R.mipmap.ic_profile_checkbox_on : R.mipmap.ic_profile_checkbox_off);

        setEditTextValue(R.id.facebook, user.getFacebookUrl());
        setEditTextValue(R.id.google, user.getGoogleUrl());
        setEditTextValue(R.id.twitter, user.getTwitterUrl());
        setEditTextValue(R.id.instagram, user.getInstagramUrl());
        setEditTextValue(R.id.youtube, user.getYoutubeUrl());

        if (user.getGender() == null) {
            setGenderSelection(R.id.layoutMale);
        } else {

            switch (user.getGender()) {
                case "male":
                    setGenderSelection(R.id.layoutMale);
                    break;
                case "female":
                    setGenderSelection(R.id.layoutFemale);
                    break;
                case "other":
                    setGenderSelection(R.id.layoutOther);
                    break;
                default:
                    setGenderSelection(R.id.layoutMale);
                    break;
            }
        }

    }

    private void setGenderSelection(int id) {
        switch (id) {
            case R.id.layoutMale:
                UserSettings.getInstance().setGender("male");
                break;
            case R.id.layoutFemale:
                UserSettings.getInstance().setGender("female");
                break;
            default:
                UserSettings.getInstance().setGender("other");
                break;
        }
        LinearLayout female = findViewById(R.id.layoutFemale);
        LinearLayout male = findViewById(R.id.layoutMale);
        LinearLayout other = findViewById(R.id.layoutOther);
        List<LinearLayout> layouts = Arrays.asList(female, male, other);
        List<Integer> iconsSelected = Arrays.asList(R.mipmap.ic_gender_woman_selected, R.mipmap.ic_gender_man_selected, R.mipmap.ic_gender_other_selected);
        List<Integer> iconsUnselected = Arrays.asList(R.mipmap.ic_gender_woman, R.mipmap.ic_gender_man, R.mipmap.ic_gender_other);
        int index = 0;
        for (LinearLayout layout : layouts) {
            if (layout.getId() == id) {
                setGenderSelection(layout, "#FFFFFF", iconsSelected.get(index), "#bebdbd");
            } else {
                setGenderSelection(layout, "#00000000", iconsUnselected.get(index), "#4EC500");
            }
            index++;
        }
    }

    private void setGenderSelection(LinearLayout layout, String backgroundColor, int iconId, String textColor) {
        layout.setBackgroundColor(Color.parseColor(backgroundColor));
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(iconId);
            } else {
                TextView text = (TextView) view;
                text.setTextColor(Color.parseColor(textColor));
            }
        }
    }

    public void photoClick(View view) {
        imagePicker.pickUp(new ImagePicker.ImagePickerListener() {
            @Override
            public void onImage(final File imageFile) {
                BProgressDialog.hideProgressBar();
                ProfileBaseActivity.this.imageFile = imageFile;
                Bitmap bmp = ImageUtils.decodeFile(imageFile, true);
                if (bmp != null)
                    App.getInstance().getFileCache().put("profile_image", bmp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileImage.setImageBitmap(ImageUtils.decodeFile(imageFile, true));
                    }
                });
            }

            @Override
            public void onCancel() {
                BProgressDialog.hideProgressBar();
            }

            @Override
            public void onProcessing() {
                BProgressDialog.showProgressBar(ProfileBaseActivity.this, false);
            }

        });
    }

    public void educationClick(View view) {
        AlertDialogUtils.showOptionsDialog(this, getString(R.string.select), R.array.educations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] items = getResources().getStringArray(R.array.educations);
                setEditTextValue(R.id.education, items[i]);
            }
        });
    }

//    public void universityClick(View view) {
//        AlertDialogUtils.showOptionsDialog(this, "Selecione", PALists.universities, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                setEditTextValue(R.id.university, PALists.universities.get(i));
//            }
//        });
//    }
//
//    public void courseClick(View view) {
//        AlertDialogUtils.showOptionsDialog(this, "Selecione", PALists.courses, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                setEditTextValue(R.id.course, PALists.courses.get(i));
//            }
//        });
//    }
//
//    public void jobClick(View view) {
//        AlertDialogUtils.showOptionsDialog(this, "Selecione", PALists.jobs, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                setEditTextValue(R.id.job, PALists.jobs.get(i));
//            }
//        });
//    }

    public void femaleClick(View view) {
        setGenderSelection(R.id.layoutFemale);
    }

    public void maleClick(View view) {
        setGenderSelection(R.id.layoutMale);
    }

    public void otherClick(View view) {
        setGenderSelection(R.id.layoutOther);
    }

    public void ageRangeClick(View view) {
        AlertDialogUtils.showOptionsDialog(this, "Selecione", PALists.ageRanges, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setEditTextValue(R.id.ageRange, PALists.ageRanges.get(i));
            }
        });
    }


    public void closeClick(View view) {
        finish();
    }

    public void authorizeContactClick(View view) {
        authorize = !authorize;
        ImageView authorizeContact = findViewById(R.id.imageAuthorizeContact);
        authorizeContact.setImageResource(authorize ? R.mipmap.ic_profile_checkbox_on : R.mipmap.ic_profile_checkbox_off);
    }

    public void sendClick(View view) {
        if (getEditTextValue(R.id.name).trim().length() == 0) {
            showAlert(getString(R.string.please_provide_your_name));
            return;
        }
        if (getEditTextValue(R.id.email).trim().length() == 0) {
            showAlert(getString(R.string.please_provide_your_email));
            return;
        }
        if (getEditTextValue(R.id.phone).trim().length() == 0) {
            showAlert(getString(R.string.please_provide_your_phone_number));
            return;
        }
        UserSettings user = UserSettings.getInstance();
        user.setName(getEditTextValue(R.id.name));
        user.setAbout(getEditTextValue(R.id.about));
        user.setEducation(getEditTextValue(R.id.education));
//        user.setUniversity(getEditTextValue(R.id.university));
//        user.setCourse(getEditTextValue(R.id.course));
//        user.setJob(getEditTextValue(R.id.job));
        user.setEmail(getEditTextValue(R.id.email));
        user.setPhone(getEditTextValue(R.id.phone));
        user.setAgeRange(getEditTextValue(R.id.ageRange));
        user.setAuthorizeContact(authorize);
        user.setFacebookUrl(getEditTextValue(R.id.facebook));
        user.setGoogleUrl(getEditTextValue(R.id.google));
        user.setTwitterUrl(getEditTextValue(R.id.twitter));
        user.setInstagramUrl(getEditTextValue(R.id.instagram));
        user.setYoutubeUrl(getEditTextValue(R.id.youtube));
        user.setHasProfile(true);
        user.setIdentified(user.getEmail() != null && user.getEmail().trim().length() > 0 &&
        user.getEmail() != null && user.getEmail().length() > 0 &&
        user.getPhone() != null && user.getPhone().length() > 0);
        showProgress("");

        SessionManager.getInstance().saveProfile(new ProfileRequest(user.getName(), user.getAbout(), user.getEmail(), user.getEducation(), user.getPhone(), user.getAuthorizeContact(), user.getGender(), user.getAgeRange(), user.getFacebookUrl(), user.getGoogleUrl(), user.getTwitterUrl(), user.getInstagramUrl(), user.getYoutubeUrl()), new SessionManager.RequestCallback<Response>() {
            @Override
            public void onResponse(Response response) {
                if (response != null && response.isSuccess()) {
                    if (imageFile == null || !imageFile.exists()) {
                        dismissProgress();
                        finish();
                    } else {
                        uploadImage();
                    }
                } else {
                    showError("Erro salvando perfil.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dismissProgress();
                showError(t.getLocalizedMessage());
            }
        });

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissProgress();
//                        finish();
//                    }
//                });
//            }
//        }, 2000);

    }

    private void uploadImage() {
        SessionManager.getInstance().uploadFile(new StorageFilePhotoProfileRequest(imageFile, UserSettings.getInstance().getId()), new SessionManager.RequestCallback<Response>() {
            @Override
            public void onResponse(Response response) {
                dismissProgress();
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, null, t);
                dismissProgress();
                finish();
            }
        }, new ProgressRequestBody.ProgressCallback() {
            @Override
            public void onProgress(int percentage) {
                updateProgressMessage("Enviando " + percentage + "%");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagePicker.onActivityResult(requestCode, resultCode, data);
        Facebook.getInstance(this).onActivityResult(requestCode, resultCode, data);
        Google.getInstance(this).onActivityResult(requestCode, resultCode, data);
    }

}
