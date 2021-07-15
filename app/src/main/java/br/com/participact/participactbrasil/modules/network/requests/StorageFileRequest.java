package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.bergmannsoft.retrofit.ProgressRequestBody;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public abstract class StorageFileRequest {

    protected File file;

    protected abstract Map<String, String> map(Map<String, String> defaultHeaders);

    public abstract Call<Response> call(Map<String, String> defaultHeaders, Retrofit retrofit, ProgressRequestBody.ProgressCallback progress);

    protected MultipartBody.Part body(ProgressRequestBody.ProgressCallback progress) {
        ProgressRequestBody requestFile = new ProgressRequestBody(file, progress);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploadFile", file.getName(), requestFile);
        return body;
    }

}
