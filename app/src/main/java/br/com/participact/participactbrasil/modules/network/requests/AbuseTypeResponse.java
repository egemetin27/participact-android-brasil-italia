package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.participact.participactbrasil.modules.db.AbuseType;

public class AbuseTypeResponse extends Response {

    @SerializedName("item")
    private List<AbuseType> types;

    public List<AbuseType> getTypes() {
        return types;
    }
}
