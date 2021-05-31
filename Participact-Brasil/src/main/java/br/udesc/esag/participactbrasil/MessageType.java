package br.udesc.esag.participactbrasil;

import br.com.bergmannsoft.application.BMessageType;

/**
 * Created by fabiobergmann on 18/10/16.
 */

public interface MessageType extends BMessageType {

    int GCM_NOTIFICATION_NEW_TASK = 1000;

    int CAMPAIGN_ACCEPTED = 1001;

    int EDIT_PROFILE = 1002;
    int EDIT_PROFILE_SAVED = 1003;
    int MANDATORY_CAMPAIGN_ACCEPTED = 1004;

    int TASK_UPDATED = 1005;
}
