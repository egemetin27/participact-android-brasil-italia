package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;

public class SignInResponse extends Response {

    class Item {
        Long id;
        String token;
    }

    Item item;

    public Long getId() {
        return item.id;
    }

    public String getToken() {
        return item.token;
    }

}
