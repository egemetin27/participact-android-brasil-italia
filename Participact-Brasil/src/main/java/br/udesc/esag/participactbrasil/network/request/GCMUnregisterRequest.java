package br.udesc.esag.participactbrasil.network.request;


import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class GCMUnregisterRequest extends SpringAndroidSpiceRequest<Boolean> {


    private Context context;

    public GCMUnregisterRequest(Context context) {
        super(Boolean.class);
        this.context = context;
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
//        return getRestTemplate().getForObject(ParticipActConfiguration.GCM_UNREGISTER_URL, Boolean.class, user, password);
        ResponseEntity<Boolean> response = getRestTemplate().exchange(ParticipActConfiguration.GCM_UNREGISTER_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), Boolean.class);
        return response.getBody();
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return "unregistergcm";
    }
}

