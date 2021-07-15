package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.EmptyBodyRequest;
import br.com.participact.participactbrasil.modules.network.requests.FeedbackSendRequest;
import br.com.participact.participactbrasil.modules.network.requests.FeedbackTypeResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface FeedbackAPI {

    @POST("/v2/api/v2/protected/feedback-type")
    Call<FeedbackTypeResponse> feedbackTypes(@HeaderMap Map<String, String> headers, @Body EmptyBodyRequest request);

    @POST("/v2/api/v2/protected/feedback/submit")
    Call<Response> feedback(@HeaderMap Map<String, String> headers, @Body FeedbackSendRequest request);

}
