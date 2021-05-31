package br.udesc.esag.participactbrasil.network.request;


import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.support.Base64;

import java.util.Map;

import br.com.bergmannsoft.util.DeviceUtils;
import br.com.bergmannsoft.util.SensorUtils;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.ParticipActConfiguration;

public class LoginRequest extends SpringAndroidSpiceRequest<Boolean> {

//    private final String name;
//    private String social;
//    private String city;
//    private String country;

    private String email;
    private String password;
    private String sensors;
    private String device;

    public LoginRequest(String email, String password/*, String social, String socialName, String city, String country*/) {
        super(Boolean.class);
        this.email = email;
        this.password = password;
        this.sensors = SensorUtils.getAvailableSensors(ParticipActApplication.getInstance().getApplicationContext());
        Log.d("LoginRequest", this.sensors);
        this.device = DeviceUtils.getDeviceInfo();
//        this.social = social;
//        this.name = socialName;
//        this.city = city;
//        this.country = country;
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        //Basic Authentication
        String authStr = String.format("%s:%s", email, password);
        String authEncoded = Base64.encodeBytes(authStr.getBytes());
        httpHeaders.add("Authorization", "Basic " + authEncoded);
        httpHeaders.add("AvailableSensors", sensors);

        ResponseEntity<Boolean> response = getRestTemplate().exchange(
                ParticipActConfiguration.LOGIN_URL + "&device=" + device,
                HttpMethod.GET,
                new HttpEntity<Map<Object, Object>>(httpHeaders),
                Boolean.class,
                device
//                social,
//                name,
//                city,
//                country
        );

        return response.getBody();
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return String.format("login.%s", email);
    }
}

