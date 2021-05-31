package br.udesc.esag.participactbrasil.activities.campaign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;
import br.udesc.esag.participactbrasil.domain.rest.ProfileUpdateRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.ProfileUpdateRestResult;
import br.udesc.esag.participactbrasil.domain.rest.StatisticsRequest;
import br.udesc.esag.participactbrasil.domain.rest.StatisticsResult;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.network.request.ProfileUpdateRequest;
import br.udesc.esag.participactbrasil.network.request.StatisticsParticipationRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class CampaignResultsFragment extends Fragment {

    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    public CampaignResultsFragment() {
        // Required empty public constructor
    }

    public static CampaignResultsFragment newInstance(TaskFlat taskFlat) {
        CampaignResultsFragment fragment = new CampaignResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("task",taskFlat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (TaskFlat) getArguments().getSerializable("task");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!contentManager.isStarted())
            contentManager.start(getContext());
    }

    @Override
    public void onStop() {
        if (contentManager.isStarted())
            contentManager.shouldStop();
        super.onStop();
    }

    private TaskFlat task;
    private View fragment;
    private DateTimeFormatter formatter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment =  inflater.inflate(R.layout.fragment_results, container, false);
        formatter = DateTimeFormat.forPattern("dd/MM/YYYY HH:mm");
        loadData();
        return fragment;
    }

    private void loadData() {

        if (fragment == null)
            return;

        //------DATES
        TextView txtStartDate = (TextView) fragment.findViewById(R.id.txtViewStartDate);
        txtStartDate.setText(formatter.print(task.getTaskStatus().getAcceptedTime()));

        //------PHOTOS
        TextView txtPhotoProgress = (TextView) fragment.findViewById(R.id.txtViewPhotoProgress);
        ProgressBar progressBarPhotos = (ProgressBar) fragment.findViewById(R.id.progressBarPhotoProgress);
        if(task.containsPhotoActions()) {
            txtPhotoProgress.setText(String.valueOf((int)task.getTaskStatus().getProgressPhotoPercentual()) + "%");
            progressBarPhotos.setProgress((int) task.getTaskStatus().getProgressPhotoPercentual());
        }else{
            txtPhotoProgress.setVisibility(View.GONE);
            progressBarPhotos.setVisibility(View.GONE);
            fragment.findViewById(R.id.labelPhotos).setVisibility(View.GONE);
        }

        //------SENSORS
        TextView txtSensorsProgress = (TextView) fragment.findViewById(R.id.txtViewSensorsProgress);
        ProgressBar progressBarSensors = (ProgressBar) fragment.findViewById(R.id.progressBarSensorsProgress);
        if(task.getSensingActions().size() > 0) {
            txtSensorsProgress.setText(String.valueOf((int)task.getTaskStatus().getProgressSensingPercentual()) + "%");
            progressBarSensors.setProgress((int) task.getTaskStatus().getProgressSensingPercentual());
        }else{
            txtSensorsProgress.setVisibility(View.GONE);
            progressBarSensors.setVisibility(View.GONE);
            fragment.findViewById(R.id.labelSensors).setVisibility(View.GONE);
        }

        //------QUESTIONNAIRE
        TextView txtQuestionnaireProgress = (TextView) fragment.findViewById(R.id.txtViewQuestionnaireProgress);
        ProgressBar progressBarQuestionnaire = (ProgressBar) fragment.findViewById(R.id.progressBarQuestionnaireProgress);
        if(task.containsQuestionnaireActions()) {
            txtQuestionnaireProgress.setText(String.valueOf((int)task.getTaskStatus().getProgressQuestionnairePercentual()) + "%");
            progressBarQuestionnaire.setProgress((int) task.getTaskStatus().getProgressQuestionnairePercentual());
        }else{
            txtQuestionnaireProgress.setVisibility(View.GONE);
            progressBarQuestionnaire.setVisibility(View.GONE);
            fragment.findViewById(R.id.labelQuestionnaire).setVisibility(View.GONE);
        }

        final ProgressBar participation = (ProgressBar) fragment.findViewById(R.id.progressBarParticipation);
        final ProgressBar finished = (ProgressBar) fragment.findViewById(R.id.progressBarFinished);
        final TextView participationText = (TextView) fragment.findViewById(R.id.progressBarParticipationText);
        final TextView finishedText = (TextView) fragment.findViewById(R.id.progressBarFinishedText);

        int participationProgress = Utils.getPreferenceInt(getContext(), "participation"+task.getId(), 0);
        int finishedProgress = Utils.getPreferenceInt(getContext(), "finished"+task.getId(), 0);

        participation.setProgress(participationProgress);
        finished.setProgress(finishedProgress);
        participationText.setText(participationProgress + "%");
        finishedText.setText(finishedProgress + "%");

        StatisticsParticipationRequest request = new StatisticsParticipationRequest(getContext(), new StatisticsRequest(task.getId()));
        contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<StatisticsResult>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                ProgressDialog.dismiss(getActivity());
                AlertDialogUtils.showError(getActivity(), spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(StatisticsResult result) {

                if (result.isSuccess()) {

                    int participationProgress = result.getData().getParticipation();
                    int finishedProgress = result.getData().getCompletedActivities();

                    participation.setProgress(participationProgress);
                    finished.setProgress(finishedProgress);
                    participationText.setText(participationProgress + "%");
                    finishedText.setText(finishedProgress + "%");

                    Utils.savePreferenceInt(getContext(), "participation"+task.getId(), participationProgress);
                    Utils.savePreferenceInt(getContext(), "finished"+task.getId(), finishedProgress);

                } else {
                    AlertDialogUtils.showError(getActivity(), result.getMessage());
                }

            }
        });

    }

    public void notifyDataSetChanged() {
        if (task != null) {
            TaskStatus status = StateUtility.getTaskStatus(getContext(), task);
            task = status.getTask();
            loadData();
        }
    }
}
