package br.udesc.esag.participactbrasil.domain.persistence.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;

public class State implements Serializable {

    private static final long serialVersionUID = 3222104785874040804L;

    private Map<Long, TaskStatus> tasks;

    public State(List<TaskStatus> list) {

        tasks = new HashMap<Long, TaskStatus>();
        for (TaskStatus taskStatus : list) {
            tasks.put(taskStatus.getTask().getId(), taskStatus);
        }

    }

    public List<TaskFlat> getTaskByState(TaskState state) {
        List<TaskFlat> result = new ArrayList<TaskFlat>();
        for (TaskStatus taskStatus : tasks.values()) {
            if (state == TaskState.ANY) {
                result.add(taskStatus.getTask());
            } else {
                if (taskStatus.getState() == state) {
                    result.add(taskStatus.getTask());
                }
            }
        }
        return result;
    }

    public List<TaskStatus> getTaskStatusByState(TaskState state) {
        List<TaskStatus> result = new ArrayList<TaskStatus>();
        for (TaskStatus taskStatus : tasks.values()) {
            if (taskStatus.getState() == state) {
                result.add(taskStatus);
            }
        }
        return result;
    }

    public TaskStatus getTaskById(Long id) {
        return tasks.get(id);
    }

    public Map<Long, TaskStatus> getTasks() {
        return tasks;
    }


}
