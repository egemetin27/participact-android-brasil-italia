package br.com.participact.participactbrasil.modules.network.api;

import br.com.participact.participactbrasil.modules.network.requests.IPDataResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IPDataAPI {

    @GET("/json.php")
    Call<IPDataResponse> ipData();

}
