package br.udesc.esag.participactbrasil.activities.questionnaire;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.persistence.ClosedAnswer;
import br.udesc.esag.participactbrasil.domain.persistence.DataQuestionnaireFlat;
import br.udesc.esag.participactbrasil.domain.persistence.Question;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;

public class QuestionnaireFragment extends Fragment {

    private static final String ARG_QUESTION = "question";

    public QuestionnaireFragment() {
        // Required empty public constructor
    }

    public static QuestionnaireFragment newInstance(Question question) {
        QuestionnaireFragment fragment = new QuestionnaireFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.question = (Question) getArguments().getSerializable(ARG_QUESTION);
        }
    }

    private View fragment;
    private Question question;
    private boolean isCompleted;
    private Button btSaveAnswers;
    private EditText txtViewOpenAnswer;
    private LinearLayout containerAnswers;
    private List<ClosedAnswer> closedAnswerList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_question, container, false);
        setRetainInstance(true);

        TextView txtViewDescription = (TextView) fragment.findViewById(R.id.txtViewQuestionDescription);
        txtViewDescription.setText(question.getQuestion());

        btSaveAnswers = (Button) fragment.findViewById(R.id.btSaveAnswer);
        btSaveAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswers();
            }
        });

        if(closedAnswerList == null){
            closedAnswerList = new ArrayList<>();
        }

        createAnswersLayout();

        return fragment;
    }

    private void createAnswersLayout() {
        if(question.getIsClosedAnswers() || question.getIsMultipleAnswers()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadPossibleAnswers();
                }
            },50);
        }else {
            txtViewOpenAnswer = (EditText) fragment.findViewById(R.id.txtViewQuestionAnswer);
            txtViewOpenAnswer.setVisibility(View.VISIBLE);
            txtViewOpenAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    btSaveAnswers.setVisibility(View.VISIBLE);
                }
            });
            displayOpenAnswer();
        }
    }

    private void loadPossibleAnswers() {
        try {
            containerAnswers = (LinearLayout) fragment.findViewById(R.id.containerAnswers);
            boolean alreadyAnswered = false;
            for (final ClosedAnswer answer : question.getClosed_answersDB()) {
                final CheckBox checkBoxAnswer = new CheckBox(getActivity());//(CheckBox)LayoutInflater.from(getActivity()).inflate(R.layout.checkbox,null);
                checkBoxAnswer.setText(answer.getAnswerDescription());
                if (checkDisplayClosedAnswer(answer)) {
                    alreadyAnswered = true;
                    checkBoxAnswer.setChecked(true);
                } else {
                    checkBoxAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (question.getIsMultipleAnswers()) {
                                handleMultipleAnswers(isChecked, answer);
                            } else if (question.getIsClosedAnswers()) {
                                handleClosedAnswers(isChecked, answer);
                                checkBoxAnswer.setChecked(isChecked);
                            }
                        }
                    });
                }
                containerAnswers.addView(checkBoxAnswer);
            }

            if(alreadyAnswered){
                btSaveAnswers.setVisibility(View.GONE);
                for(int i = 0;i<containerAnswers.getChildCount();i++){
                    CheckBox checkBox = (CheckBox) containerAnswers.getChildAt(i);
                    checkBox.setEnabled(false);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void displayOpenAnswer() {
        List<DataQuestionnaireFlat> listAnswers = StateUtility.getAnswerForQuestion(getActivity(),question);
        if(listAnswers.size() > 0){
            txtViewOpenAnswer.setEnabled(false);
            txtViewOpenAnswer.setText(listAnswers.get(0).getOpenAnswerValue());

            btSaveAnswers.setVisibility(View.GONE);
        }
    }

    private boolean checkDisplayClosedAnswer(ClosedAnswer answer){
        List<DataQuestionnaireFlat> listAnswers = StateUtility.getAnswerForQuestion(getActivity(),question);
        if(listAnswers.size() > 0){
            boolean foundAnswer = false;
            for(DataQuestionnaireFlat data:listAnswers) {
                if(answer.getId().equals(data.getAnswerId())){
                    foundAnswer = true;
                    break;
                }
            }
            return foundAnswer;
        }else{
            return false;
        }
    }

    private void handleMultipleAnswers(boolean isChecked, ClosedAnswer answer) {
        if(isChecked){
            closedAnswerList.add(answer);
        }else{
            closedAnswerList.remove(answer);
        }
        btSaveAnswers.setVisibility(View.VISIBLE);
    }

    private void handleClosedAnswers(boolean isChecked, ClosedAnswer answer) {
        if(isChecked){
            closedAnswerList.add(answer);
            for(int i = 0;i<containerAnswers.getChildCount();i++){
                CheckBox checkBoxAnswer = (CheckBox) containerAnswers.getChildAt(i);
                checkBoxAnswer.setChecked(false);
            }
        }else{
            closedAnswerList.remove(answer);
        }
        btSaveAnswers.setVisibility(View.VISIBLE);
    }

    private void saveAnswers() {
        if(question.getIsClosedAnswers() || question.getIsMultipleAnswers()){
            saveClosedAnswers();
        }else{
            String answer = txtViewOpenAnswer.getText().toString();
            saveOpenAnswer(answer);
        }
    }

    private void saveClosedAnswers(){
        ((QuestionnaireActivity) getActivity()).addClosedAnswerList(question, closedAnswerList);
    }

    private void saveOpenAnswer(String openAnswer){
        ((QuestionnaireActivity) getActivity()).addOpenAnswer(question, openAnswer);
    }

}
