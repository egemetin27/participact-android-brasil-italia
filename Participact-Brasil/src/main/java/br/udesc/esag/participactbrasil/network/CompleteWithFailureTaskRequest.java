package br.udesc.esag.participactbrasil.network;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class CompleteWithFailureTaskRequest extends SpringAndroidSpiceRequest<ResponseMessage> {

    private Context context;
    private TaskStatus taskStatus;


    public CompleteWithFailureTaskRequest(Context context, TaskStatus taskStatus) {
        super(ResponseMessage.class);
        this.context = context;
        this.taskStatus = taskStatus;
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {
        ResponseEntity<ResponseMessage> response = getRestTemplate().exchange(ParticipActConfiguration.COMPLETE_WITH_FAILURE_TASK_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), ResponseMessage.class,
                taskStatus.getTask().getId(), (int)taskStatus.getProgressSensingPercentual(), (int)taskStatus.getProgressPhotoPercentual(), (int)taskStatus.getProgressQuestionnairePercentual());
        return response.getBody();
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return String.format("completeWithFailureTaskRequest.%s", taskStatus.getTask().getId());
    }
}

