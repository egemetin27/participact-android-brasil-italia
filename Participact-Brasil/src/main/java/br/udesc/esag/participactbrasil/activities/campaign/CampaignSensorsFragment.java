package br.udesc.esag.participactbrasil.activities.campaign;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.adapters.SensorsListAdapter;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;

public class CampaignSensorsFragment extends Fragment {

    private AbsListView listView;

    public enum SensorsType {
        PENDING,
        HISTORY
    }

    private SensorsType type;

    private SensorsListAdapter listAdapter;

    public CampaignSensorsFragment() {
        // Required empty public constructor
    }


    public static CampaignSensorsFragment newInstance(TaskFlat task) {
        CampaignSensorsFragment fragment = new CampaignSensorsFragment();
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        fragment.setArguments(args);
        return fragment;
    }

    public static CampaignSensorsFragment newInstance(ArrayList<ActionFlat> actions, SensorsType type) {
        CampaignSensorsFragment fragment = new CampaignSensorsFragment();
        Bundle args = new Bundle();
        args.putSerializable("actions", actions);
        args.putSerializable("type", type.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (TaskFlat) getArguments().getSerializable("task");
            if (task != null) {
                actions = task.getSensingActions();
            } else {
                this.type = getArguments().getSerializable("type").equals(SensorsType.PENDING.toString()) ? SensorsType.PENDING : SensorsType.HISTORY;
                this.actions = (ArrayList<ActionFlat>) getArguments().getSerializable("actions");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (task != null) {
            actions = task.getSensingActions();
        } else {
            if (type != null) {
                switch (type) {
                    case PENDING:
                        this.actions = new ArrayList<>(StateUtility.getAllOpenSensorsActions(getActivity()));
                        break;
                    case HISTORY:
                        this.actions = new ArrayList<>(StateUtility.getDoneSensorsActions(getActivity()));
                        break;
                }
            }
        }
        loadSensorsList();
    }

    private TaskFlat task;
    private ArrayList<ActionFlat> actions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_sensors, container, false);
        listView = (AbsListView) fragment.findViewById(R.id.listViewSensorsFragment);

        loadSensorsList();

        return fragment;

    }

    private void loadSensorsList() {
        listAdapter = new SensorsListAdapter(getActivity(), actions);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SensorDialogFragment.newInstance(actions.get(i)).show(getActivity().getSupportFragmentManager(),null);
            }
        });
    }


    public static class SensorDialogFragment extends DialogFragment {

        public SensorDialogFragment() {
            // Required empty public constructor
        }

        public static SensorDialogFragment newInstance(ActionFlat actionFlat) {
            SensorDialogFragment fragment = new SensorDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable("action",actionFlat);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                this.actionFlat = (ActionFlat) getArguments().getSerializable("action");
            }
        }

        private View fragment;
        private View iconStatusOn;
        private View iconStatusOff;
        private TextView txtStatus;
        private TextView txtDescription;
        private Button btActivateSensor;
        private ActionFlat actionFlat;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            fragment = inflater.inflate(R.layout.fragment_sensor_dialog, container, false);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            iconStatusOn = (View) fragment.findViewById(R.id.iconStatusOn);
            iconStatusOff = (View) fragment.findViewById(R.id.iconStatusOff);
            txtStatus = (TextView) fragment.findViewById(R.id.txtViewSensorStatus);
            txtDescription = (TextView) fragment.findViewById(R.id.txtViewSensorDescription);
            if(actionFlat.getDescription() != null) {
                txtDescription.setText(actionFlat.getDescription());
            }else if(actionFlat.getName() != null){
                txtDescription.setText(actionFlat.getName());
            }

            btActivateSensor = (Button) fragment.findViewById(R.id.btActivateSensor);
            btActivateSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //changeSensorStatus();
                    dismiss();
                }
            });

            checkSensorStatus();
            return fragment;
        }

        public void checkSensorStatus() {
            if(actionFlat.getTask().getTaskStatus().getState() == TaskState.RUNNING
                    || actionFlat.getTask().getTaskStatus().getState() == TaskState.RUNNING_BUT_NOT_EXEC ){
                iconStatusOn.setVisibility(View.VISIBLE);
                iconStatusOff.setVisibility(View.INVISIBLE);
                txtStatus.setText("Sensor ativado");
            }else{
                iconStatusOff.setVisibility(View.VISIBLE);
                iconStatusOn.setVisibility(View.INVISIBLE);
                txtStatus.setText("Sensor desativado");
            }
        }
    }

    public void notifyDataSetChanged() {

        if (task != null) {
            TaskStatus status = StateUtility.getTaskStatus(getContext(), task);
            task = status.getTask();
            actions = task.getSensingActions();
            listAdapter.setActions(actions);
        }

    }

}
