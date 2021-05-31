package br.udesc.esag.participactbrasil.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.broadcastreceivers.GcmBroadcastReceiver;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlatList;
import br.udesc.esag.participactbrasil.network.request.AvailableTaskRequest;
import br.udesc.esag.participactbrasil.network.request.CheckClientAppVersionRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.NotificationUtility;
import br.udesc.esag.participactbrasil.support.SntpClient;
import br.udesc.esag.participactbrasil.support.TaskUtility;
import br.udesc.esag.participactbrasil.support.preferences.ChangeTimePreferences;

public class NetworkService extends IntentService {

    private final static Logger logger = LoggerFactory.getLogger(NetworkService.class);
    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    public final static String CHECK_TIME_ACTION = "br.udesc.esag.participactbrasil.participact.CHECK_TIME";
    public final static String CHECK_TASK_FROM_GCM_ACTION = "br.udesc.esag.participactbrasil.participact.CHECK_TASK_FROM_GCM";
    public final static String CHECK_CLIENT_APP_VERSION = "br.udesc.esag.participactbrasil.participact.CHECK_CLIENT_APP_VERSION";
    public final static long CHANGE_TIME_THRESHOLD = 1000 * 60 * 15;

    NotificationManager mNotificationManager;

    public NetworkService() {
        super(NetworkService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getAction().equals(CHECK_TIME_ACTION)) {
            SntpClient client = new SntpClient();
            if (client.requestTime("time.windows.com", 2000)) {
                long ntp = client.getNtpTime() + SystemClock.elapsedRealtime()
                        - client.getNtpTimeReference();
                long diff = Math.abs(System.currentTimeMillis() - ntp);

                logger.info(
                        "Checked time with ntp server. Ntp time = {}, System time = {}, diff = {}.",
                        ntp, System.currentTimeMillis(), diff);

                if (diff < CHANGE_TIME_THRESHOLD) {
                    ChangeTimePreferences.getInstance(this).setLastCurrentMillisChecked(
                            System.currentTimeMillis());
                    ChangeTimePreferences.getInstance(this).setLastElapsedChecked(
                            SystemClock.elapsedRealtime());

                    StateUtility.defreezeAllTask(this);
                    ChangeTimePreferences.getInstance(this).setChangeTimeRequest(false);
                    mNotificationManager = (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(GcmBroadcastReceiver.NOTIFICATION_TIME_ERR);

                    NotificationUtility.addNotification(NetworkService.this, R.drawable.ic_stat_ok, getString(R.string.participact_notification), getString(R.string.time_restore_success), GcmBroadcastReceiver.NOTIFICATION_TIME_OK);

                }
            }
        }

        if (intent.getAction().equals(CHECK_TASK_FROM_GCM_ACTION)) {

            logger.info("Check task by gcm.");

            if (!contentManager.isStarted()) {
                contentManager.start(this);
            }
            AvailableTaskRequest request = new AvailableTaskRequest(this, TaskState.AVAILABLE, AvailableTaskRequest.ALL);
            contentManager.execute(request, new NotificationTaskRequestListener(this));
        }

        if (intent.getAction().equals(CHECK_CLIENT_APP_VERSION)) {

            logger.info("Check app version.");

            if (!contentManager.isStarted()) {
                contentManager.start(this);
            }
            // TODO: verify
//            CheckClientAppVersionRequest request = new CheckClientAppVersionRequest(this);
//            contentManager.execute(request, new CheckClientAppVersionRequestListener());
        }

    }

    private void sendNewClientAppVersionNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: verify
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=br.udesc.esag.participactbrasil.participact"));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

        NotificationUtility.addNotification(NetworkService.this, R.drawable.ic_login_err, getString(R.string.new_app_update), getString(R.string.tap_to_update), GcmBroadcastReceiver.NOTIFICATION_NEW_VERSION, contentIntent);
    }

    private class NotificationTaskRequestListener implements RequestListener<TaskFlatList> {

        Context context;

        public NotificationTaskRequestListener(Context context) {
            this.context = context;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (spiceException.getCause() instanceof HttpMessageNotReadableException) {
                sendNewClientAppVersionNotification();
            }
        }

        @Override
        public void onRequestSuccess(TaskFlatList result) {
            if (result != null) {
                TaskUtility.getInstance(context).handleNewTasksFromService(result,contentManager);
            }
        }
    }

    private class CheckClientAppVersionRequestListener implements RequestListener<Integer> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
        }

        @Override
        public void onRequestSuccess(Integer result) {
            if (result != null) {
                if (result > ParticipActConfiguration.VERSION) {
                    sendNewClientAppVersionNotification();
                }
            }
        }
    }

}
