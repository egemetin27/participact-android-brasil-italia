package br.udesc.esag.participactbrasil.activities.questionnaire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.data.Data;
import br.udesc.esag.participactbrasil.domain.data.DataQuestionaireClosedAnswer;
import br.udesc.esag.participactbrasil.domain.data.DataQuestionaireOpenAnswer;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ClosedAnswer;
import br.udesc.esag.participactbrasil.domain.persistence.DataQuestionnaireFlat;
import br.udesc.esag.participactbrasil.domain.persistence.Question;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.support.DomainDBHelper;
import br.udesc.esag.participactbrasil.support.DataUploader;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderPhotoPreferences;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderQuestionnairePreferences;

public class QuestionnaireActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ActionFlat actionFlat;
    private Map<Long, List<Data>> answersData;
    private Map<Question, String> openAnswerList;
    private Map<Question, List<ClosedAnswer>> closedAnswerList;
    private QuestionnairePageAdapter questionnairePageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_questionnaire);

        this.answersData = new LinkedHashMap<>();
        this.openAnswerList = new LinkedHashMap<>();
        this.closedAnswerList = new LinkedHashMap<>();
        this.actionFlat = (ActionFlat) getIntent().getSerializableExtra("action");

        adjustToolbar();
        loadViewPager();
    }

    private void adjustToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(actionFlat.getTitle().toUpperCase());
    }

    private void loadViewPager() {
        questionnairePageAdapter = new QuestionnairePageAdapter(getSupportFragmentManager(), actionFlat);

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(questionnairePageAdapter);
        viewPager.setOffscreenPageLimit(actionFlat.getQuestions().size() + 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideKeyboard();
            }

            @Override
            public void onPageSelected(int position) {
                hideKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void addClosedAnswerList(Question question, List<ClosedAnswer> closedAnswerList) {
        this.closedAnswerList.put(question, closedAnswerList);
        proceedToNextQuestion();
    }

    public void addOpenAnswer(Question question, String openAnswer) {
        this.openAnswerList.put(question, openAnswer);
        proceedToNextQuestion();
    }

    public void proceedToNextQuestion() {
        int currentPage = viewPager.getCurrentItem();
        if ((currentPage + 1) < viewPager.getChildCount()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    public void saveQuestionnaire() {

        for (Map.Entry<Question, List<ClosedAnswer>> answer : this.closedAnswerList.entrySet()) {
            List<Data> dataList = new ArrayList<>();
            for (ClosedAnswer closedAnswer : answer.getValue()) {
                DataQuestionaireClosedAnswer closedAnswerData = new DataQuestionaireClosedAnswer();
                closedAnswerData.setClosedAnswer(closedAnswer);
                closedAnswerData.setSampleTimestamp(System.currentTimeMillis());
                closedAnswerData.setAnswer_value(true);
                dataList.add(closedAnswerData);
            }
            answersData.put(answer.getKey().getId(), dataList);
        }

        for (Map.Entry<Question, String> answer : this.openAnswerList.entrySet()) {
            DataQuestionaireOpenAnswer closedAnswerData = new DataQuestionaireOpenAnswer();
            closedAnswerData.setAnswer_value(answer.getValue());
            closedAnswerData.setSampleTimestamp(System.currentTimeMillis());

            List<Data> openAnswerDataList = new ArrayList<>();
            openAnswerDataList.add(closedAnswerData);

            answersData.put(answer.getKey().getId(), openAnswerDataList);
        }


        try {
            DomainDBHelper dbHelper = OpenHelperManager.getHelper(this, DomainDBHelper.class);
            RuntimeExceptionDao<DataQuestionnaireFlat, Long> dao = dbHelper.getRuntimeExceptionDao(DataQuestionnaireFlat.class);
            for (Map.Entry<Long, List<Data>> answer : answersData.entrySet()) {

                for (Data data : answer.getValue()) {
                    DataQuestionnaireFlat quest = new DataQuestionnaireFlat();
                    quest.setTaskId(actionFlat.getTask().getId());
                    quest.setActionId(actionFlat.getId());
                    quest.setQuestionId(answer.getKey());
                    quest.setAnswerId(-1L);
                    quest.setClosedAnswerValue(false);
                    quest.setOpenAnswerValue("");

                    if (data instanceof DataQuestionaireOpenAnswer) {
                        DataQuestionaireOpenAnswer dataOpen = (DataQuestionaireOpenAnswer) data;
                        quest.setType(DataQuestionnaireFlat.TYPE_OPEN_ANSWER);
                        quest.setOpenAnswerValue(dataOpen.isAnswer_value());
                        quest.setTimestamp(dataOpen.getSampleTimestamp());
                    }

                    if (data instanceof DataQuestionaireClosedAnswer) {
                        DataQuestionaireClosedAnswer dataClosed = (DataQuestionaireClosedAnswer) data;
                        quest.setType(DataQuestionnaireFlat.TYPE_CLOSED_ANSWER);
                        quest.setTimestamp(dataClosed.getSampleTimestamp());
                        quest.setAnswerId(dataClosed.getClosedAnswer().getId());
                        quest.setClosedAnswerValue(dataClosed.isAnswer_value());
                    }
                    dao.create(quest);

                }

            }

            DataUploaderQuestionnairePreferences.getInstance(this).setQuestionnaireUpload(true);
            DataUploader.getInstance(this).uploadQuestionnaire();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            List<DataQuestionnaireFlat> listAnswers = StateUtility.getAnswerForAction(this,actionFlat);
            if(listAnswers.size() >= actionFlat.getQuestions().size()) {
                StateUtility.incrementQuestionnaireProgress(this, actionFlat.getTask().getId(), actionFlat.getId());
            }
            OpenHelperManager.releaseHelper();
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        boolean isCompleted = actionFlat.getTask().getTaskStatus().isQuestionnaireCompleted(actionFlat.getId());
        if (actionFlat.getTask().getTaskStatus().getState() == TaskState.RUNNING && !isCompleted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Atenção");
            builder.setMessage("Deseja salvar as respostas atuais antes de sair?");
            builder.setPositiveButton("Salvar e sair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveQuestionnaire();
                }
            });
            builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
        }
        else {
            finish();
        }
    }

    private void hideKeyboard(){
        try {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );

            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
