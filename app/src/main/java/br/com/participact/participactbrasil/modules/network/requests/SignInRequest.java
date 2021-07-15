package br.com.participact.participactbrasil.modules.network.requests;

public class SignInRequest {

    String username;
    String password;

    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
