package br.udesc.esag.participactbrasil.domain.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class RemainingPhotoPerAction implements Serializable {

    private static final long serialVersionUID = 2543115181050810886L;

    @DatabaseField(generatedId = true)
    Long id;

    @DatabaseField(foreign = true)
    ActionFlat action;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
    TaskStatus taskStatus;

    @DatabaseField
    int remaingPhoto;

    public RemainingPhotoPerAction() {

    }

    public RemainingPhotoPerAction(ActionFlat action, int remaingPhoto) {
        super();
        this.action = action;
        this.remaingPhoto = remaingPhoto;
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

    public int getRemaingPhoto() {
        return remaingPhoto;
    }

    public void setRemaingPhoto(int remaingPhoto) {
        this.remaingPhoto = remaingPhoto;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

}
