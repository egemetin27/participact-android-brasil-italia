package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.dashboard.DashboardActivity;
import br.udesc.esag.participactbrasil.domain.enums.PANotification;
import br.udesc.esag.participactbrasil.services.NetworkService;
import br.udesc.esag.participactbrasil.support.NotificationUtility;
import br.udesc.esag.participactbrasil.support.preferences.ChangeTimePreferences;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderLogPreferences;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderStatePreferences;

public class GcmBroadcastReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(GcmBroadcastReceiver.class);

    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_NEW_TASK = 2;
    public static final int NOTIFICATION_NEW_TASK_ACCEPTED = 3;
    public static final int NOTIFICATION_NEW_VERSION = 4;
    public static final int NOTIFICATION_NEW_TASK_TIME_ERR = 5;
    public static final int NOTIFICATION_TIME_OK = 6;
    public static final int NOTIFICATION_TIME_ERR = 7;
    public static final int NOTIFICATION_NEW_FRIEND = 8;
    public static final int NOTIFICATION_NEW_BADGE = 9;
    public static final int NOTIFICATION_NEW_TASKUSER_APPROVED = 10;
    public static final int NOTIFICATION_NEW_TASKUSER_REFUSED = 11;
    public static final int NOTIFICATION_MESSAGE = 12;


    private NotificationManager mNotificationManager;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            logger.error("GCM MESSAGE_TYPE_SEND_ERROR: {}.", intent.getExtras().toString());
            // sendNotification("Error Message: " +
            // intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            // sendNotification("Deleted messages on server: " +
            // intent.getExtras().toString());
            logger.warn("GCM MESSAGE_TYPE_DELETED: {}.", intent.getExtras().toString());
        } else {


            Log.d("GCM", intent.getExtras().toString());
            try {

                PANotification.Type type = PANotification.Type.valueOf(intent.getExtras().getString(PANotification.KEY));

                switch (type) {
                    case NEW_TASK:
                        logger.info("GCM NEW_TASK message received.");
                        ParticipActApplication.getInstance().dispatchMessage(MessageType.GCM_NOTIFICATION_NEW_TASK);
                        if (!ChangeTimePreferences.getInstance(context).getChangeTimeRequest()) {
                            Intent i = new Intent(context, NetworkService.class);
                            i.setAction(NetworkService.CHECK_TASK_FROM_GCM_ACTION);
                            context.startService(i);
                        } else {
                            NotificationUtility.addNotification(context, R.drawable.ic_time_error, context.getString(R.string.participact_notification), context.getString(R.string.task_blocked_wrong_time), NOTIFICATION_NEW_TASK_TIME_ERR);
                        }
                        break;
                    case NEW_VERSION:
                        logger.info("GCM NEW_VERSION message received.");
                        newVersionCase(context);
                        break;
                    case MESSAGE:
                        String text = intent.getExtras().getString(PANotification.TEXT);
                        NotificationUtility.addNotification(context, R.drawable.ic_language_white_36dp, context.getString(R.string.participact_notification), text, NOTIFICATION_MESSAGE);
                        break;
                    case NEWS:

                        break;
                    case LOG_UPLOAD_REQUEST:
                        logger.info("GCM LOG_UPLOAD_REQUEST message received.");
                        DataUploaderLogPreferences.getInstance(context).setLogUpload(true);
                        DataUploaderStatePreferences.getInstance(context).setStateUpload(true);
                        break;

                    case NEW_FRIEND:
                        newFriendCase(context);
                        break;

                    case NEW_BADGE:
                        newBadgeCase(context);
                        break;

                    case TASK_POSITIVE_VALUTATION:
                        newPositiveValutationCase(context);
                        break;
                    case TASK_NEGATIVE_VALUTATION:
                        newNegativeValutationCase(context);
                        break;

                    default:
                        break;
                }

            } catch (Exception e) {
                Log.e("GCM", null, e);
            }

        }
        setResultCode(Activity.RESULT_OK);
    }


    private void newVersionCase(Context context) {

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(
                Intent.ACTION_VIEW, Uri.parse("market://details?id=br.udesc.esag.participactbrasil.participact")), 0);


        NotificationUtility.addNotification(context, R.drawable.ic_new_task, context.getString(R.string.participact_notification), context.getString(R.string.new_app_update), NOTIFICATION_NEW_VERSION, contentIntent);

    }

    private void newFriendCase(Context context) {

        Intent resultIntent = new Intent(context, DashboardActivity.class);

        resultIntent.setAction(DashboardActivity.GO_TO_FRIENDS_FRAGMENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationUtility.addNotification(context, R.drawable.ic_person_add_white_36dp, context.getString(R.string.participact_notification), context.getString(R.string.new_friend_request), NOTIFICATION_NEW_FRIEND, resultPendingIntent);

    }

    private void newBadgeCase(Context context) {

        Intent resultIntent = new Intent(context, DashboardActivity.class);

        resultIntent.setAction(DashboardActivity.GO_TO_PROFILE_FRAGMENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationUtility.addNotification(context, R.drawable.ic_stars_white_36dp, context.getString(R.string.participact_notification), context.getString(R.string.new_badge_unlocked), NOTIFICATION_NEW_BADGE, resultPendingIntent);

    }

    private void newPositiveValutationCase(Context context) {
        Intent resultIntent = new Intent(context, DashboardActivity.class);
        resultIntent.setAction(DashboardActivity.GO_TO_APRROVED_TASK_CREATED_FRAGMENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationUtility.addNotification(context, R.drawable.ic_new_task, context.getString(R.string.participact_notification), context.getString(R.string.new_task_valutation_approved), NOTIFICATION_NEW_TASKUSER_APPROVED, resultPendingIntent);


    }

    private void newNegativeValutationCase(Context context) {

        Intent resultIntent = new Intent(context, DashboardActivity.class);
        resultIntent.setAction(DashboardActivity.GO_TO_REFUSED_TASK_CREATED_FRAGMENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationUtility.addNotification(context, R.drawable.ic_new_task, context.getString(R.string.participact_notification), context.getString(R.string.new_task_valutation_refused), NOTIFICATION_NEW_TASKUSER_REFUSED, resultPendingIntent);

    }

}