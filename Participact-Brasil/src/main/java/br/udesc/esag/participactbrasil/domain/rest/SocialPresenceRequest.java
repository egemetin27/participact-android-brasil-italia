package br.udesc.esag.participactbrasil.domain.rest;

public class SocialPresenceRequest {

    private String socialId;

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {

        if (socialId == null)
            throw new NullPointerException();

        this.socialId = socialId;
    }
}
