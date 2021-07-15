package com.bergmannsoft.push;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;

public class GcmIntentService extends JobIntentService {
    private static final String TAG = GcmIntentService.class.getName();
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
//    private QuizApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
//        app = (QuizApplication) getApplication();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//        String messageType = gcm.getMessageType(intent);
//
//        if (!extras.isEmpty()) {
//            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                Log.i(TAG, "Received: " + extras.toString());
//                int type = 0;
//                try {
//                    type = Integer.parseInt(extras.getString("type"));
//                } catch (Exception e) {
//                }
//                if (type != 0) {
//                    sendNotification(extras);
//                    app.dispatchMessage(MessageType.PUSH_NOTIFICATION, extras);
//
//                    try {
//
//                        Vibrator v = (Vibrator) app.getSystemService(Context.VIBRATOR_SERVICE);
//                        v.vibrate(1000);
//
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                        r.play();
//
//                    } catch (Exception e) {
//                        Log.e(TAG, null, e);
//                    }
//                }
//            }
//        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//
//    }

    private void sendNotification(Bundle extras) {

        String message = extras.getString("message");
        if (message == null) {
            message = "Novo quiz disponível.";
        }

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

//        Intent i = new Intent(this, SignInActivity.class);
//        i.putExtras(extras);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                i, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                this)
//                .setSmallIcon(getNotificationIcon())
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(message);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            mBuilder.setStyle(new NotificationCompat.InboxStyle());
//            mBuilder.setWhen(0);
//        } else {
//            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
//        }
//
//        mBuilder.setContentIntent(contentIntent);
//        Notification notification = mBuilder.build();
//        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
//        mNotificationManager.notify(Constants.NOTIFICATION_ALERT_ID, notification);

    }

    private int getNotificationIcon() {
//        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//        return useWhiteIcon ? R.mipmap.ic_check_circle_white_36dp : R.mipmap.ic_launcher;
        return 0;
    }

}