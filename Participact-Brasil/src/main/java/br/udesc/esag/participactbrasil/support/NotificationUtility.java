package br.udesc.esag.participactbrasil.support;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import br.udesc.esag.participactbrasil.R;

public class NotificationUtility {

    public static void addNotification(Context context, int smallIconId, String title, String content, int notificationId) {
        addNotification(context, smallIconId, title, content, notificationId, null);
    }

    public static void addNotification(Context context, int smallIconId, String title, String content, int notificationId, PendingIntent pendingIntent) {

        if (context == null)
            throw new NullPointerException();
        if (title == null)
            throw new NullPointerException();
        if (content == null)
            throw new NullPointerException();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIconId)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.primary_dark));

        if (pendingIntent != null) {
            mBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }
}
