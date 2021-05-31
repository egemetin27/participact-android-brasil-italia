package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlatList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class AvailableTaskRequest extends SpringAndroidSpiceRequest<TaskFlatList> {

    private final static Logger logger = LoggerFactory.getLogger(AvailableTaskRequest.class);

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String ALL = "all";


    private TaskState state;
    private Context context;
    private String type;

    public AvailableTaskRequest(Context context, TaskState state, String type) {
        super(TaskFlatList.class);
        this.context = context;
        this.state = state;
        this.type = type;
    }

    @Override
    public TaskFlatList loadDataFromNetwork() throws Exception {
        logger.info("Executing GET task request {} state {}.", ParticipActConfiguration.TASK_URL, state);

        ResponseEntity<TaskFlatList> response = getRestTemplate().exchange(ParticipActConfiguration.TASK_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), TaskFlatList.class, type, state.toString());
        return response.getBody();
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public String createCacheKey() {
        return String.format("getTask%s%s", type, state.toString());
    }
}

