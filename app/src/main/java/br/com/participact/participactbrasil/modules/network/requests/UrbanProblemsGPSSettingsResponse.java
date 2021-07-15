package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;

public class UrbanProblemsGPSSettingsResponse extends Response {

    class Item {
        Long intervalTime;
        Boolean enabled;
    }

    Item item;

    public long getIntervalTime() {
        return item != null && item.intervalTime != null ? item.intervalTime : 60000*6;
    }

    public boolean isEnabled() {
        return item != null && item.enabled != null ? item.enabled : true;
    }

}
