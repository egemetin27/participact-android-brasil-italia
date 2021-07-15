package br.com.participact.participactbrasil.modules.db;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import org.most.pipeline.Pipeline;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.CampaignTaskPhotoActivity;
import br.com.participact.participactbrasil.modules.activities.CampaignTaskQuestionChooseActivity;
import br.com.participact.participactbrasil.modules.activities.CampaignTaskQuestionTextActivity;

public class ActionWrapper {

    Campaign campaign;
    Action action;

    protected ActionWrapper(Campaign campaign, Action action) {
        this.campaign = campaign;
        this.action = action;
    }

    public static ActionWrapper wrap(Campaign campaign, Action action) {
        return new ActionWrapper(campaign, action);
    }

    public Action getAction() {
        return action;
    }

    public boolean isSensing() {
        return "SENSING_MOST".equals(action.getType());
    }

    public boolean isQuestion() {
        return "QUESTIONNAIRE".equals(action.getType());
    }

    public boolean isPhoto() {
        return "PHOTO".equals(action.getType());
    }

    public Pipeline.Type getInputType() {
        if (action.getInputType() != null) {
            return Pipeline.Type.fromInt(action.getInputType());
        }
        return null;
    }

    public int tintColor() {
        int colorInactive = Color.parseColor("#C5CAB9");
        int colorActive = Color.parseColor("#4EC500");
        if (action.getType() != null) {
            if ("AVAILABLE".equalsIgnoreCase(campaign.getRawState()) ||
                    "COMPLETED_WITH_SUCCESS".equalsIgnoreCase(campaign.getRawState()) ||
                    "COMPLETED_WITH_UNSUCCESS".equalsIgnoreCase(campaign.getRawState())) {
                return colorInactive;
            }
            int progress = getProgress();
            switch (action.getType()) {
                case "SENSING_MOST":
                    return pipelineTintColor(colorActive, colorInactive);
                case "PHOTO":
                case "QUESTIONNAIRE":
                    return progress >= 100 ? colorInactive : colorActive;
            }
        }
        return colorInactive;
    }

    public int pipelineTintColor(int colorActive, int colorInactive) {
        Pipeline.Type type = getInputType();
        if (type != null) {
            return App.getInstance().getPipelineManager().isActive(type) ? colorActive : colorInactive;
        }
        return colorInactive;
    }

