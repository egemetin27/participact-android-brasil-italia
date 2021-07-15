package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedbackTypeResponse extends Response {

    @SerializedName("item")
    List<FeedbackType> types;

    public List<FeedbackType> getTypes() {
        return types;
    }
}
