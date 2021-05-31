package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.TwitterStatusList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class TwitterRequest extends SpringAndroidSpiceRequest<TwitterStatusList> {

//	private static final String OAUTH_CONSUMER_KEY = "6RXduk1K0qVp1KySa2jwgA";
//	private static final String OAUTH_CONSUMER_SECRET = "DG7cH3h2YsCHFj6A23qqozZwbiDNN0VAkUd9cCsaq0";
//	private static final String OAUTH_ACCESS_TOKEN = "1433054970-1uzccYY2iEZ9t5RfjgjP5wwcat9RQUafnvhC16U";
//	private static final String OAUTH_ACCESS_TOKEN_SECRET = "X2fWvhTqWnkN0ajNrHvZ2mQLk2ASyaBy6kISYtC6cs";

    private Context context;

    public TwitterRequest(Context context) {
        super(TwitterStatusList.class);
        this.context = context;
    }

    @Override
    public TwitterStatusList loadDataFromNetwork() throws Exception {

        ResponseEntity<TwitterStatusList> response = getRestTemplate().exchange(ParticipActConfiguration.TWITTER_URL,
                HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context),
                TwitterStatusList.class);
        return response.getBody();
    }


    public String createCacheKey() {
        return "tweets.participact";
    }
}