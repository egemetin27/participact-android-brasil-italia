package br.com.participact.participactbrasil.modules.network.api;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.SignInRequest;
import br.com.participact.participactbrasil.modules.network.requests.SignInResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface LoginAPI {

    @POST("/v2/api/v2/protected/login")
    Call<SignInResponse> signIn(@HeaderMap Map<String, String> headers, @Body SignInRequest request);


    @POST("/v2/api/v2/public/guest")
    Call<SignInResponse> guest(@HeaderMap Map<String, String> headers, @Body SignInRequest request);

}
