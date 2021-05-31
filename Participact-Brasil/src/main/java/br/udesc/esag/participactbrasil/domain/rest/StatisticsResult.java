package br.udesc.esag.participactbrasil.domain.rest;

import br.udesc.esag.participactbrasil.domain.rest.base.RestResult;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class StatisticsResult extends RestResult {

    public class Data {

        Integer participation;
        Integer completedActivities;

        public Integer getParticipation() {
            return participation;
        }

        public Integer getCompletedActivities() {
            return completedActivities;
        }
    }

    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
