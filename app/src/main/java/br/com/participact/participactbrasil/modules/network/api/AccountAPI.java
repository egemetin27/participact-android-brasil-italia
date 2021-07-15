package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.MeResponse;
import br.com.participact.participactbrasil.modules.network.requests.ProfileRequest;
import br.com.participact.participactbrasil.modules.network.requests.SendFacebookTokenRequest;
import br.com.participact.participactbrasil.modules.network.requests.SendGoogleTokenRequest;
import br.com.participact.participactbrasil.modules.network.requests.SendRegIdRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface AccountAPI {

    @POST("/v2/api/v2/protected/account/facebook")
    Call<Response> facebookToken(@HeaderMap Map<String, String> headers, @Body SendFacebookTokenRequest request);

    @POST("/v2/api/v2/protected/account/google")
    Call<Response> googleToken(@HeaderMap Map<String, String> headers, @Body SendGoogleTokenRequest request);

    @POST("/v2/api/v2/protected/account/regid")
    Call<Response> sendRegid(@HeaderMap Map<String, String> headers, @Body SendRegIdRequest request);

    @POST("/v2/api/v2/protected/account/submit")
    Call<Response> updateProfile(@HeaderMap Map<String, String> headers, @Body ProfileRequest request);

    @GET("/v2/api/v2/protected/account/me")
    Call<MeResponse> me(@HeaderMap Map<String, String> headers);

}
