package br.com.participact.participactbrasil.modules.controller;

import android.support.v7.app.AppCompatActivity;

public abstract class Controller {

    private AppCompatActivity activity;

    public Controller(AppCompatActivity activity) {
        this.activity = activity;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }
}
