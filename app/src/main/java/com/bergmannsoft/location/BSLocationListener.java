package com.bergmannsoft.location;

import android.location.Location;

import br.com.participact.participactbrasil.modules.support.UserSettings;


public abstract class BSLocationListener {

    public abstract void onLocationChanged(final Location location);

    public void onPermissionDenied(String permission) {

    }

    public void onStatusChanged(BSLocationManager.Status status) {

    }

    public void onNoProviderAvailable() {

    }

    public void onTimeout() {

    }

    public long getUpdateTimeInterval() {
        return UserSettings.getInstance().getUrbanProblemsGPSInterval();
    }

    public boolean shouldContinueUpdating() {
        return true;
    }

}