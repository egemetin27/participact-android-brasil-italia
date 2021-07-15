package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.bergmannsoft.retrofit.ProgressRequestBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import br.com.participact.participactbrasil.modules.network.api.StorageFileAPI;
import br.com.participact.participactbrasil.modules.network.requests.StorageFileRequest;
import retrofit2.Call;
import retrofit2.Retrofit;

public class StorageFileVideoRequest extends StorageFileRequest {

    Long reportId;
    Integer duration;

    public StorageFileVideoRequest(File file, Long reportId, Integer duration) {
        this.reportId = reportId;
        this.duration = duration;
        this.file = file;
    }

    @Override
    protected Map<String, String> map(Map<String, String> defaultHeaders) {
        defaultHeaders.put("X-STORAGE-REPORT-ID", reportId.toString());
        defaultHeaders.put("X-STORAGE-DURATION", duration.toString());
        defaultHeaders.put("X-STORAGE-TIMESTAMP", String.valueOf(System.currentTimeMillis() / 1000));
        return defaultHeaders;
    }

    @Override
    public Call<Response> call(Map<String, String> defaultHeaders, Retrofit retrofit, ProgressRequestBody.ProgressCallback progress) {
        StorageFileAPI api = retrofit.create(StorageFileAPI.class);
        return api.uploadVideo(map(defaultHeaders), body( progress));
    }

}
