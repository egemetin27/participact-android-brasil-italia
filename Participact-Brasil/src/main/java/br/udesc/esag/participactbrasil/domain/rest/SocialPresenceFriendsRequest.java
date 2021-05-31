package br.udesc.esag.participactbrasil.domain.rest;

import java.util.Set;

public class SocialPresenceFriendsRequest {

    Set<String> ids;

    public Set<String> getIds() {
        return ids;
    }

    public void setIds(Set<String> ids) {

        if (ids == null)
            throw new NullPointerException();

        this.ids = ids;
    }
}
