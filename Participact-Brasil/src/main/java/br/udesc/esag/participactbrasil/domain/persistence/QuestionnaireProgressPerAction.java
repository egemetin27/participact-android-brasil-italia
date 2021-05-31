package br.udesc.esag.participactbrasil.domain.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class QuestionnaireProgressPerAction implements Serializable {

    private static final long serialVersionUID = 2543115181050810886L;

    @DatabaseField(generatedId = true)
    Long id;

    @DatabaseField(foreign = true)
    ActionFlat action;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
    TaskStatus taskStatus;

    @DatabaseField
    Boolean done;

    public QuestionnaireProgressPerAction() {
        done = false;
    }

    public QuestionnaireProgressPerAction(ActionFlat action, Boolean done) {
        super();
        this.action = action;
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionFlat getAction() {
        return action;
    }

    public void setAction(ActionFlat action) {
        this.action = action;
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }


}
