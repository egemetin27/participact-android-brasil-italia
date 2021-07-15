package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

public class UrbanProblemSendResponse extends Response {

    @SerializedName("outcome")
    Long reportId;

    public Long getReportId() {
        return reportId;
    }
}
