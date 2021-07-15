package br.com.participact.participactbrasil.modules.network.api;

import com.bergmannsoft.rest.Response;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface StorageFileAPI {

    @Multipart
    @POST("/v2/api/v2/protected/storage-file/audio")
    Call<Response> uploadAudio(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part file);

    @Multipart
    @POST("/v2/api/v2/protected/storage-file/video")
    Call<Response> uploadVideo(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part file);

    @Multipart
    @POST("/v2/api/v2/protected/storage-file/image")
    Call<Response> uploadPhoto(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part file);

}
