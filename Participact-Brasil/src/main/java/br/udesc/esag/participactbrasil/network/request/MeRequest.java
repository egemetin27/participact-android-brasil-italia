package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.MeRestResult;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class MeRequest extends BaseSpringAndroidSpiceRequest<MeRestResult> {

    private Context context;

    public MeRequest(Context context) {
        super(MeRestResult.class);
        this.context = context;
    }

    @Override
    public MeRestResult loadDataFromNetwork() throws Exception {
        ResponseEntity<MeRestResult> responseEntity = getRestTemplate().exchange(ParticipActConfiguration.ME_URL, HttpMethod.GET, null, MeRestResult.class);
        MeRestResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("me.%s", UserAccountPreferences.getInstance(context)
                .getUserAccount().getUsername());
    }

}