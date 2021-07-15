package br.com.participact.participactbrasil.modules.network.requests;

import com.google.gson.annotations.SerializedName;

public class UrbanProblemsRequest {

    Double latitude;
    Double longitude;
    int count = 100000;
    int offset = 0;
    @SerializedName("start_date")
    String startDate;
    @SerializedName("end_date")
    String endDate;
    Boolean mine;

    public UrbanProblemsRequest(Double latitude, Double longitude, String startDate, String endDate, boolean mine) {
        this.latitude = latitude;
        this.longitude = longitude;
        if (mine) {
            this.mine = true;
        } else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
