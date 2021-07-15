package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.participact.participactbrasil.modules.support.UrbanProblem;

public class UrbanProblemsResponse extends Response {

    @SerializedName("item")
    List<UrbanProblem> items;

    public List<UrbanProblem> getItems() {
        return items;
    }
}
