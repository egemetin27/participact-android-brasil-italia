package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ScoreRestResultList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class LeaderboardRequest extends
        SpringAndroidSpiceRequest<ScoreRestResultList> {

    public static final String GLOBAL = "global";
    public static final String SOCIAL = "social";
    private final Context context;
    private String type;

    public LeaderboardRequest(String type, Context context) {
        super(ScoreRestResultList.class);
        if (type == null)
            throw new NullPointerException();
        if (context == null)
            throw new NullPointerException();
        this.type = type;
        if (this.type == null)
            this.type = GLOBAL;
        this.context = context;
    }

    @Override
    public ScoreRestResultList loadDataFromNetwork() throws Exception {
        ResponseEntity<ScoreRestResultList> response = getRestTemplate().exchange(ParticipActConfiguration.LEADERBOARD_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), ScoreRestResultList.class, type);

        return response.getBody();
    }


    public String createCacheKey() {
        return "leaderboard." + type;
    }


}
