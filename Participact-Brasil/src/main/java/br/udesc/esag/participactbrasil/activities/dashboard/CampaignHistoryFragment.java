package br.udesc.esag.participactbrasil.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignActivity;
import br.udesc.esag.participactbrasil.adapters.CampaignsListAdapter;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;

public class CampaignHistoryFragment extends Fragment {

    public CampaignHistoryFragment() {
        // Required empty public constructor
    }


    public static CampaignHistoryFragment newInstance() {
        CampaignHistoryFragment fragment = new CampaignHistoryFragment();
        Bundle args = new Bundle();
;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTaskList();
    }

    private View fragment;
    private AbsListView listView;
    private List<TaskFlat> taskList;
    private CampaignsListAdapter listAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_campaign, container, false);

        listView = (AbsListView) fragment.findViewById(R.id.listViewCampaignFragment);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startCampaignActivity(taskList.get(i));
            }
        });

        loadCampaigns();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) fragment.findViewById(R.id.swipeRefreshLayoutCampaigns);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return fragment;
    }

    private void loadCampaigns() {
        listAdapter = null;
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
        this.taskList = StateUtility.getTaskListByState(getActivity(), TaskState.REJECTED);
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.COMPLETED_WITH_UNSUCCESS), taskList));
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.COMPLETED_WITH_SUCCESS), taskList));
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.COMPLETED_NOT_SYNC_WITH_SERVER), taskList));
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.ERROR), taskList));
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.FAILED), taskList));
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.SUSPENDED), taskList));
        this.taskList.addAll(getUniqueItems(StateUtility.getTaskListByState(getActivity(), TaskState.INTERRUPTED), taskList));

        Collections.sort(taskList, new Comparator<TaskFlat>() {
            @Override
            public int compare(TaskFlat a, TaskFlat b) {
                return a.getDeadline().compareTo(b.getDeadline());
            }
        });
    }

    public void reloadTaskList() {
        loadTaskList();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadCampaigns();
            }
        });
    }

    private List<TaskFlat> getUniqueItems(List<TaskFlat> newItems, List<TaskFlat> currentItems) {
        List<TaskFlat> unique = new ArrayList<>();
        for (TaskFlat task : newItems) {
            boolean exists = false;
            for (TaskFlat cur : currentItems) {
                if (cur.getId().equals(task.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                unique.add(task);
            }
        }
        return unique;
    }

}
