package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.UserRestResult;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class UserRequest extends SpringAndroidSpiceRequest<UserRestResult> {

    private final long id; //if not setted, the request is for the logged user
    private final Context context;

    public UserRequest(long id, Context context) {
        super(UserRestResult.class);
        if (context == null)
            throw new NullPointerException();
        this.id = id;
        this.context = context;
    }

    public UserRequest(Context context) {
        super(UserRestResult.class);
        if (context == null)
            throw new NullPointerException();
        this.id = -1; //logged user
        this.context = context;
    }

    @Override
    public UserRestResult loadDataFromNetwork() throws Exception {
        String idS;
        if (id > 0)
            idS = String.valueOf(id);
        else
            idS = "me";
        ResponseEntity<UserRestResult> response = getRestTemplate().exchange(ParticipActConfiguration.USER_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), UserRestResult.class, idS);
        return response.getBody();
    }

    public String createCacheKey() {
        return "user." + id;
    }


}
