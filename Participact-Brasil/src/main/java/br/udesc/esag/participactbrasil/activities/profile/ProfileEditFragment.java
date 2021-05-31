package br.udesc.esag.participactbrasil.activities.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;
import org.springframework.util.support.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.bergmannsoft.network.ProgressHttpEntityWrapper;
import br.com.bergmannsoft.network.RestUtils;
import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.FileCache;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.adapters.SingleFragmentAdapter;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.rest.MeRestResult;
import br.udesc.esag.participactbrasil.domain.rest.ProfileUpdateRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.ProfileUpdateRestResult;
import br.udesc.esag.participactbrasil.network.request.MeRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.network.request.ProfileUpdateRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

import static br.udesc.esag.participactbrasil.ParticipActConfiguration.PROFILE_EDIT_PHOTO;

/**
 * Created by fabiobergmann on 14/10/16.
 */

public class ProfileEditFragment extends Fragment {

    public static final int TAKE_PICTURE = 100;
    public static final int PICKUP_PICTURE = 101;
    private View fragment;
    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
    private String currentPhotoPath;
    private ImageView profilePic;

    public static ProfileEditFragment newInstance() {
        ProfileEditFragment fragment = new ProfileEditFragment();
        return fragment;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        UserAccount userAccount = UserAccountPreferences.getInstance(getActivity()).getUserAccount();
        setData(userAccount);

        if (!contentManager.isStarted()) {
            contentManager.start(getActivity());
        }

        fragment.findViewById(R.id.image_check_male).setVisibility(View.INVISIBLE);
        fragment.findViewById(R.id.image_check_female).setVisibility(View.INVISIBLE);
        fragment.findViewById(R.id.image_check_other).setVisibility(View.INVISIBLE);

        if (!Utils.isValidNotEmpty(userAccount.getGender())) {
            fragment.findViewById(R.id.image_check_male).setVisibility(View.VISIBLE);
        } else if (userAccount.getGender().equals("MALE")) {
            fragment.findViewById(R.id.image_check_male).setVisibility(View.VISIBLE);
        } else if (userAccount.getGender().equals("FEMALE")) {
            fragment.findViewById(R.id.image_check_female).setVisibility(View.VISIBLE);
        } else if (userAccount.getGender().equals("OTHER")) {
            fragment.findViewById(R.id.image_check_other).setVisibility(View.VISIBLE);
        } else {
            fragment.findViewById(R.id.image_check_male).setVisibility(View.VISIBLE);
        }

        fragment.findViewById(R.id.button_male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.findViewById(R.id.image_check_male).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.image_check_female).setVisibility(View.INVISIBLE);
                fragment.findViewById(R.id.image_check_other).setVisibility(View.INVISIBLE);
            }
        });
        fragment.findViewById(R.id.button_female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.findViewById(R.id.image_check_male).setVisibility(View.INVISIBLE);
                fragment.findViewById(R.id.image_check_female).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.image_check_other).setVisibility(View.INVISIBLE);
            }
        });
        fragment.findViewById(R.id.button_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.findViewById(R.id.image_check_male).setVisibility(View.INVISIBLE);
                fragment.findViewById(R.id.image_check_female).setVisibility(View.INVISIBLE);
                fragment.findViewById(R.id.image_check_other).setVisibility(View.VISIBLE);
            }
        });

        fragment.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText) fragment.findViewById(R.id.name)).getText().toString();
                final String surname = ((EditText) fragment.findViewById(R.id.surname)).getText().toString();
                final String phone = ((EditText) fragment.findViewById(R.id.phone)).getText().toString();
                final String address = ((EditText) fragment.findViewById(R.id.address)).getText().toString();
                final String number =  ((EditText) fragment.findViewById(R.id.number)).getText().toString();
                final String city =  ((EditText) fragment.findViewById(R.id.city)).getText().toString();
                final String province =  ((EditText) fragment.findViewById(R.id.province)).getText().toString();
                final String country =  ((EditText) fragment.findViewById(R.id.country)).getText().toString();
                final String password = ((EditText) fragment.findViewById(R.id.password)).getText().toString();
                final String zipCode = ((EditText) fragment.findViewById(R.id.cep)).getText().toString();
                final String birthday = ((EditText) fragment.findViewById(R.id.birthday)).getText().toString();
                String gender = "NONE";
                if (fragment.findViewById(R.id.image_check_male).getVisibility() == View.VISIBLE) {
                    gender = "MALE";
                } else if (fragment.findViewById(R.id.image_check_female).getVisibility() == View.VISIBLE) {
                    gender = "FEMALE";
                } else if (fragment.findViewById(R.id.image_check_other).getVisibility() == View.VISIBLE) {
                    gender = "OTHER";
                }
                final String _gender = gender;
                if (!Utils.isValidNotEmpty(name)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_name));
                } else if (!Utils.isValidNotEmpty(surname)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_surname));
                } else if (!Utils.isValidNotEmpty(phone)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_phone));
                } else if (!Utils.isValidNotEmpty(address)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_address));
                } else if (!Utils.isValidNotEmpty(number)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_address_number));
                } else if (!Utils.isValidNotEmpty(city)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_city));
                } else if (!Utils.isValidNotEmpty(province)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_province));
                } else if (!Utils.isValidNotEmpty(country)) {
                    AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_country));
                } else {

                    Date birth = Utils.stringToDate(birthday, "dd/MM/yyyy");
                    if(birth == null) {
                        AlertDialogUtils.showError(getActivity(), getString(R.string.birthday_format_error));
                        return;
                    }

                    ProgressDialog.show(getActivity(), getString(R.string.processing));

                    ProfileUpdateRequest request = new ProfileUpdateRequest(getContext(), new ProfileUpdateRestRequest(name, surname, phone, address, number, city, province, country, zipCode, password, _gender, Utils.dateToString("yyyy-MM-dd", birth)));
                    contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ProfileUpdateRestResult>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            ProgressDialog.dismiss(getActivity());
                            AlertDialogUtils.showError(getActivity(), spiceException.getMessage());
                        }

                        @Override
                        public void onRequestSuccess(ProfileUpdateRestResult result) {

                            ProgressDialog.dismiss(getActivity());

                            if (result.isSuccess()) {

                                UserAccount userAccount = UserAccountPreferences.getInstance(getActivity()).getUserAccount();
                                userAccount.setName(name);
                                userAccount.setSurname(surname);
                                userAccount.setPhone(phone);
                                userAccount.setAddress(address);
                                userAccount.setAddressNumber(number);
                                userAccount.setAddressCity(city);
                                userAccount.setAddressProvince(province);
                                userAccount.setAddressCountry(country);
                                userAccount.setZipCode(zipCode);
                                userAccount.setGender(_gender);
                                userAccount.setBirthday(Utils.stringToDate(birthday, "dd/MM/yyyy"));
                                if (Utils.isValidNotEmpty(password))
                                    userAccount.setPassword(password);
                                UserAccountPreferences.getInstance(getActivity()).saveUserAccount(userAccount);

                                ParticipActApplication.getInstance().dispatchMessage(MessageType.EDIT_PROFILE_SAVED);

                            } else {
                                AlertDialogUtils.showError(getActivity(), result.getMessage());
                            }

                        }
                    });
                }
            }
        });

        fragment.findViewById(R.id.button_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        return fragment;
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Opções").setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0: {
                        File photo = Utils.newPhotoFile();
                        if (photo.exists())
                            photo.delete();

                        try {
                            photo.createNewFile();
                        } catch (IOException e) {
                            Log.e(this.getClass().getSimpleName(), e.toString());
                        }

                        currentPhotoPath = photo.getAbsolutePath();

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                        getActivity().startActivityForResult(intent, TAKE_PICTURE);
                        break;
                    }
                    case 1: {
                        currentPhotoPath = null;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        getActivity().startActivityForResult(intent, PICKUP_PICTURE);
                        break;
                    }
                }
            }
        });
        builder.create().show();
    }

    public void setData(final UserAccount user) {

        ((EditText) fragment.findViewById(R.id.name)).setText(user.getName());
        ((EditText) fragment.findViewById(R.id.surname)).setText(user.getSurname());
        ((EditText) fragment.findViewById(R.id.phone)).setText(user.getPhone());
        ((EditText) fragment.findViewById(R.id.address)).setText(user.getAddress());
        ((EditText) fragment.findViewById(R.id.number)).setText(user.getAddressNumber());
        ((EditText) fragment.findViewById(R.id.city)).setText(user.getAddressCity());
        ((EditText) fragment.findViewById(R.id.province)).setText(user.getAddressProvince());
        ((EditText) fragment.findViewById(R.id.country)).setText(user.getAddressCountry());
        ((EditText) fragment.findViewById(R.id.cep)).setText(user.getZipCode());

        if (user.getBirthday() != null) {
            String birthday = Utils.dateToString("dd/MM/yyyy", user.getBirthday());
            if (birthday != null) {
                ((EditText) fragment.findViewById(R.id.birthday)).setText(birthday);
            }
        }

        profilePic = (ImageView) fragment.findViewById(R.id.profile_pic);
        profilePic.setImageResource(R.drawable.no_profile_pic);
        if (Utils.isValidNotEmpty(user.getPhoto())) {
            FileCache fileCache = FileCache.getInstance(getActivity());
            if (user.hasPhoto() && !user.getPhoto().equals(user.getPhoto()) && Utils.isValidNotEmpty(user.getPhoto())) {
                fileCache.remove(user.getPhotoKey());
            }
            Bitmap bmp = fileCache.getBitmap(user.getPhotoKey());
            if (bmp != null) {
                profilePic.setImageBitmap(bmp);
            } else {
                fileCache.downloadAsync(user.getPhotoKey(), user.getPhoto(), new FileCache.FileCacheCallback() {
                    @Override
                    public void onDownloadDone(String key, final Bitmap bmp) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profilePic.setImageBitmap(bmp);
                                UserAccountPreferences.getInstance(getActivity()).updateUserAccount(user);
                            }
                        });

                    }
                });
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if(resultCode == getActivity().RESULT_OK) {

            ProgressDialog.show(getActivity(), "");

            ParticipActApplication.getInstance().getJobHandler().post(new Runnable() {
                @Override
                public void run() {
                    String path = currentPhotoPath;

                    try {

                        if (path == null) {
                            path = Utils.getFilePath(data.getData(), getActivity().getApplicationContext());
                        }

                        BitmapFactory.Options bounds = new BitmapFactory.Options();
                        bounds.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(path, bounds);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;

                        Bitmap bmp = BitmapFactory.decodeFile(path, options);

                        // Fix orientation
                        ExifInterface exif = new ExifInterface(path);
                        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

                        int rotationAngle = 0;
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                            rotationAngle = 180;
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                            rotationAngle = 270;

                        Matrix matrix = new Matrix();
                        matrix.setRotate(rotationAngle, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

                        Utils.saveToCameraRoll(rotatedBitmap, "ParticipAct", "", getActivity().getApplicationContext());

                        // Crop
                        int ow = rotatedBitmap.getWidth();
                        int oh = rotatedBitmap.getHeight();

                        int cropTo = ow > oh ? oh : ow;

                        int x = 0;
                        int y = 0;

                        if (ow > oh) {
                            x = (ow - oh) / 2;
                        } else {
                            y = (oh - ow) / 2;
                        }

                        Bitmap cropped = Bitmap.createBitmap(rotatedBitmap, x, y, cropTo, cropTo);
                        rotatedBitmap.recycle();

                        // Resize
                        Bitmap resized = Bitmap.createScaledBitmap(cropped, 320, 320, true);
                        rotatedBitmap.recycle();

                        // Store
                        storeImage(resized);

                        // Upload
                        uploadToServer(currentPhotoPath);

                    } catch (IOException e) {
                        Log.d("Profile", null, e);
                    }
                }
            });
        }
    }

    private void storeImage(Bitmap image) {
        File pictureFile = new File(currentPhotoPath);
        if (pictureFile.exists())
            pictureFile.delete();
        if (pictureFile == null) {
            Log.d("Profile",
                    "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Profile", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Profile", "Error accessing file: " + e.getMessage());
        }
    }

    private void uploadToServer(String path) {
        try {
            File bz2 = new File(path);
            FileBody body = new FileBody(bz2);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", body);

            String url = PROFILE_EDIT_PHOTO;

            final UserAccount user = UserAccountPreferences.getInstance(getContext()).getUserAccount();
            String authStr = String.format("%s:%s", user.getUsername(), user.getPassword());
            String authEncoded = Base64.encodeBytes(authStr.getBytes());

            String resp = RestUtils.doPostFile(builder.build(), url,
                    "Basic " + authEncoded, new ProgressHttpEntityWrapper.ProgressCallback() {
                        @Override
                        public void progress(float progress) {
                            int p = (int) progress;
                            ProgressDialog.updateMessage(getActivity(), "Uploading " + p + "%");
                            if (p == 100) {
                                ProgressDialog.updateMessage(getActivity(), "Processing");
                            }
                        }
                    });

            Log.d("Profile", resp);

            JSONObject json = new JSONObject(resp);
            if (!json.isNull("status") && json.getBoolean("status")) {
                if (!json.isNull("data")) {
                    String pic = json.getString("data");
                    user.setPhoto(pic);
                    UserAccountPreferences.getInstance(getContext()).updateUserAccount(user);
                    FileCache.getInstance(getActivity()).remove(user.getPhotoKey());
                    FileCache.getInstance(getActivity()).downloadAsync(user.getPhotoKey(), user.getPhoto(), new FileCache.FileCacheCallback() {
                        @Override
                        public void onDownloadDone(String key, final Bitmap bmp) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profilePic.setImageBitmap(bmp);
                                }
                            });

                        }
                    });
                }
            }
            bz2.delete();

        } catch (Exception e) {
            Log.e("Profile", null, e);
            AlertDialogUtils.showError(getActivity(), e.getMessage());
        } finally {
            ProgressDialog.dismiss(getActivity());
        }
    }

}
