package br.udesc.esag.participactbrasil.activities.campaign;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.services.LocationService;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.network.request.AcceptTaskRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.network.request.RejectTaskRequest;
import br.udesc.esag.participactbrasil.services.PendingActiveTaskIntentService;
import br.udesc.esag.participactbrasil.support.AlarmStateUtility;
import br.udesc.esag.participactbrasil.support.DialogFactory;
import br.udesc.esag.participactbrasil.support.GeolocalizationTaskUtils;
import br.udesc.esag.participactbrasil.support.LoginUtility;
import br.udesc.esag.participactbrasil.utils.LocationUtils;

public class CampaignDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CampaignDetailsFragment.class.getSimpleName();

    private TaskFlat task;
    private static final String ARG_PARAM_TASK = "task";

    private String lastRequestCacheKey;
    private final static Logger logger = LoggerFactory.getLogger(CampaignDetailsFragment.class);
    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
    private Polygon polygon;

    public CampaignDetailsFragment() {
        // Required empty public constructor
    }

    public static CampaignDetailsFragment newInstance(TaskFlat taskFlat) {
        CampaignDetailsFragment fragment = new CampaignDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("task", taskFlat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (TaskFlat) getArguments().getSerializable(ARG_PARAM_TASK);
        }
    }

    private View fragment;
    private MapView mapView;
    private Button btAccept;
    private Button btReject;
    private Button btPause;
    private Button btResume;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_details, container, false);
        mapView = (MapView) fragment.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        loadDetails();
        loadButtons();
        loadMap();

        return fragment;
    }

    private void loadDetails() {
        TextView txtViewDescription = (TextView) fragment.findViewById(R.id.txtViewDescription);
        txtViewDescription.setText(task.getDescription());

        TextView txtViewCreationDate = (TextView) fragment.findViewById(R.id.txtViewCreationDate);
        txtViewCreationDate.setText(DateTimeFormat.forPattern("dd/MM/YYYY HH:mm").print(task.getStart()));

        TextView txtViewExpirationDate = (TextView) fragment.findViewById(R.id.txtViewExpirationDate);
        txtViewExpirationDate.setText(DateTimeFormat.forPattern("dd/MM/YYYY HH:mm").print(task.getDeadline()));

        TextView txtViewStatus = (TextView) fragment.findViewById(R.id.txtViewCampaignStatus);
        txtViewStatus.setText(task.getTaskStatus().getState().toReadableString());
    }

    private void loadButtons() {
        btAccept = (Button) fragment.findViewById(R.id.btAccept);
        btReject = (Button) fragment.findViewById(R.id.btReject);
        btPause = (Button) fragment.findViewById(R.id.btPauseTask);
        btResume = (Button) fragment.findViewById(R.id.btResumeTask);
        if(task.getTaskStatus().getState().equals(TaskState.AVAILABLE)){
            btAccept.setOnClickListener(this);
            btReject.setOnClickListener(this);
        }else {
            btAccept.setVisibility(View.GONE);
            btReject.setVisibility(View.GONE);

            btResume.setOnClickListener(this);
            btPause.setOnClickListener(this);

            if(task.getTaskStatus().getState().equals(TaskState.RUNNING)
                    || task.getTaskStatus().getState().equals(TaskState.RUNNING_BUT_NOT_EXEC)){
                btResume.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
            }else if(task.getTaskStatus().getState().equals(TaskState.SUSPENDED)){
                btResume.setVisibility(View.VISIBLE);
                btPause.setVisibility(View.GONE);
            }
        }
    }

    private void loadMap() {
        if(task.getActivationArea() != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    updateMap(googleMap);
                }
            });
        }else{
            mapView.setVisibility(View.GONE);
            fragment.findViewById(R.id.txtViewActivationArea).setVisibility(View.GONE);
        }
    }

    private void updateMap(GoogleMap map){
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        Log.d(TAG, task.getActivationArea());
        List<LatLng> points = LocationUtils.polygonStringToLatLngList(task.getActivationArea());
        PolygonOptions options = new PolygonOptions();
        options.addAll(points);
        options.strokeColor(Color.RED)
                .fillColor(Color.BLUE);

        if (polygon != null) {
            polygon.remove();
        }
        polygon = map.addPolygon(options);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.getCenter(points), 14));
    }

    private void acceptTask(){
        AcceptTaskRequest request = new AcceptTaskRequest(getActivity(), task.getId());
        lastRequestCacheKey = request.createCacheKey();
        contentManager.execute(request, lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new AcceptTaskRequestListener(getActivity()));
        btAccept.setEnabled(false);
        btReject.setEnabled(false);
        ParticipActApplication.getInstance().dispatchMessage(MessageType.CAMPAIGN_ACCEPTED);
    }

    private void rejectTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.reject_confirmation);
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                RejectTaskRequest request = new RejectTaskRequest(getActivity(), task.getId());
                lastRequestCacheKey = request.createCacheKey();
                contentManager.execute(request, lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new RejectTaskRequestListener(getActivity()));
                btAccept.setEnabled(false);
                btReject.setEnabled(false);
            }
        });

        builder.create().show();
    }

    private void pauseTask(){
        StateUtility.changeTaskState(getActivity(), task, TaskState.SUSPENDED);
        AlarmStateUtility.addAlarm(getActivity(), task.getId());
        loadButtons();
        loadDetails();
    }

    private void resumeTask(){
        Location last = LocationService.getLastLocation();
        if (!GeolocalizationTaskUtils.isActivatedByArea(task)
                || (last != null && GeolocalizationTaskUtils.isActivatedByArea(task)
                && GeolocalizationTaskUtils.isInside(getActivity(), last.getLongitude(), last.getLatitude(), task.getActivationArea()))) {
            StateUtility.changeTaskState(getActivity(), task, TaskState.RUNNING);
        } else {
            StateUtility.changeTaskState(getActivity(), task, TaskState.RUNNING_BUT_NOT_EXEC);
        }
        AlarmStateUtility.removeAlarm(getActivity(), task.getId());
        loadButtons();
        loadDetails();
    }

    @Override
    public void onClick(View v) {
        final Context context = v.getContext();
        NetworkStateChecker networkChecker = new DefaultNetworkStateChecker();
        if (!networkChecker.isNetworkAvailable(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.attention).setMessage(R.string.error_no_network)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;
        } else {

            if (!contentManager.isStarted()) {
                contentManager.start(context);
            }

            if (v.getId() == R.id.btAccept) {
                acceptTask();
            } else if (v.getId() == R.id.btReject) {
                rejectTask();
            }else if (v.getId() == R.id.btPauseTask) {
                pauseTask();
            }else if (v.getId() == R.id.btResumeTask) {
                resumeTask();
            }
        }
    }

    private class AcceptTaskRequestListener implements RequestListener<ResponseMessage> {

        Context context;

        public AcceptTaskRequestListener(Context context) {
            this.context = context;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            logger.warn("Accept task request of task with id {} failed.", task.getId(), spiceException);
            if (LoginUtility.checkIfLoginException(context, spiceException)) {

            } else {
                DialogFactory.showCommunicationErrorWithServer(context);
                btAccept.setEnabled(true);
                btReject.setEnabled(true);
            }
        }

        @Override
        public void onRequestSuccess(ResponseMessage result) {
            if (result != null && result.getResultCode() == ResponseMessage.RESULT_OK) {
                logger.info("Accept task request of task with id {} completed with success.", task.getId());

                Location last = LocationService.getLastLocation();
                if (task != null) {
                    if (!GeolocalizationTaskUtils.isActivatedByArea(task) || (last != null && GeolocalizationTaskUtils.isActivatedByArea(task) && GeolocalizationTaskUtils.isInside(context, last.getLongitude(), last.getLatitude(), task.getActivationArea()))) {
                        //task in running
                        StateUtility.changeTaskState(context, task, TaskState.RUNNING);
                    } else {
                        StateUtility.changeTaskState(context, task, TaskState.RUNNING_BUT_NOT_EXEC);
                    }
                }
                PendingActiveTaskIntentService.startActionScheduleReceiver(context, task.getId());
                loadButtons();
                loadDetails();
            }
        }

    }

    private class RejectTaskRequestListener implements RequestListener<ResponseMessage> {

        Context context;

        public RejectTaskRequestListener(Context context) {
            this.context = context;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            logger.warn("Reject task request of task with id {} failed.", task.getId(), spiceException);
            if (LoginUtility.checkIfLoginException(context, spiceException)) {

            } else {
                DialogFactory.showCommunicationErrorWithServer(context);
                btAccept.setEnabled(true);
                btReject.setEnabled(true);
            }
        }

        @Override
        public void onRequestSuccess(ResponseMessage result) {
            if (result != null && result.getResultCode() == ResponseMessage.RESULT_OK) {
                logger.info("Reject task request of task with id {} completed with success.", task.getId());
                StateUtility.changeTaskState(context, task, TaskState.REJECTED);
                loadButtons();
                loadDetails();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void notifyDataSetChanged() {
        loadDetails();
        loadButtons();
        loadMap();
    }
}
