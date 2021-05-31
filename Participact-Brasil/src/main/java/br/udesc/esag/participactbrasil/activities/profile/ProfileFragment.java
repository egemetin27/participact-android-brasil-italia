package br.udesc.esag.participactbrasil.activities.profile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.FileCache;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignDetailsFragment;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.rest.MeRestResult;
import br.udesc.esag.participactbrasil.network.request.AcceptTaskRequest;
import br.udesc.esag.participactbrasil.network.request.MeRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 14/10/16.
 */

public class ProfileFragment extends Fragment {

    private View fragment;
    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_profile, container, false);

        UserAccount userAccount = UserAccountPreferences.getInstance(getActivity()).getUserAccount();
        setData(userAccount);

        if (!contentManager.isStarted()) {
            contentManager.start(getActivity());
        }

        if (!userAccount.hasCompletedData()) {

            ProgressDialog.show(getActivity(), null);

            MeRequest request = new MeRequest(getActivity());
            contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<MeRestResult>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    ProgressDialog.dismiss(getActivity());
                    AlertDialogUtils.showError(getActivity(), spiceException.getMessage());
                }

                @Override
                public void onRequestSuccess(MeRestResult meRestResult) {

                    ProgressDialog.dismiss(getActivity());

                    try {

                        UserAccount userAccount = UserAccountPreferences.getInstance(getActivity()).getUserAccount();
                        userAccount.setName(meRestResult.getName());
                        userAccount.setPhoto(meRestResult.getPhoto());
                        userAccount.setPhone(meRestResult.getPhone());
                        userAccount.setAddress(meRestResult.getAddress());
                        userAccount.setZipCode(meRestResult.getCurrentZipCode());
                        userAccount.setAddressNumber(meRestResult.getCurrentNumber());
                        userAccount.setAddressCity(meRestResult.getCurrentCity());
                        userAccount.setAddressCountry(meRestResult.getCurrentCountry());
                        userAccount.setAddressProvince(meRestResult.getCurrentProvince());
                        userAccount.setSurname(meRestResult.getSurname());
                        userAccount.setGender(meRestResult.getGender());
                        userAccount.setBirthday(Utils.stringToDate(meRestResult.getBirthdate(), "yyyy-MM-dd"));
                        UserAccountPreferences.getInstance(getActivity()).saveUserAccount(userAccount);

                        setData(userAccount);
                    } catch (Exception e){
                        AlertDialogUtils.showError(getActivity(), e.getMessage());
                    }

                }
            });
        }

        fragment.findViewById(R.id.button_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticipActApplication.getInstance().dispatchMessage(MessageType.EDIT_PROFILE);
            }
        });

        return fragment;
    }

    public void setData(final UserAccount user) {

        ((TextView) fragment.findViewById(R.id.text_name)).setText(user.getFullname());
        ((TextView) fragment.findViewById(R.id.text_email)).setText(user.getUsername());
        ((TextView) fragment.findViewById(R.id.text_phone)).setText(user.getPhone());
        ((TextView) fragment.findViewById(R.id.text_address)).setText(user.getAddress1());
        ((TextView) fragment.findViewById(R.id.text_cep)).setText(user.getAddress2());
        final ImageView profilePic = (ImageView) fragment.findViewById(R.id.profile_pic);
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
}
