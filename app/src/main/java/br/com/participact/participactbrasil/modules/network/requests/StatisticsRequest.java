package br.com.participact.participactbrasil.modules.network.requests;

public class StatisticsRequest {

    Double latitude;
    Double longitude;
    Integer count;
    Integer offset;

    public StatisticsRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        count = 100000;
        offset = 0;
    }
}
