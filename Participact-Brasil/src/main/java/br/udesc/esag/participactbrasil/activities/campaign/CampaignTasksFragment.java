package br.udesc.esag.participactbrasil.activities.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.PicturesActivity;
import br.udesc.esag.participactbrasil.activities.questionnaire.QuestionnaireActivity;
import br.udesc.esag.participactbrasil.adapters.TaskListAdapter;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ActionType;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;


public class CampaignTasksFragment extends Fragment {

    public enum TasksType {
        PENDING,
        HISTORY
    }

    private TasksType type;

    public CampaignTasksFragment() {
        // Required empty public constructor
    }

    public static CampaignTasksFragment newInstance(TaskFlat taskFlat) {
        CampaignTasksFragment fragment = new CampaignTasksFragment();
        Bundle args = new Bundle();
        args.putSerializable("task", taskFlat);
        fragment.setArguments(args);
        return fragment;
    }

    public static CampaignTasksFragment newInstance(ArrayList<ActionFlat> actions, TasksType type) {
        CampaignTasksFragment fragment = new CampaignTasksFragment();
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
            this.task = (TaskFlat) getArguments().getSerializable("task");
            if (task != null) {
                this.actions = task.getDirectActions();
            } else {
                this.type = getArguments().getSerializable("type").equals(TasksType.PENDING.toString()) ? TasksType.PENDING : TasksType.HISTORY;
                this.actions = (ArrayList<ActionFlat>) getArguments().getSerializable("actions");
            }
        }
    }

    private TaskFlat task;
    private View fragment;
    private AbsListView listViewTasks;
    private TaskListAdapter listAdapter;
    private ArrayList<ActionFlat> actions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_tasks, container, false);
        listViewTasks = (AbsListView) fragment.findViewById(R.id.listViewTasksFragment);
        loadTaskList();
        return fragment;
    }

    private void loadTaskList() {
        if (actions.size() > 0) {
            listAdapter = new TaskListAdapter(getActivity(), actions);
            listViewTasks.setAdapter(listAdapter);
            listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ActionFlat action = (ActionFlat) view.getTag();
                    handleSelectedAction(action);
                }
            });
        } else {
            if (listAdapter != null) {
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            }
            fragment.findViewById(R.id.textViewNoTaskAvailable).setVisibility(View.VISIBLE);
        }
    }

    private void handleSelectedAction(ActionFlat action) {
        if (action.getType().equals(ActionType.PHOTO)) {
            startPhotoActivity(action);
        } else {
            startQuestionnaireActivity(action);
        }
    }

    private void startPhotoActivity(ActionFlat action) {
        Intent intent = new Intent(getActivity(), PicturesActivity.class);
        intent.putExtra("action", action);
        getActivity().startActivity(intent);
        startedActionActivity = true;
    }

    private void startQuestionnaireActivity(ActionFlat action) {
        Intent intent = new Intent(getActivity(), QuestionnaireActivity.class);
        intent.putExtra("action", action);
        getActivity().startActivity(intent);
        startedActionActivity = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (type != null) {
            switch (type) {
                case PENDING:
                    this.actions = new ArrayList<>(StateUtility.getAllOpenDirectActions(getActivity()));
                    break;
                case HISTORY:
                    this.actions = new ArrayList<>(StateUtility.getDoneDirectActions(getActivity()));
                    break;
            }
            loadTaskList();
        }
        updateTaskList();
    }

    //used to control when the update is needed since onResume is also executed on the first run of the fragment
    private boolean startedActionActivity = false;

    private void updateTaskList() {
        if (startedActionActivity && this.task != null) {
            this.task = StateUtility.getTaskStatus(getActivity(), task).getTask();
            this.actions = task.getDirectActions();
            startedActionActivity = false;
            loadTaskList();
        }
    }

    public void notifyDataSetChanged() {
        loadTaskList();
    }

}
