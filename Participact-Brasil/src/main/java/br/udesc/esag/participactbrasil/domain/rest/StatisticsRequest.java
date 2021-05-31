package br.udesc.esag.participactbrasil.domain.rest;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class StatisticsRequest extends RestRequest {

    Long taskId;

    public StatisticsRequest(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}
