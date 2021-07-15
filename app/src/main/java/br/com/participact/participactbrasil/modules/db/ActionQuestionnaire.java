package br.com.participact.participactbrasil.modules.db;

import java.util.ArrayList;
import java.util.List;

public class ActionQuestionnaire extends ActionWrapper {

    private ActionQuestionnaire(Campaign campaign, Action action) {
        super(campaign, action);
    }

    public static ActionQuestionnaire wrap(Campaign campaign, Action action) {
        return new ActionQuestionnaire(campaign, action);
    }

    @Override
    public int getProgress() {
        if (action.getQuestions() == null)
            return 0;
        int replied = 0;
        for (Question question : action.getQuestions()) {
            if (question.getAnswer() != null || (question.getAnswerIds() != null && question.getAnswerIds().length() > 0)) {
                replied++;
            }
        }
        return Math.min(replied * 100 / action.getQuestions().size(), 100);
    }

    public List<Question> getQuestionsToUpload() {
        List<Question> items = new ArrayList<>();
        if (action.getQuestions() != null) {
            for (Question question : action.getQuestions()) {
                if (
                        ((question.getAnswerIds() != null && question.getAnswerIds().length() > 0) || (question.getAnswer() != null && question.getAnswer().length() > 0)) &&
                                (question.getUploaded() == null || !question.getUploaded()) &&
                                (question.getReadyToUpload() != null && question.getReadyToUpload())) {
                    items.add(question);
                }
            }
        }
        return items;
    }

}
