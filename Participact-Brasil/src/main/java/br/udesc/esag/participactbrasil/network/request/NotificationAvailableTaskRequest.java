package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlatList;
import br.udesc.esag.participactbrasil.support.BasicAuthenticationUtility;

public class NotificationAvailableTaskRequest extends SpringAndroidSpiceRequest<Integer> {

    public final static int NO_TASK = 0;
    public final static int OPT_TASK_ONLY = 1;
    public final static int MANDATORY_TASK_ONLY = 2;
    public final static int OPT_AND_MANDATORY = 3;

    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    private TaskState state;
    private Context context;


    public NotificationAvailableTaskRequest(Context context) {
        super(Integer.class);
        this.context = context;
        this.state = TaskState.AVAILABLE;
    }

    @Override
    public Integer loadDataFromNetwork() throws Exception {
        boolean mandatory = false;
        boolean opt = false;
        ResponseEntity<TaskFlatList> taskListEntity = getRestTemplate().exchange(ParticipActConfiguration.TASK_URL, HttpMethod.GET, BasicAuthenticationUtility.getHttpEntityForAuthentication(context), TaskFlatList.class, state.toString());
        TaskFlatList taskList = taskListEntity.getBody();

        if (taskList.getList().size() == 0) {
            return NO_TASK;
        }

        for (TaskFlat task : taskList.getList()) {
            if (!task.getCanBeRefused()) {
                mandatory = true;
                AcceptTaskRequest request = new AcceptTaskRequest(context, task.getId());
                if (!contentManager.isStarted()) {
                    contentManager.start(context);
                }
                contentManager.execute(request, new NotificationAcceptMandatoryTaskListener(context, task));
            } else {
                opt = true;
            }
        }

        if (mandatory && opt) {
            return OPT_AND_MANDATORY;
        }

        if (mandatory) {
            return MANDATORY_TASK_ONLY;
        } else {
            return OPT_TASK_ONLY;
        }

    }

    public String createCacheKey() {
        return String.format("getTaskFromNotification%s", state.toString());
    }
}

