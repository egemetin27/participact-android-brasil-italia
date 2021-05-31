package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.MeRestResult;
import br.udesc.esag.participactbrasil.domain.rest.NotificationsResult;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class NotificationsRequest extends BaseSpringAndroidSpiceRequest<NotificationsResult> {

    private Context context;

    public NotificationsRequest(Context context) {
        super(NotificationsResult.class);
        this.context = context;
    }

    @Override
    public NotificationsResult loadDataFromNetwork() throws Exception {
        ResponseEntity<NotificationsResult> responseEntity = getRestTemplate().exchange(ParticipActConfiguration.NOTIFICATIONS_URL, HttpMethod.GET, null, NotificationsResult.class);
        NotificationsResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("me.%s", UserAccountPreferences.getInstance(context)
                .getUserAccount().getUsername());
    }

}