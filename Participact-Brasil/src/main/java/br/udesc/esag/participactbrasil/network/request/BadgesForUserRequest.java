package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.BadgeRestResultList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class BadgesForUserRequest extends
        SpringAndroidSpiceRequest<BadgeRestResultList> {

    private final long id; //if not set, the request is for the logged user
    private final Context context;

    public BadgesForUserRequest(long id, Context context) {
        super(BadgeRestResultList.class);

        if (context == null)
            throw new NullPointerException();

        this.id = id;
        this.context = context;
    }

    public BadgesForUserRequest(Context context) {
        super(BadgeRestResultList.class);
        if (context == null)
            throw new NullPointerException();
        this.id = -1; //logged user
        this.context = context;
    }

    @Override
    public BadgeRestResultList loadDataFromNetwork() throws Exception {
        String idS;
        if (id > 0)
            idS = String.valueOf(id);
        else
            idS = "me";
        ResponseEntity<BadgeRestResultList> response = getRestTemplate().exchange(ParticipActConfiguration.BADGES_FOR_USER_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), BadgeRestResultList.class, idS);
        return response.getBody();
    }

    public String createCacheKey() {
        return "badges." + id;
    }
}
