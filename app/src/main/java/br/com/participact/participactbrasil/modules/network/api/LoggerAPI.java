package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import br.com.participact.participactbrasil.modules.db.Log;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoggerAPI {

    @POST("/users")
    Call<Response> logger(@Body Log request);


}
