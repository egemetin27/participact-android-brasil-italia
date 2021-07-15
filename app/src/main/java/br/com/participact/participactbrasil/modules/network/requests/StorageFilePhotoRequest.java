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

public class StorageFilePhotoRequest extends StorageFileRequest {

    private Long answerGroupId;
    Long reportId;
    Long campaignId;
    Long actionId;
    Long questionId;
    Double latitude;
    Double longitude;
    String provider;
    String ipAddress;

    public StorageFilePhotoRequest(File file, Long reportId) {
        this.reportId = reportId;
        this.file = file;
    }

    public StorageFilePhotoRequest(File file, Long campaignId, Long actionId, Long questionId, Double latitude, Double longitude, String provider, String ipAddress, Long answerGroupId) {
        this.campaignId = campaignId;
        this.actionId = actionId;
        this.questionId = questionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.ipAddress = ipAddress;
        this.answerGroupId = answerGroupId;
        this.file = file;
    }

    @Override
    protected Map<String, String> map(Map<String, String> defaultHeaders) {
        if (reportId != null) {
            defaultHeaders.put("X-STORAGE-REPORT-ID", reportId.toString());
        }
        if (campaignId != null) {
            defaultHeaders.put("X-STORAGE-TASK-ID", campaignId.toString());
        }
        if (actionId != null) {
            defaultHeaders.put("X-STORAGE-ACTION-ID", actionId.toString());
        }
        if (questionId != null) {
            defaultHeaders.put("X-STORAGE-QUESTION-ID", questionId.toString());
        }
        if (latitude != null) {
            defaultHeaders.put("X-STORAGE-LATITUDE", latitude.toString());
        }
        if (longitude != null) {
            defaultHeaders.put("X-STORAGE-LONGITUDE", longitude.toString());
        }
        if (provider != null) {
            defaultHeaders.put("X-STORAGE-PROVIDER", provider);
        }
        if (ipAddress != null) {
            defaultHeaders.put("X-STORAGE-IP-ADDRESS", ipAddress);
        }
        if (answerGroupId != null) {
            defaultHeaders.put("X-STORAGE-ANSWER-GROUP-ID", answerGroupId.toString());
        }
        defaultHeaders.put("X-STORAGE-TIMESTAMP", String.valueOf(System.currentTimeMillis() / 1000));
        return defaultHeaders;
    }

    @Override
    public Call<Response> call(Map<String, String> defaultHeaders, Retrofit retrofit, ProgressRequestBody.ProgressCallback progress) {
        StorageFileAPI api = retrofit.create(StorageFileAPI.class);
        return api.uploadPhoto(map(defaultHeaders), body(progress));
    }

}
