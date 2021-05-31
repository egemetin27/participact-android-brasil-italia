package br.udesc.esag.participactbrasil.domain.local;

import java.io.Serializable;

public class ImageDescriptor implements Serializable {

    private static final long serialVersionUID = 7614646837877329294L;

    String imagePath;
    private String imageName;
    Long taskId;
    Long actionId;
    private Long sampleTimestamp;
    private boolean uploaded;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Long getSampleTimestamp() {
        return sampleTimestamp;
    }

    public void setSampleTimestamp(Long sampleTimestamp) {
        this.sampleTimestamp = sampleTimestamp;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}
