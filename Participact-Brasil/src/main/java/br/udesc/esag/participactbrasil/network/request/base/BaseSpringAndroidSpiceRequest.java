package br.udesc.esag.participactbrasil.network.request.base;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.support.Base64;
import org.springframework.web.client.RestTemplate;

import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.rest.RestRequest;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public abstract class BaseSpringAndroidSpiceRequest<RESULT> extends SpringAndroidSpiceRequest<RESULT> {

    public BaseSpringAndroidSpiceRequest(Class<RESULT> clazz) {
        super(clazz);
    }

    protected <RESULT extends RestRequest> HttpEntity<RESULT> getRequest(Context context, RESULT request) {
        HttpHeaders requestHeaders = new HttpHeaders();

        if (UserAccountPreferences.getInstance(context).isUserAccountValid() && (!(request instanceof SignUpRestRequest))) {
            UserAccount user = UserAccountPreferences.getInstance(context).getUserAccount();
            String authStr = String.format("%s:%s", user.getUsername(), user.getPassword());
            String authEncoded = Base64.encodeBytes(authStr.getBytes());
            requestHeaders.add("Authorization", "Basic " + authEncoded);
        }

        requestHeaders.setContentType(new MediaType("application", "json"));
        HttpEntity<RESULT> requestEntity = new HttpEntity<>(request, requestHeaders);

        return requestEntity;
    }

    @Override
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = super.getRestTemplate();
        // Add the Jackson and String message converters
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        return restTemplate;
    }
}
