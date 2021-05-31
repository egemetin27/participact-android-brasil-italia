package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.StatisticsMessage;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class StatisticsRequest extends SpringAndroidSpiceRequest<StatisticsMessage> {

    private Context context;

    public StatisticsRequest(Context context) {
        super(StatisticsMessage.class);
        context = this.context;
    }

    @Override
    public StatisticsMessage loadDataFromNetwork() throws Exception {

        ResponseEntity<StatisticsMessage> response = getRestTemplate().exchange(ParticipActConfiguration.STATISTICS_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), StatisticsMessage.class);
        return response.getBody();
    }

    public String createCacheKey() {
        return String.format("statisticsRequest.%s", UserAccountPreferences.getInstance(context).getUserAccount().getUsername());
    }
}

