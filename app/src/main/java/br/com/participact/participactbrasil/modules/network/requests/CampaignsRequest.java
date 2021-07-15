package br.com.participact.participactbrasil.modules.network.requests;

public class CampaignsRequest {

    Integer count = 50;
    Integer offset = 0;
    String version;

    public CampaignsRequest(String version) {
        this.version = version;
    }

}
