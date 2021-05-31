package br.udesc.esag.participactbrasil.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.local.TaskStatus;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;

public class State implements Serializable {

    private static final long serialVersionUID = 5269547844542264323L;

    private Map<Long, TaskStatus> tasks;

    public State() {
        tasks = new HashMap<Long, TaskStatus>();
    }

    public void addTask(TaskFlat task) {
        TaskStatus wrapper = new TaskStatus(task, TaskState.UNKNOWN);
        getTasks().put(task.getId(), wrapper);
    }

    public void removeTask(TaskFlat task) {
        getTasks().remove(task.getId());
    }

    public void changeState(TaskFlat task, TaskState newState) {

        if (!getTasks().containsKey(task.getId()))
            return;

        getTasks().get(task.getId()).setState(newState);

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

    public void setTasks(Map<Long, TaskStatus> tasks) {
        this.tasks = tasks;
    }


}
