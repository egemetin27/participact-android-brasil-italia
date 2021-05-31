package br.udesc.esag.participactbrasil.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.j256.ormlite.stmt.query.In;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.Connectivity;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignActivity;
import br.udesc.esag.participactbrasil.activities.login.LoginActivity;
import br.udesc.esag.participactbrasil.adapters.CampaignsListAdapter;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlatList;
import br.udesc.esag.participactbrasil.network.request.AvailableTaskRequest;
import br.udesc.esag.participactbrasil.network.request.LoginRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.DialogFactory;
import br.udesc.esag.participactbrasil.support.LoginRetry;
import br.udesc.esag.participactbrasil.support.LoginUtility;
import br.udesc.esag.participactbrasil.support.TaskUtility;

import static br.udesc.esag.participactbrasil.ParticipActConfiguration.PAGES_URL;

public class CampaignActiveFragment extends Fragment {

    private static final String TAG = "CampaignActiveFragment";

    ImageButton errorInfoButton;
    private boolean networkReachable;

    public CampaignActiveFragment() {
        // Required empty public constructor
    }

    public static CampaignActiveFragment newInstance() {
        CampaignActiveFragment fragment = new CampaignActiveFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTaskList();
    }

    @Override
    public void onStart(){
        super.onStart();
        if (!_contentManager.isStarted()) {
            _contentManager.start(getActivity());
        }
        performRequestForAvailableTasks();
    }

    @Override
    public void onStop(){
        if (_contentManager.isStarted()) {
            _contentManager.shouldStop();
        }
        super.onStop();
    }

    private View fragment;
    private AbsListView listView;
    private List<TaskFlat> taskList;
    private CampaignsListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpiceManager _contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_campaign, container, false);

        errorInfoButton = (ImageButton) fragment.findViewById(R.id.error_info_button);
        errorInfoButton.setVisibility(View.INVISIBLE);
        errorInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtils.showMessage(getActivity(), getString(R.string.network_error_message) + (!networkReachable ? " [506]" : " [505]"), getString(R.string.network_error));
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) fragment.findViewById(R.id.swipeRefreshLayoutCampaigns);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performRequestForAvailableTasks();
            }
        });

        listView = (AbsListView) fragment.findViewById(R.id.listViewCampaignFragment);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startCampaignActivity(taskList.get(i));
            }
        });

        loadCampaigns();

        return fragment;
    }

    private void loadCampaigns() {
        listAdapter = new CampaignsListAdapter(getActivity(), taskList);
        listView.setAdapter(listAdapter);

        if(taskList.size() > 0){
            fragment.findViewById(R.id.textViewNoCampaignAvailable).setVisibility(View.GONE);
        }else{
            fragment.findViewById(R.id.textViewNoCampaignAvailable).setVisibility(View.VISIBLE);
        }
    }

    private void startCampaignActivity(TaskFlat taskFlat){
        Intent intent = new Intent(getActivity(),CampaignActivity.class);
        intent.putExtra("task",taskFlat);
        startActivity(intent);
    }

    private void loadTaskList(){
        this.taskList = StateUtility.getTaskListByState(getActivity(), TaskState.ACCEPTED);
        this.taskList.addAll(StateUtility.getTaskListByState(getActivity(), TaskState.AVAILABLE));
        this.taskList.addAll(StateUtility.getTaskListByState(getActivity(), TaskState.RUNNING));
        this.taskList.addAll(StateUtility.getTaskListByState(getActivity(), TaskState.UNKNOWN));
        this.taskList.addAll(StateUtility.getTaskListByState(getActivity(), TaskState.RUNNING_BUT_NOT_EXEC));
    }

    public void performRequestForAvailableTasks() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean connected = Connectivity.isConnected(getActivity());
                networkReachable = Connectivity.isReachable(getActivity(), PAGES_URL);
                if (connected && networkReachable) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AvailableTaskRequest request = new AvailableTaskRequest(getActivity(), TaskState.AVAILABLE, AvailableTaskRequest.ALL);
                                String _lastRequestCacheKey = request.createCacheKey();
                                _contentManager.execute(request, _lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new TaskRequestListener());

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                } else {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorInfoButton.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, null, e);
                    }
                }
            }
        }).start();

    }

    private class TaskRequestListener implements RequestListener<TaskFlatList> {

        public TaskRequestListener() {}

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            swipeRefreshLayout.setRefreshing(false);
            if (LoginUtility.checkIfLoginException(getActivity(), spiceException, false)) {
                Log.d(TAG, "Login Error");
                new LoginRetry(getActivity(), _contentManager, new LoginRetry.LoginRetryListener() {
                    @Override
                    public void onSuccess() {
                        performRequestForAvailableTasks();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "Impossible to login again.");
                    }
                });

            } else {
                DialogFactory.showCommunicationErrorWithServer(getActivity());
            }
        }

        @Override
        public void onRequestSuccess(TaskFlatList result) {
            swipeRefreshLayout.setRefreshing(false);
            if (result != null) {
                TaskUtility.getInstance(getActivity()).handleNewTasks(result,_contentManager);
                loadTaskList();
                loadCampaigns();
            }
        }

    }
}
