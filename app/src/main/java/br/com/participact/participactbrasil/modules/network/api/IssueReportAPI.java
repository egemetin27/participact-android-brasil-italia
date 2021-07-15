package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.AbuseSubmitRequest;
import br.com.participact.participactbrasil.modules.network.requests.AbuseTypeRequest;
import br.com.participact.participactbrasil.modules.network.requests.AbuseTypeResponse;
import br.com.participact.participactbrasil.modules.network.requests.StatisticsRequest;
import br.com.participact.participactbrasil.modules.network.requests.StatisticsResponse;
import br.com.participact.participactbrasil.modules.network.requests.UPCategoriesRequest;
import br.com.participact.participactbrasil.modules.network.requests.UPCategoriesResponse;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemSendRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemSendResponse;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface IssueReportAPI {

    @POST("/v2/api/v2/protected/issue-report/category")
    Call<UPCategoriesResponse> categories(@HeaderMap Map<String, String> headers, @Body UPCategoriesRequest request);

    @POST("/v2/api/v2/protected/issue-report/submit")
    Call<UrbanProblemSendResponse> submit(@HeaderMap Map<String, String> headers, @Body UrbanProblemSendRequest request);

    @POST("/v2/api/v2/protected/issue-report")
    Call<UrbanProblemsResponse> urbanProblems(@HeaderMap Map<String, String> headers, @Body UrbanProblemsRequest request);

    @POST("/v2/api/v2/protected/issue-report/stats")
    Call<StatisticsResponse> statistics(@HeaderMap Map<String, String> headers, @Body StatisticsRequest request);

    @POST("/v2/api/v2/protected/issue-report/abuse")
    Call<AbuseTypeResponse> abuseTypes(@HeaderMap Map<String, String> headers, @Body AbuseTypeRequest request);

    @POST("/v2/api/v2/protected/issue-report/abuse/submit")
    Call<Response> abuseSubmit(@HeaderMap Map<String, String> headers, @Body AbuseSubmitRequest request);

}
