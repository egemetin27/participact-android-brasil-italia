package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.*;
import br.udesc.esag.participactbrasil.domain.rest.StatisticsRequest;
import br.udesc.esag.participactbrasil.network.request.base.BaseSpringAndroidSpiceRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class StatisticsParticipationRequest extends BaseSpringAndroidSpiceRequest<StatisticsResult> {

    private final StatisticsRequest request;
    private Context context;

    public StatisticsParticipationRequest(Context context, br.udesc.esag.participactbrasil.domain.rest.StatisticsRequest request) {
        super(StatisticsResult.class);
        this.context = context;
        this.request = request;
    }

    @Override
    public StatisticsResult loadDataFromNetwork() throws Exception {
        ResponseEntity<StatisticsResult> responseEntity = getRestTemplate().exchange(
                ParticipActConfiguration.STATISTICS2_URL + request.getTaskId(),
                HttpMethod.GET,
                getRequest(context, request),
                StatisticsResult.class
        );
        StatisticsResult body = responseEntity.getBody();
        return body;
    }

    public String getRequestCacheKey() {
        return String.format("stats.%s", UserAccountPreferences.getInstance(context)
                .getUserAccount().getUsername());
    }

}