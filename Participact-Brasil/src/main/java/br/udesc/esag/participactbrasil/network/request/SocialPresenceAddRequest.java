package br.udesc.esag.participactbrasil.network.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.SocialPresenceRequest;

public class SocialPresenceAddRequest extends SpringAndroidSpiceRequest<Boolean> {

    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";

    private String socialNetwork;
    private SocialPresenceRequest socialPresenceRequest;


    public SocialPresenceAddRequest(String socialId, String socialNetwork) {
        super(Boolean.class);
        if (socialId == null)
            throw new NullPointerException();
        if (socialNetwork == null)
            throw new NullPointerException();
        if (!(socialNetwork.equals(FACEBOOK) || socialNetwork.equals(TWITTER)))
            throw new IllegalArgumentException(socialNetwork);
        this.socialNetwork = socialNetwork;
        this.socialPresenceRequest = new SocialPresenceRequest();
        this.socialPresenceRequest.setSocialId(socialId);
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
        // Set the Content-Type header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        HttpEntity<SocialPresenceRequest> requestEntity = new HttpEntity<SocialPresenceRequest>(this.socialPresenceRequest, requestHeaders);


        RestTemplate restTemplate = getRestTemplate();

        // Add the Jackson and String message converters
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(ParticipActConfiguration.SOCIAL_PRESENCE_ADD_URL, HttpMethod.POST, requestEntity, Boolean.class, socialNetwork);
        return responseEntity.getBody();
    }
}
