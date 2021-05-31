package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ForgotRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.ForgotRestResult;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestResult;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public class ForgotRequest extends BaseSpringAndroidSpiceRequest<ForgotRestResult> {

    private final Context context;
    private final ForgotRestRequest request;

    public ForgotRequest(Context context, ForgotRestRequest request) {
        super(ForgotRestResult.class);
        this.request = request;
        this.context = context;
    }

    @Override
    public ForgotRestResult loadDataFromNetwork() throws Exception {
        ResponseEntity<ForgotRestResult> responseEntity = getRestTemplate().exchange(
                ParticipActConfiguration.FORGOT_URL,
                HttpMethod.POST,
                getRequest(context, this.request),
                ForgotRestResult.class
        );
        ForgotRestResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("forgot.%s", request.getEmail());
    }

}
