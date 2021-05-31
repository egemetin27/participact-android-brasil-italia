package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class CompleteWithSuccessTaskRequest extends SpringAndroidSpiceRequest<ResponseMessage> {

    private Context context;
    private Long taskId;

    public CompleteWithSuccessTaskRequest(Context context, Long taskId) {
        super(ResponseMessage.class);
        this.context = context;
        this.taskId = taskId;
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {
        ResponseEntity<ResponseMessage> response = getRestTemplate().exchange(ParticipActConfiguration.COMPLETE_WITH_SUCCESS_TASK_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), ResponseMessage.class, taskId);
        return response.getBody();
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return String.format("completeWithSuccessTaskRequest.%s", taskId);
    }
}

