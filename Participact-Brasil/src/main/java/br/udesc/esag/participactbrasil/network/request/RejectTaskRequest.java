package br.udesc.esag.participactbrasil.network.request;


import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class RejectTaskRequest extends SpringAndroidSpiceRequest<ResponseMessage> {

    private final static Logger logger = LoggerFactory.getLogger(RejectTaskRequest.class);

    private Context context;
    private Long taskId;

    public RejectTaskRequest(Context context, Long taskId) {
        super(ResponseMessage.class);
        this.context = context;
        this.taskId = taskId;
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {
        logger.info("Executing reject task request of task with id {}.", taskId);
//       return getRestTemplate().getForObject(ParticipActConfiguration.REJECT_TASK_URL, ResponseMessage.class, user, password, taskId);
        ResponseEntity<ResponseMessage> response = getRestTemplate().exchange(ParticipActConfiguration.REJECT_TASK_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), ResponseMessage.class, taskId);
        return response.getBody();

    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return String.format("rejectTaskRequest.%s", UserAccountPreferences.getInstance(context).getUserAccount().getUsername());
    }
}

