package br.udesc.esag.participactbrasil.domain.rest;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import br.udesc.esag.participactbrasil.domain.data.Data;


public class TaskResult implements Serializable {

    private static final long serialVersionUID = -8735101887225128L;

    private Long id;

    private TaskReport taskReport;

    private Set<Data> data = new HashSet<Data>();

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

    public Set<Data> getData() {
        return data;
    }

    public void setData(Set<Data> data) {
        this.data = data;
    }

}
