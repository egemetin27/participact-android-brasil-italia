package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.springframework.util.support.Base64;

import java.util.UUID;

import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public abstract class ApacheHttpSpiceRequest<RESULT> extends SpiceRequest<RESULT> {

    private static final String HEADER_KEY = "Request_key";

    HttpClient httpClient;
    Context context;
    String key;

    public ApacheHttpSpiceRequest(Context context, Class<RESULT> clazz) {
        super(clazz);
        this.setContext(context);
        key = UUID.randomUUID().toString();

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpPost getHttpPost(String URL) {
        HttpPost httppost = new HttpPost(URL);
        // for basic Autentication
        UserAccount user = UserAccountPreferences.getInstance(context).getUserAccount();
        String authStr = String.format("%s:%s", user.getUsername(), user.getPassword());
        String authEncoded = Base64.encodeBytes(authStr.getBytes());
        httppost.setHeader("Authorization", "Basic " + authEncoded);
        httppost.setHeader(HEADER_KEY, key);
        return httppost;
    }

}
