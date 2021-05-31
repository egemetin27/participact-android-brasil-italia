package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.PagesRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.PagesRestResult;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestResult;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public class PagesRequest extends BaseSpringAndroidSpiceRequest<PagesRestResult> {

    private final Context context;
    private final PagesRestRequest request;

    public PagesRequest(Context context, PagesRestRequest request) {
        super(PagesRestResult.class);
        this.request = request;
        this.context = context;
    }

    @Override
    public PagesRestResult loadDataFromNetwork() throws Exception {
        ResponseEntity<PagesRestResult> responseEntity = getRestTemplate().exchange(
                ParticipActConfiguration.PAGES_URL,
                HttpMethod.GET,
                null,
                PagesRestResult.class
        );
        PagesRestResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("pages.%s", UserAccountPreferences.getInstance(context).getUserAccount().getUsername());
    }

}
