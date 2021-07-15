package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.participact.participactbrasil.modules.support.UPCategory;

public class UPCategoriesResponse extends Response {

    @SerializedName("item")
    private List<UPCategory> categories;

    public List<UPCategory> getCategories() {
        return categories;
    }
}
