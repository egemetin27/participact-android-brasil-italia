package br.udesc.esag.participactbrasil.support;

import android.content.Context;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.support.Base64;

import java.util.Map;

import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class BasicAuthenticationUtility {

    /**
     * @author Bergmann
     *
     * Before, this class was holding a static instance of authorization entity.
     * It was causing a problem, because if the user signs out and signs in back with a different user,
     * The authorization entity in this class would continue the same, with previous credentials.
     *
     * It also was causing a 401 error on server side.
     *
     * The solution was to remove static instance and always create the authorization entity.
     *
     */

    /**
     * Returns authorization entity.
     * @param context
     * @return
     */
    public static HttpEntity<Map<Object, Object>> getHttpEntityForAuthentication(Context context) {
        HttpHeaders httpHeaders = new HttpHeaders();
        //Basic Authentication
        UserAccount user = UserAccountPreferences.getInstance(context).getUserAccount();
        String authStr = String.format("%s:%s", user.getUsername(), user.getPassword());
        String authEncoded = Base64.encodeBytes(authStr.getBytes());
        httpHeaders.add("Authorization", "Basic " + authEncoded);
        return new HttpEntity<>(httpHeaders);
    }

    public static UserAccount getUserForAuthentication(Context context) {
        UserAccount user;
        user = UserAccountPreferences.getInstance(context).getUserAccount();
        return user;
    }
}
