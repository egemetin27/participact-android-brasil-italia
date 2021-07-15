package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.CampaignsRequest;
import br.com.participact.participactbrasil.modules.network.requests.CampaignsResponse;
import br.com.participact.participactbrasil.modules.network.requests.EndRequest;
import br.com.participact.participactbrasil.modules.network.requests.ParticipateRequest;
import br.com.participact.participactbrasil.modules.network.requests.ParticipateResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface CampaignsAPI {

    @POST("/v2/api/v2/protected/campaigns")
    Call<CampaignsResponse> campaigns(@HeaderMap Map<String, String> headers, @Body CampaignsRequest request);

    @POST("/v2/api/v2/protected/campaigns/deleted")
    Call<CampaignsResponse> campaignsDeleted(@HeaderMap Map<String, String> headers, @Body CampaignsRequest request);

    @POST("/v2/api/v2/protected/campaigns/accept")
    Call<ParticipateResponse> accept(@HeaderMap Map<String, String> headers, @Body ParticipateRequest request);

    @POST("/v2/api/v2/protected/campaigns/complete")
    Call<Response> complete(@HeaderMap Map<String, String> headers, @Body EndRequest request);

    @POST("/v2/api/v2/protected/campaigns/incomplete")
    Call<Response> incomplete(@HeaderMap Map<String, String> headers, @Body EndRequest request);

}
