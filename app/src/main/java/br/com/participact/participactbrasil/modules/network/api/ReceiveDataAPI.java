package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerChooseRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerTextRequest;
import br.com.participact.participactbrasil.modules.network.requests.sensing.DataRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ReceiveDataAPI {

    @POST("/v2/api/v2/protected/receive-data/location")
    Call<Response> location(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/accelerometer")
    Call<Response> accelerometer(@HeaderMap Map<String, String> headers, @Body DataRequest request);



    @POST("/v2/api/v2/protected/receive-data/accelerometerclassifier")
    Call<Response> accelerometerClassifier(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/activityrecognitioncompare")
    Call<Response> activityRecognitionCompare(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/apponscreen")
    Call<Response> appOnScreen(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/connectiontype")
    Call<Response> connectionType(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/googleactivityrecognition")
    Call<Response> googleActivityRecognition(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/battery")
    Call<Response> battery(@HeaderMap Map<String, String> headers, @Body DataRequest request);

    @POST("/v2/api/v2/protected/receive-data/gyroscope")
    Call<Response> gyroscope(@HeaderMap Map<String, String> headers, @Body DataRequest request);




    @POST("/v2/api/v2/protected/receive-data/question")
    Call<Response> questionText(@HeaderMap Map<String, String> headers, @Body QuestionAnswerTextRequest request);

    @POST("/v2/api/v2/protected/receive-data/question")
    Call<Response> questionChoose(@HeaderMap Map<String, String> headers, @Body QuestionAnswerChooseRequest request);

//    @POST("/res-tful/debug")
//    Call<Response> debugQuestionText(@HeaderMap Map<String, String> headers, @Body QuestionAnswerTextRequest request);
//
//    @POST("/res-tful/debug")
//    Call<Response> debugQuestionChoose(@HeaderMap Map<String, String> headers, @Body QuestionAnswerChooseRequest request);

}
