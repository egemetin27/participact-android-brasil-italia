package br.udesc.esag.participactbrasil.domain.persistence;

import org.joda.time.DateTime;
import org.most.pipeline.Pipeline;

import java.io.Serializable;
import java.util.Set;

import br.udesc.esag.participactbrasil.domain.rest.TaskReport;

public class Task implements Serializable {

    private static final long serialVersionUID = -2904334468882835256L;

    private Long id;

    private String name;

    private String description;

    //@JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime deadline;

    private Integer points;

    Set<Action> actions;

    private Set<TaskReport> taskReport;

    private DateTime start;

    private Long duration;

    private Long sensingDuration;

    private Boolean canBeRefused;


    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long period) {
        this.duration = period;
    }

    public Long getSensingDuration() {
        return sensingDuration;
    }

    public void setSensingDuration(Long sensingDuration) {
        this.sensingDuration = sensingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(DateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }

    public Set<TaskReport> getTaskReport() {
        return taskReport;
    }

    public void setTaskReport(Set<TaskReport> taskReport) {
        this.taskReport = taskReport;
    }

    public Boolean getCanBeRefused() {
        return canBeRefused;
    }

    public void setCanBeRefused(Boolean canBeRefused) {
        this.canBeRefused = canBeRefused;
    }

    public TaskFlat convertToTaskFlat() {
        return new TaskFlat(this);
    }

    public boolean hasPipelineType(Pipeline.Type dataType) {
        if (getActions() == null) {
            return false;
        }
        for (Action a : getActions()) {
            if (a instanceof ActionSensing) {
                if (((ActionSensing) a).getInput_type() == dataType.toInt()) {
                    return true;
                }
            }
        }
        return false;
    }

}
