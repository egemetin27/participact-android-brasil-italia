package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.participact.participactbrasil.modules.db.PANotification;

public class NotificationsResponse extends Response {

    @SerializedName("item")
    List<PANotification> notifications;

    public List<PANotification> getNotifications() {
        return notifications;
    }
}
