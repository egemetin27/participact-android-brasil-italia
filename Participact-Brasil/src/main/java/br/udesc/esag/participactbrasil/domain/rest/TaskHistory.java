package br.udesc.esag.participactbrasil.domain.rest;

import org.joda.time.DateTime;

import java.io.Serializable;

import br.udesc.esag.participactbrasil.domain.enums.TaskState;

public class TaskHistory implements Serializable {

    private static final long serialVersionUID = 7408740264508346025L;

    private Long id;

    private TaskReport taskReport;

    private DateTime timestamp;

    private TaskState state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskReport getTaskReport() {
        return taskReport;
    }

    public void setTaskReport(TaskReport taskReport) {
        this.taskReport = taskReport;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

}

