package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.enums.TaskValutation;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlatList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

/**
 * Created by alessandro on 11/11/14.
 */
public class TaskCreatedRequest extends SpringAndroidSpiceRequest<TaskFlatList> {


    private Context context;
    private TaskValutation valutation;

    private final static Logger logger = LoggerFactory.getLogger(AvailableTaskRequest.class);


    public TaskCreatedRequest(Context context, TaskValutation valutation) {
        super(TaskFlatList.class);
        this.context = context;
        this.valutation = valutation;
    }

    @Override
    public TaskFlatList loadDataFromNetwork() throws Exception {
        logger.info("Executing GET task request {} state {}.", ParticipActConfiguration.CREATED_TASK_URL, valutation);
        ResponseEntity<TaskFlatList> response = getRestTemplate().exchange(ParticipActConfiguration.CREATED_TASK_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), TaskFlatList.class, valutation.toString());
        return response.getBody();


    }

    public String createCacheKey() {
        return String.format("getCreatedTask%s", valutation.toString());
    }

}
