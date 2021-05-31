package br.udesc.esag.participactbrasil.domain.rest;

public class FriendshipRestStatus {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if (status == null)
            throw new NullPointerException();

        this.status = status;
    }

}
