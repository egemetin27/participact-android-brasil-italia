package br.udesc.esag.participactbrasil.domain.rest;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public class ForgotRestRequest extends RestRequest {

    private String email;
    private String locale;

    public ForgotRestRequest(String email) {
        this.email = email;
        this.locale = "pt_BR";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
