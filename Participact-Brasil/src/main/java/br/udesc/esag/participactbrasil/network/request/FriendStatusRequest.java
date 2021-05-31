package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.FriendshipRestStatus;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class FriendStatusRequest extends SpringAndroidSpiceRequest<FriendshipRestStatus> {

    private final long id;
    private final Context context;

    public FriendStatusRequest(long id, Context context) {
        super(FriendshipRestStatus.class);
        if (context == null)
            throw new NullPointerException();
        this.id = id;
        this.context = context;
    }

    @Override
    public FriendshipRestStatus loadDataFromNetwork() throws Exception {
        ResponseEntity<FriendshipRestStatus> response = getRestTemplate().exchange(ParticipActConfiguration.FRIEND_STATUS_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), FriendshipRestStatus.class, id);
        return response.getBody();
    }

}
