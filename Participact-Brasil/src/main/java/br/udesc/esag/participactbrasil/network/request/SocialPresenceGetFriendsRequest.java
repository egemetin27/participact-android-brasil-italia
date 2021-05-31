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

import java.util.Set;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.SocialPresenceFriendsRequest;
import br.udesc.esag.participactbrasil.domain.rest.UserRestResultList;

public class SocialPresenceGetFriendsRequest extends SpringAndroidSpiceRequest<UserRestResultList> {

    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";

    private String socialNetwork;
    private SocialPresenceFriendsRequest socialPresenceFriendsRequest;

    public SocialPresenceGetFriendsRequest(String socialNetwork, Set<String> ids) {
        super(UserRestResultList.class);
        if (socialNetwork == null)
            throw new NullPointerException();
        if (ids == null)
            throw new NullPointerException();
        if (!(socialNetwork.equals(FACEBOOK) || socialNetwork.equals(TWITTER)))
            throw new IllegalArgumentException(socialNetwork);
        this.socialNetwork = socialNetwork;
        this.socialPresenceFriendsRequest = new SocialPresenceFriendsRequest();
        this.socialPresenceFriendsRequest.setIds(ids);
    }

    @Override
    public UserRestResultList loadDataFromNetwork() throws Exception {
        // Set the Content-Type header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        HttpEntity<SocialPresenceFriendsRequest> requestEntity = new HttpEntity<SocialPresenceFriendsRequest>(this.socialPresenceFriendsRequest, requestHeaders);


        RestTemplate restTemplate = getRestTemplate();

        // Add the Jackson and String message converters
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
        ResponseEntity<UserRestResultList> responseEntity = restTemplate.exchange(ParticipActConfiguration.SOCIAL_PRESENCE_GET_FIENDS_URL, HttpMethod.POST, requestEntity, UserRestResultList.class, socialNetwork);
        return responseEntity.getBody();
    }
}
