package br.udesc.esag.participactbrasil.support;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.octo.android.robospice.persistence.exception.SpiceException;

import org.apache.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.login.LoginActivity;

public class LoginUtility {

    private static final int NOTIFICATION_ID = 102;

    public static boolean checkIfLoginException(Context context, SpiceException e) {
        return checkIfLoginException(context, e, true);
    }

    public static boolean checkIfLoginException(Context context, SpiceException e, boolean showNotificationError) {
        if (e.getCause() instanceof HttpClientErrorException) {
            if (((HttpClientErrorException) e.getCause()).getStatusCode().value() == HttpStatus.SC_UNAUTHORIZED) {

                if (showNotificationError) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(LoginActivity.class);
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    NotificationUtility.addNotification(context, R.drawable.ic_login_err, context.getString(R.string.participact_notification), context.getString(R.string.login_again_error), NOTIFICATION_ID, resultPendingIntent);
                }
                return true;
            }
        }
        return false;
    }

}
