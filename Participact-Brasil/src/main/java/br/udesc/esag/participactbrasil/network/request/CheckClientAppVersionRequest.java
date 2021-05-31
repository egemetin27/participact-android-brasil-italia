package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class CheckClientAppVersionRequest extends SpringAndroidSpiceRequest<Integer> {

    private Context context;

    public CheckClientAppVersionRequest(Context context) {
        super(Integer.class);
        this.context = context;
    }

    @Override
    public Integer loadDataFromNetwork() throws Exception {
        ResponseEntity<Integer> response = getRestTemplate().exchange(
                ParticipActConfiguration.CHECK_CLIENT_APP_VERSION, HttpMethod.GET,
                BasicAuthenticationUtility.getHttpEntityForAuthentication(context), Integer.class);
        return response.getBody();
    }

    public String createCacheKey() {
        return "CheckClientAppVersionRequest";
    }
}
