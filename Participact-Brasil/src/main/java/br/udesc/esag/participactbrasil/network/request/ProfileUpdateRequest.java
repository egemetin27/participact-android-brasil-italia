package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ProfileUpdateRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.ProfileUpdateRestResult;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 25/10/16.
 */

public class ProfileUpdateRequest extends BaseSpringAndroidSpiceRequest<ProfileUpdateRestResult> {

    private final ProfileUpdateRestRequest request;
    private final Context context;

    public ProfileUpdateRequest(Context context, ProfileUpdateRestRequest request) {
        super(ProfileUpdateRestResult.class);
        this.context = context;
        this.request = request;
    }

    @Override
    public ProfileUpdateRestResult loadDataFromNetwork() throws Exception {
        ResponseEntity<ProfileUpdateRestResult> responseEntity = getRestTemplate().exchange(
                ParticipActConfiguration.PROFILE_UPDATE_URL,
                HttpMethod.POST,
                getRequest(context, request),
                ProfileUpdateRestResult.class
        );
        ProfileUpdateRestResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("profile.%s", UserAccountPreferences.getInstance(context).getUserAccount().getUsername());
    }
}
