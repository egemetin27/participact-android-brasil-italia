package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.Map;

import br.com.participact.participactbrasil.modules.network.requests.QRCodeRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface QRCodeAPI {

    @POST("/v2/api/v2/protected/qrcode")
    Call<Response> confirmToken(@HeaderMap Map<String, String> headers, @Body QRCodeRequest request);

}
