package br.udesc.esag.participactbrasil.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.octo.android.robospice.SpiceManager;
import com.splunk.mint.Mint;

import org.most.MoSTApplication;
import org.most.MoSTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.network.NotificationAcceptMandatoryTaskListener;
import br.udesc.esag.participactbrasil.network.request.AcceptTaskRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.CheckClientAppVersionAlarm;
import br.udesc.esag.participactbrasil.support.GeolocalizationTaskUtils;
import br.udesc.esag.participactbrasil.support.ProgressAlarm;
import br.udesc.esag.participactbrasil.support.StateLogAlarm;
import br.udesc.esag.participactbrasil.support.UploadAlarm;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
    public static final String START = "ParticipActService.START";
    public static final String STOP = "ParticipActService.STOP";

    public static final String GEO_TASK_UPDATE_INTENT = "br.udesc.esag.participactbrasil.participact.GEO_TASK_UPDATE";

    private static Location last;

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 90;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

   // LocationClient locationClient;
    LocationRequest mLocationRequest;

    private AtomicBoolean isStarted = new AtomicBoolean(false);
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
//        Mint.initAndStartSession(LocationService.this, "c874cdb0"); /*"6e05d719"*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void init() {
        Editor editor = getSharedPreferences(MoSTApplication.PREF_MOST_SERVICE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(MoSTService.PREF_KEY_STORE_STATE, false);
        editor.apply();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        if (intent != null) {
            if (START.equals(intent.getAction()) && !isStarted.get()) {
                //il telefono � stato spento correttamente tutti i task sono in SUSPENDED
                //se invece il telefono si � spento all'improvviso alcuni RUNNING e altri SUSPENDED
                StateUtility.suspendAllTask(this, TaskState.RUNNING);
                StateUtility.activateAllTask(this, TaskState.SUSPENDED);
                ProgressAlarm.getInstance(this).start();
                UploadAlarm.getInstance(this).start();
                CheckClientAppVersionAlarm.getInstance(this).start();
                StateLogAlarm.getInstance(this).start();

                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                googleApiClient.connect();

                isStarted.set(true);
                logger.info("Starting ParticipActService with START intent");
            } else if (STOP.equals(intent.getAction()) && isStarted.get()) {
                StateUtility.suspendAllTask(this, TaskState.RUNNING);
                ProgressAlarm.getInstance(this).stop();
                UploadAlarm.getInstance(this).stop();
                CheckClientAppVersionAlarm.getInstance(this).stop();
                StateLogAlarm.getInstance(this).stop();

                googleApiClient.disconnect();
                isStarted.set(false);
                stopSelf();
                logger.info("Stopping ParticipActService with STOP intent");
            }
        } else {
            //restore state
            ProgressAlarm.getInstance(this).start();
            UploadAlarm.getInstance(this).start();
            CheckClientAppVersionAlarm.getInstance(this).start();
            StateLogAlarm.getInstance(this).start();

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();

            logger.info("Starting ParticipActService with STICKY intent");
        }
        return START_STICKY;
    }


    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        logger.info("Location Client connection failed.");
    }

    @Override
    public void onConnected(Bundle arg0) {
        logger.info("Location Client connected.");

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            Log.e("LocationService", null, e);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        logger.info("Location Client suspended.");
    }

    @Override
    public void onLocationChanged(Location location) {

        List<TaskFlat> hiddens = StateUtility.getTaskByState(this, TaskState.HIDDEN);

        for (TaskFlat taskFlat : hiddens) {
            if (GeolocalizationTaskUtils.isNotifiedByArea(taskFlat) && GeolocalizationTaskUtils.isInside(this, location.getLongitude(), location.getLatitude(), taskFlat.getNotificationArea())) {
                if (!taskFlat.getCanBeRefused()) {
                    AcceptTaskRequest request = new AcceptTaskRequest(this, taskFlat.getId());
                    if (!contentManager.isStarted()) {
                        contentManager.start(this);
                    }
                    contentManager.execute(request, new NotificationAcceptMandatoryTaskListener(this, taskFlat));
                } else {
                    StateUtility.changeTaskState(this, taskFlat, TaskState.GEO_NOTIFIED_AVAILABLE);
                }
            }
        }

        List<TaskFlat> running = StateUtility.getTaskByState(this, TaskState.RUNNING);

        for (TaskFlat taskFlat : running) {
            if (GeolocalizationTaskUtils.isActivatedByArea(taskFlat)
                    && !GeolocalizationTaskUtils.isInside(this, location.getLongitude(), location.getLatitude(),
                    taskFlat.getActivationArea())) {
                StateUtility.changeTaskState(this, taskFlat, TaskState.RUNNING_BUT_NOT_EXEC);
            }
        }

        List<TaskFlat> runningNotExec = StateUtility.getTaskByState(this, TaskState.RUNNING_BUT_NOT_EXEC);

        for (TaskFlat taskFlat : runningNotExec) {
            if (GeolocalizationTaskUtils.isActivatedByArea(taskFlat)
                    && GeolocalizationTaskUtils.isInside(this, location.getLongitude(), location.getLatitude(),
                    taskFlat.getActivationArea())) {
                StateUtility.changeTaskState(this, taskFlat, TaskState.RUNNING);
            }
        }

        last = location;

        Intent i = new Intent();
        i.setAction(GEO_TASK_UPDATE_INTENT);
        sendBroadcast(i);
    }


    public static Location getLastLocation() {
        return last;
    }


}