    public Bitmap icon(Context context) {
        if (action.getType() != null) {
            switch (action.getType()) {
                case "SENSING_MOST":
                    return pipelineIcon(context);
                case "PHOTO":
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_foto);
                case "QUESTIONNAIRE":
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_questionnaire);
                default:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_gps);
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_vibration_36pt);
    }

    public Bitmap pipelineIcon(Context context) {
        Pipeline.Type type = getInputType();
        if (type != null) {
            switch (type) {
                case PHONE_CALL_EVENT:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_chamada);
                case PHONE_CALL_DURATION:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_chamada_duracao);
                case CELL:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_celular);
                case BLUETOOTH:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_bluetooth);
                case ACCELEROMETER:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_acelerometro);
                case BATTERY:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_bateria);
                case GYROSCOPE:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_giroscopio);
                case LOCATION:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_gps);
                case MAGNETIC_FIELD:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_campo_magnetico);
                case ACCELEROMETER_CLASSIFIER:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_eixos);
                case DEVICE_NET_TRAFFIC:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_uso_rede_apps);
                case CONNECTION_TYPE:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_tipo_conexao);
                case ACTIVITY_RECOGNITION_COMPARE:
                case GOOGLE_ACTIVITY_RECOGNITION:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_atividade_google);
                case INSTALLED_APPS:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_apps_instalados);
                case APP_ON_SCREEN:
                    return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_apps_na_tela);
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_vibration_36pt);
    }

    public String getSensingName() {
        Pipeline.Type type = getInputType();
        if (type != null) {
            switch (type) {
                case ACCELEROMETER:
                    return "Acelerômetro";
                case BATTERY:
                    return "Bateria";
                case GYROSCOPE:
                    return "Giroscópio";
                case LOCATION:
                    return "GPS";
                case MAGNETIC_FIELD:
                    return "Campo Magnético";
                case ACCELEROMETER_CLASSIFIER:
                    return "Acelerômetro";
                case DEVICE_NET_TRAFFIC:
                    return "Tráfego de Dados";
                case CONNECTION_TYPE:
                    return "Tipo Conexão";
                case ACTIVITY_RECOGNITION_COMPARE:
                case GOOGLE_ACTIVITY_RECOGNITION:
                    return "Atividade";
                case INSTALLED_APPS:
                    return "Aplicativos Instalados";
                case APP_ON_SCREEN:
                    return "Aplicativos na Tela";
            }
        }
        return "N/A";
    }

    public int getProgress() {
        if (action.getType() != null) {
            switch (action.getType()) {
                case "SENSING_MOST":
                    return ActionSensing.wrap(campaign, action).getProgress();
                case "PHOTO":
                    return ActionPhoto.wrap(campaign, action).getProgress();
                case "QUESTIONNAIRE":
                    if (getNextQuestion() == null) {
                        return 100;
                    } else {
                        return ActionQuestionnaire.wrap(campaign, action).getProgress();
                    }
            }
        }
        return 0;
    }

    public long getTargetId() {
        return getTargetId(new QuestionDaoImpl().getLastAnswered(action.getId()));
    }

    public long getTargetId(Question lastAnswered) {
        long targetId = 0;
        if (lastAnswered != null) {
            if (lastAnswered.getClosed_answers() != null && lastAnswered.getClosed_answers().size() > 0) {
                if (lastAnswered.getAnswerIds() != null && lastAnswered.getAnswerIds().length() > 0) {
                    String[] answerIds = new String[]{lastAnswered.getAnswerIds()};
                    if (lastAnswered.getAnswerIds().contains(",")) {
                        answerIds = lastAnswered.getAnswerIds().split(",");
                    }
                    if (answerIds.length == 1) {
                        long answerId = Long.parseLong(answerIds[0]);
                        for (QuestionOption op : lastAnswered.getClosed_answers()) {
                            if (op.getId().longValue() == answerId) {
                                if (op.getTargetId() != null && op.getTargetId() > 0) {
                                    targetId = op.getTargetId();
                                }
                            }
                        }
                    }
                }
            } else {
                if (lastAnswered.getTargetId() != null && lastAnswered.getTargetId() > 0) {
                    targetId = lastAnswered.getTargetId();
                }
            }
        }
        return targetId;
    }

    public Question getPreviousQuestion() {
        Question lastAnswered = new QuestionDaoImpl().getLastAnswered(action.getId());
        if (lastAnswered == null) {
            if (action.getQuestions() != null && action.getQuestions().size() > 0) {
                lastAnswered = action.getQuestions().get(0);
            }
        }
        if (lastAnswered != null) {
            lastAnswered.setAnswer(null);
            lastAnswered.setAnswerIds(null);
            lastAnswered.setAnswerDate(null);
            new CampaignDaoImpl().commit(campaign);
            return lastAnswered;
        }
        return null;
    }

    public Question getNextQuestion() {
        return getNextQuestion(null);
    }

    public Question getNextQuestion(Question lastAnswered) {
        if (lastAnswered == null) {
            lastAnswered = new QuestionDaoImpl().getLastAnswered(action.getId());
        }
        long targetId = getTargetId(lastAnswered);
        Question next = null;
        boolean lastAnsweredIdFound = false;
        long lastAnsweredId = lastAnswered != null ? lastAnswered.getId() : 0;
        for (Question q : action.getQuestions()) {
            if (targetId > 0) {
                if (q.getId().longValue() == targetId) {
                    next = q;
                    break;
                }
            } else if (lastAnsweredId > 0) {
                if (lastAnsweredIdFound) {
                    next = q;
                    break;
                } else {
                    if (q.getId().longValue() == lastAnsweredId) {
                        lastAnsweredIdFound = true;
                    }
                }
            } else {
                if (!QuestionWrapper.wrap(q).isAnswered()) {
                    if (q.getSkipped() == null || !q.getSkipped()) {
                        next = q;
                        break;
                    }
                }
            }
        }
        return next;
    }

    public void presentNext(Context context) {
        Question question = action.getQuestions().get(0);
        Question lastAnswered = new QuestionDaoImpl().getLastAnswered(action.getId());
        Question next = getNextQuestion(lastAnswered);
        if (next != null) {
            question = next;
        }
        QuestionWrapper wrapper = QuestionWrapper.wrap(question);

        if (wrapper.isPhoto()) {
            Intent i = new Intent(context, CampaignTaskPhotoActivity.class);
            i.putExtra("campaignId", campaign.getId());
            i.putExtra("actionId", action.getId());
            context.startActivity(i);
        } else if (wrapper.isText()) {
            Intent i = new Intent(context, CampaignTaskQuestionTextActivity.class);
            i.putExtra("campaignId", action.getCampaignId());
            i.putExtra("actionId", action.getId());
            context.startActivity(i);
        } else {
            Intent i = new Intent(context, CampaignTaskQuestionChooseActivity.class);
            i.putExtra("campaignId", action.getCampaignId());
            i.putExtra("actionId", action.getId());
            context.startActivity(i);
        }
    }

    public boolean presentPrevious(Context context, Question current) {
        Question question = getPreviousQuestion();
        if (question != null) {
            if (current != null && current.getId().longValue() == question.getId().longValue()) {
                Toast.makeText(context, "Essa é a primeira questão.", Toast.LENGTH_LONG).show();
            } else {
                presentNext(context);
                return true;
            }
        } else {
            Toast.makeText(context, "Essa é a primeira questão.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public boolean hasNextQuestion() {
        return hasNextQuestion(new QuestionDaoImpl().getLastAnswered(action.getId()));
    }

    public boolean hasNextQuestion(Question lastAnswered) {
        Question next = getNextQuestion(lastAnswered);
        return next != null;
    }

    public boolean isRepeat() {
        return action.getRepeat() != null && action.getRepeat();
    }
}
