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
import br.udesc.esag.participactbrasil.domain.rest.FriendshipRestStatus;

public class FriendsPostRequest extends SpringAndroidSpiceRequest<Boolean> {

    public static final String PENDING = "pending";
    public static final String REJECTED = "rejected";
    public static final String ACCEPTED = "accepted";

    private final long id;
    private final FriendshipRestStatus friendshipRestRequest;

    public FriendsPostRequest(long id, String status) {
        super(Boolean.class);
        if (status == null)
            throw new NullPointerException();
        this.id = id;
        this.friendshipRestRequest = new FriendshipRestStatus();
        this.friendshipRestRequest.setStatus(status);
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
        // Set the Content-Type header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        HttpEntity<FriendshipRestStatus> requestEntity = new HttpEntity<FriendshipRestStatus>(this.friendshipRestRequest, requestHeaders);


        RestTemplate restTemplate = getRestTemplate();

        // Add the Jackson and String message converters
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(ParticipActConfiguration.FRIENDS_POST_URL, HttpMethod.POST, requestEntity, Boolean.class, id);
        return responseEntity.getBody();
    }

}
