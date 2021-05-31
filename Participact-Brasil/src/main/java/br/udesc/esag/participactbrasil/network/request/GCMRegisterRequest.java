package br.udesc.esag.participactbrasil.network.request;


import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class GCMRegisterRequest extends SpringAndroidSpiceRequest<Boolean> {

    private Context context;
    private String gcmId;

    public GCMRegisterRequest(Context context) {
        super(Boolean.class);
        this.context = context;
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        gcmId = gcm.register(ParticipActConfiguration.GCM_SENDER_ID);
        UserAccountPreferences accountDao = UserAccountPreferences.getInstance(context);
        UserAccount account = accountDao.getUserAccount();
        account.setRegistrationId(gcmId);
        accountDao.saveUserAccount(account);
        accountDao.setGCMSetOnServer(false);
        accountDao.setGcmOnServerExpirationTime(System.currentTimeMillis() + ParticipActConfiguration.REGISTRATION_EXPIRY_TIME_MS);
//      return getRestTemplate().getForObject(ParticipActConfiguration.GCM_REGISTER_URL, Boolean.class, user, password, gcmId);
        ResponseEntity<Boolean> response = getRestTemplate().exchange(ParticipActConfiguration.GCM_REGISTER_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), Boolean.class, gcmId);
        return response.getBody();
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return String.format("registergcm.%s", UserAccountPreferences.getInstance(context).getUserAccount().getUsername());
    }
}

