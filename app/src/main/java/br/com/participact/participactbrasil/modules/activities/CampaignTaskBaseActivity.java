package br.com.participact.participactbrasil.modules.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bergmannsoft.dialog.AlertDialogUtils;

import java.util.Date;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Action;
import br.com.participact.participactbrasil.modules.db.ActionDaoImpl;
import br.com.participact.participactbrasil.modules.db.ActionWrapper;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.Photo;
import br.com.participact.participactbrasil.modules.db.PhotoDaoImpl;
import br.com.participact.participactbrasil.modules.db.Question;
import br.com.participact.participactbrasil.modules.db.QuestionAnswer;
import br.com.participact.participactbrasil.modules.db.QuestionAnswerDaoImpl;
import br.com.participact.participactbrasil.modules.db.QuestionDaoImpl;
import br.com.participact.participactbrasil.modules.db.QuestionOption;
import br.com.participact.participactbrasil.modules.db.QuestionWrapper;
import br.com.participact.participactbrasil.modules.dialog.QuestionnaireRepeatDialog;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.IPDataResponse;

public class CampaignTaskBaseActivity extends BaseActivity {

    protected Campaign campaign;
    protected Action action;
    protected Question question;
    protected CampaignWrapper campaignWrapper;
    protected ActionWrapper actionWrapper;
    protected QuestionWrapper questionWrapper;

    protected QuestionnaireRepeatDialog questionnaireRepeatDialog;

    protected String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long campaignId = getIntent().getLongExtra("campaignId", 0);
        Long actionId = getIntent().getLongExtra("actionId", 0);

        createBase();

        if (questionWrapper != null && questionWrapper.isReadyToUpload() && actionWrapper.isRepeat()) {
            if (questionnaireRepeatDialog != null) {
                questionnaireRepeatDialog.dismiss();
            }
            questionnaireRepeatDialog = new QuestionnaireRepeatDialog(this, campaignId, actionId, new QuestionnaireRepeatDialog.QuestionnaireDialogListener() {
                @Override
                public void onQuestionnaireAvailable() {
                    createBase();
                    createUI();
                }

                @Override
                public void onOKClick() {
                    finish();
                }
            });
            questionnaireRepeatDialog.show();
        }

