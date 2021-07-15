package br.com.participact.participactbrasil.modules.network.api;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsGPSSettingsRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsGPSSettingsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface GeneralAPI {

    @POST("/v2/api/v2/public/general/issue-setting")
    Call<UrbanProblemsGPSSettingsResponse> settings(@HeaderMap Map<String, String> headers, @Body UrbanProblemsGPSSettingsRequest request);

}
