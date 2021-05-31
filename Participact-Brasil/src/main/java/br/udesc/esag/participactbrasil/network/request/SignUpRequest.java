package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestResult;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestRequest;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public class SignUpRequest extends BaseSpringAndroidSpiceRequest<SignUpRestResult> {

    private final Context context;
    private final SignUpRestRequest request;

    public SignUpRequest(Context context, SignUpRestRequest request) {
        super(SignUpRestResult.class);
        this.request = request;
        this.context = context;
    }

    @Override
    public SignUpRestResult loadDataFromNetwork() throws Exception {
        ResponseEntity<SignUpRestResult> responseEntity = getRestTemplate().exchange(
                ParticipActConfiguration.SIGNUP_URL,
                HttpMethod.POST,
                getRequest(context, this.request),
                SignUpRestResult.class
        );
        SignUpRestResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("signup.%s", request.getEmail());
    }

}
