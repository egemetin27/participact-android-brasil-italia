package br.udesc.esag.participactbrasil.network.request;


import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.rest.TaskFlatMap;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

/**
 * Created by alessandro on 10/11/14.
 */
public class CreatedTaskByStateRequest extends SpringAndroidSpiceRequest<TaskFlatMap> {

    private final static Logger logger = LoggerFactory.getLogger(AvailableTaskRequest.class);

    private TaskState state;
    private Context context;


    public CreatedTaskByStateRequest(Context context, TaskState state) {
        super(TaskFlatMap.class);
        this.state = state;
        this.context = context;
    }

    @Override
    public TaskFlatMap loadDataFromNetwork() throws Exception {
        logger.info("Executing GET created task request {} state {}.", ParticipActConfiguration.CREATED_TASK_STATE_URL, state);
        ResponseEntity<TaskFlatMap> response = getRestTemplate().exchange(ParticipActConfiguration.CREATED_TASK_STATE_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), TaskFlatMap.class, state.toString());
        return response.getBody();
    }


    public TaskState getState() {
        return state;
    }


    public Context getContext() {
        return context;
    }

    public String createCacheKey() {
        return String.format("getCreatedTask%s", state.toString());
    }

}
