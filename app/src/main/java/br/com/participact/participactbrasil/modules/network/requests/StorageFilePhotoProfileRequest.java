package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.bergmannsoft.retrofit.ProgressRequestBody;

import java.io.File;
import java.util.Map;

import br.com.participact.participactbrasil.modules.network.api.StorageFileAPI;
import retrofit2.Call;
import retrofit2.Retrofit;

public class StorageFilePhotoProfileRequest extends StorageFileRequest {

    Long profileId;

    public StorageFilePhotoProfileRequest(File file, Long profileId) {
        this.profileId = profileId;
        this.file = file;
    }

    @Override
    protected Map<String, String> map(Map<String, String> defaultHeaders) {
        if (profileId != null) {
            defaultHeaders.put("X-STORAGE-PROFILE-ID", profileId.toString());
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
