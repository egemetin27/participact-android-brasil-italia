package br.com.participact.participactbrasil.modules.network.api;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.NotificationsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

public interface NotificationsAPI {

    @GET("/v2/api/v2/protected/messages/me")
    Call<NotificationsResponse> notifications(@HeaderMap Map<String, String> headers);

}
