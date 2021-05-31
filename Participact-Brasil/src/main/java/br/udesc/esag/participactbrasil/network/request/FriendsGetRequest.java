package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.UserRestResultList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class FriendsGetRequest extends SpringAndroidSpiceRequest<UserRestResultList> {

    public static final String PENDING_SENT = "pending_sent";
    public static final String PENDING_RECEIVED = "pending_received";
    public static final String ACCEPTED = "accepted";
    private final Context context;
    private String status;

    public FriendsGetRequest(String status, Context context) {
        super(UserRestResultList.class);
        if (status == null)
            throw new NullPointerException();
        if (context == null)
            throw new NullPointerException();
        this.status = status;
        if (this.status == null)
            this.status = ACCEPTED;
        this.context = context;
    }

    @Override
    public UserRestResultList loadDataFromNetwork() throws Exception {
        ResponseEntity<UserRestResultList> response = getRestTemplate().exchange(ParticipActConfiguration.FRIENDS_GET_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), UserRestResultList.class, status);
        return response.getBody();
    }


    public String createCacheKey() {
        return "friends." + status;
    }

}