        SessionManager.getInstance().getIPData(new SessionManager.RequestCallback<IPDataResponse>() {
            @Override
            public void onResponse(IPDataResponse response) {
                System.out.println();
                if (response != null) {
                    ipAddress = response.getIp();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void createBase() {
        Long campaignId = getIntent().getLongExtra("campaignId", 0);
        Long actionId = getIntent().getLongExtra("actionId", 0);
        if (campaignId > 0) {
            campaign = new CampaignDaoImpl().find(campaignId);
        }
        if (campaign == null) {
            AlertDialogUtils.showAlert(this, "Campanha não encontrada.");
            return;
        }

        if (campaign.getActions() != null) {
            for (Action act : campaign.getActions()) {
                if (act.getId().longValue() == actionId.longValue()) {
                    action = act;
                    break;
                }
            }
        } else {
            AlertDialogUtils.showAlert(this, "Campanha sem ações.");
            return;
        }

        if (action == null) {
            AlertDialogUtils.showAlert(this, "Ação não encontrada.");
            return;
        }

        campaignWrapper = CampaignWrapper.wrap(campaign);
        actionWrapper = ActionWrapper.wrap(campaign, action);

        if (!(this instanceof CampaignTaskPhotoActivity)) {
            if (action.getQuestions() == null || action.getQuestions().size() == 0) {
                AlertDialogUtils.showAlert(this, "Campanha sem questões.");
                return;
            }
            question = nextQuestion();
            if (question == null) {
                AlertDialogUtils.showAlert(this, "Questão não encontrada.");
                return;
            }
        } else {
            if (action.getQuestions() != null && action.getQuestions().size() > 0) {
                question = nextQuestion();
            }
        }

        if (question != null) {
            questionWrapper = QuestionWrapper.wrap(question);
        }
    }

    protected void createUI() {

    }

    protected void setupDescriptionHeader() {
        TextView campaignDescription = findViewById(R.id.textCampaignDescription);
        campaignDescription.setText(campaign.getName());
        TextView taskDescription = findViewById(R.id.textDescription);
        TextView textTitle = findViewById(R.id.textTitle);
        if (this instanceof CampaignTaskPhotoActivity) { // Photo
            taskDescription.setText(action.getName());
            if (question != null) {
                taskDescription.setText(question.getQuestion());
            }
        } else {
            textTitle.setText(action.getName());
            taskDescription.setText(question.getQuestion());
        }
    }

    protected boolean isValid() {
        if (!(this instanceof CampaignTaskPhotoActivity)) {
            return campaign != null && action != null && question != null;
        } else {
            return campaign != null && action != null;
        }
    }

    protected Question nextQuestion() {
        Question question = action.getQuestions().get(0);
        Question lastAnswered = new QuestionDaoImpl().getLastAnswered(action.getId());
        if (actionWrapper != null) {
            Question next = actionWrapper.getNextQuestion(lastAnswered);
            if (next != null) {
                question = next;
            }
        }
        return question;
    }

    /**
     * Used for photo question.
     */
    protected void prepareToSend() {
        question.setAnswerDate(new Date());
        if (actionWrapper.isRepeat()) {
            question.setReadyToUpload(false);
            prepareToSendAnswer();
        }
        new CampaignDaoImpl().commit(campaign);
        if (actionWrapper.hasNextQuestion(question)) {
            actionWrapper.presentNext(this);
        } else {
            new QuestionAnswerDaoImpl().makeReadyToUpload(action.getId());
            new ActionDaoImpl().prepareToRepeat(action.getId());
        }
        finish();
    }

    protected void prepareToSend(String text) {
        if (question.getSkipped() == null || !question.getSkipped()) {
            question.setAnswer(text);
            question.setReadyToUpload(true);
            question.setIpAddress(ipAddress);
        }
        if (actionWrapper.isRepeat()) {
            question.setReadyToUpload(false);
            prepareToSendAnswer();
        }
        question.setAnswerDate(new Date());
        new CampaignDaoImpl().commit(campaign);
        if (actionWrapper.hasNextQuestion(question)) {
            actionWrapper.presentNext(this);
        } else {
            new QuestionAnswerDaoImpl().makeReadyToUpload(action.getId());
            new ActionDaoImpl().prepareToRepeat(action.getId());
        }
        finish();
    }

    protected void prepareToSend(List<Long> ids) {
        String items = "";
        for (Long id : ids) {
            if (items.length() > 0) {
                items += ",";
            }
            items += String.valueOf(id);
        }
        if (question.getSkipped() == null || !question.getSkipped()) {
            question.setAnswerIds(items);
            question.setReadyToUpload(true);
            question.setIpAddress(ipAddress);
        }
        if (actionWrapper.isRepeat()) {
            question.setReadyToUpload(false);
            prepareToSendAnswer();
        }
        question.setAnswerDate(new Date());
        new CampaignDaoImpl().commit(campaign);
        if (actionWrapper.hasNextQuestion(question)) {
            actionWrapper.presentNext(this);
        } else {
            new QuestionAnswerDaoImpl().makeReadyToUpload(action.getId());
            new ActionDaoImpl().prepareToRepeat(action.getId());
        }
        finish();
    }

    protected void goToPreviousQuestion() {
        question.setAnswerDate(null);
        new CampaignDaoImpl().commit(campaign);
        if (actionWrapper.presentPrevious(this, question)) {
            finish();
        }
    }

    private void prepareToSendAnswer() {
        QuestionAnswerDaoImpl answerDao = new QuestionAnswerDaoImpl();
        if (questionWrapper.isPhoto()) {
            PhotoDaoImpl photoDao = new PhotoDaoImpl();
            List<Photo> entities = photoDao.findByQuestionId(question.getId());
            for (Photo entity : entities) {
                QuestionAnswer answer = getQuestionAnswer();
                answer.setFilename(entity.getFilename());
                answer.setLatitude(entity.getLatitude());
                answer.setLongitude(entity.getLongitude());
                answer.setProvider(entity.getProvider());
                answer.setPhoto(true);
                photoDao.delete(entity);
                answerDao.commit(answer);
            }
        } else {
            QuestionAnswer answer = getQuestionAnswer();
            answerDao.commit(answer);
        }
    }

    private QuestionAnswer getQuestionAnswer() {
        QuestionAnswer answer = new QuestionAnswer();
        //answer.setId(0L);
        answer.setAnswer(question.getAnswer());
        answer.setAnswerIds(question.getAnswerIds());
        answer.setCampaignId(question.getCampaignId());
        answer.setActionId(question.getActionId());
        answer.setQuestionId(question.getId());
        answer.setReadyToUpload(false);
        answer.setIpAddress(ipAddress);
        answer.setPhoto(false);
        return answer;
    }

    public void backClick(View view) {
        goToPreviousQuestion();
    }

    public void closeClick(View view) {
        finish();
    }

}
