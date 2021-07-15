package br.com.participact.participactbrasil.modules.db;

import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerChooseData;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerChooseRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerTextData;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerTextRequest;

public class QuestionWrapper {

    private Question question;

    private QuestionWrapper(Question question) {
        this.question = question;
    }

    public static QuestionWrapper wrap(Question question) {
        return new QuestionWrapper(question);
    }

    public boolean isReadyToUpload() {
        return question.getReadyToUpload() != null && question.getReadyToUpload();
    }

    public boolean isAnswered() {
        return (question.getAnswer() != null && question.getAnswer().trim().length() > 0) || (question.getAnswerIds() != null && question.getAnswerIds().length() > 0)
                || isSkipped();
    }

    public boolean isRequired() {
        return question.getRequired() != null && question.getRequired();
    }

    public boolean isSkipped() {
        return question.getSkipped() != null && question.getSkipped();
    }

    public boolean isText() {
        return question.getClosedAnswers()!= null && question.getClosedAnswers() == false && question.getMultipleSelect() != null && question.getMultipleSelect() == false;
    }

    public boolean isPhoto() {
        return question.getPhoto() != null && question.getPhoto();
    }

    public boolean isCheckboxes() {
        return question.getClosedAnswers()!= null && question.getClosedAnswers() == true && question.getMultipleSelect() != null && question.getMultipleSelect() == false;
    }

    public boolean isMultiChoice() {
        return question.getMultipleSelect() != null && question.getMultipleSelect() == true;
    }

    public List<Long> getAnswerIds() {
        List<Long> answers = new ArrayList<>();
        if (question.getAnswerIds() != null) {
            String[] items = question.getAnswerIds().split(",");
            for (String item : items) {
                try {
                    Long id = Long.parseLong(item);
                    answers.add(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return answers;
    }

    public QuestionAnswerRequest getRequest() {
        if (isText()) {
            List<QuestionAnswerTextData> data = new ArrayList<>();
            data.add(new QuestionAnswerTextData(question.getActionId(), question.getCampaignId(), question.getId(), question.getAnswer(), question.getIpAddress(), question.getAnswerGroupId()));
            return new QuestionAnswerTextRequest(data);
        } else {
            List<QuestionAnswerChooseData> data = new ArrayList<>();
            String[] ids = question.getAnswerIds().split(",");
            for (String id : ids) {
                data.add(new QuestionAnswerChooseData(question.getActionId(), question.getCampaignId(), question.getId(), id, question.getIpAddress(), question.getAnswerGroupId()));
            }
            return new QuestionAnswerChooseRequest(data);
        }
    }


    public boolean isDate() {
        return question.getIsDate() != null && question.getIsDate();
    }
}
