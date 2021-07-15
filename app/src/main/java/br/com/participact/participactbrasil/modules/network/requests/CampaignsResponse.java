package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.participact.participactbrasil.modules.db.Campaign;

public class CampaignsResponse extends Response {

    @SerializedName("tasks")
    List<Campaign> campaigns;
    String version;

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public String getVersion() {
        return version;
    }
}
