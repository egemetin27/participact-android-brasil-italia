package br.com.participact.participactbrasil.modules.db;

import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerChooseData;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerChooseRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerTextData;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerTextRequest;

public class QuestionAnswerWrapper {

    private QuestionAnswer question;

    private QuestionAnswerWrapper(QuestionAnswer question) {
        this.question = question;
    }

    public static QuestionAnswerWrapper wrap(QuestionAnswer question) {
        return new QuestionAnswerWrapper(question);
    }

    public boolean isText() {
        return question.getAnswer() != null && question.getAnswer().length() > 0;
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
            data.add(new QuestionAnswerTextData(question.getActionId(), question.getCampaignId(), question.getQuestionId(), question.getAnswer(), question.getIpAddress(), question.getAnswerGroupId()));
            return new QuestionAnswerTextRequest(data);
        } else {
            List<QuestionAnswerChooseData> data = new ArrayList<>();
            if (question.getAnswerIds() != null) {
                String[] ids = question.getAnswerIds().split(",");
                for (String id : ids) {
                    data.add(new QuestionAnswerChooseData(question.getActionId(), question.getCampaignId(), question.getQuestionId(), id, question.getIpAddress(), question.getAnswerGroupId()));
                }
            } else {
                data.add(new QuestionAnswerChooseData(question.getActionId(), question.getCampaignId(), question.getQuestionId(), "0", question.getIpAddress(), question.getAnswerGroupId()));
            }
            return new QuestionAnswerChooseRequest(data);
        }
    }

}
