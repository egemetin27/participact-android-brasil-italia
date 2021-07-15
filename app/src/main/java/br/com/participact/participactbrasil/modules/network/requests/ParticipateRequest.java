package br.com.participact.participactbrasil.modules.network.requests;

import com.google.gson.annotations.SerializedName;

public class ParticipateRequest {

    @SerializedName("taskId")
    Long campaignId;

    public ParticipateRequest(Long campaignId) {
        this.campaignId = campaignId;
    }
}
