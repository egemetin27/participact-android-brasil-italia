package br.udesc.esag.participactbrasil.activities.questionnaire;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.DataQuestionnaireFlat;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;

public class QuestionnaireInfoFragment extends Fragment {

    private static final String ARG_ACTION = "action";

    public QuestionnaireInfoFragment() {
        // Required empty public constructor
    }

    public static QuestionnaireInfoFragment newInstance(ActionFlat actionFlat) {
        QuestionnaireInfoFragment fragment = new QuestionnaireInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACTION, actionFlat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actionFlat = (ActionFlat) getArguments().getSerializable(ARG_ACTION);
        }
    }

    private ActionFlat actionFlat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_questionnaire_info, container, false);

        TextView txtDescription = (TextView) fragment.findViewById(R.id.txtViewDescription);
        txtDescription.setText(actionFlat.getDescription());

        TextView txtStatus = (TextView) fragment.findViewById(R.id.txtViewQuestionnaireStatus);

        boolean isCompleted = actionFlat.getTask().getTaskStatus().isQuestionnaireCompleted(actionFlat.getId());
        if(isCompleted){
            txtStatus.setText(R.string.completed);
        }else{
            txtStatus.setText(R.string.pending_task_questionnaire);
        }

        Button btSaveQuestionnaire = (Button) fragment.findViewById(R.id.btSaveQuestionnaire);

        if(isCompleted){
            btSaveQuestionnaire.setText("OK");
            btSaveQuestionnaire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }else{
            btSaveQuestionnaire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((QuestionnaireActivity) getActivity()).saveQuestionnaire();
                }
            });
        }

        return fragment;
    }


}
