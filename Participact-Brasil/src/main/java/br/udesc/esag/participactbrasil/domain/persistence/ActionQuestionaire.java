package br.udesc.esag.participactbrasil.domain.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;


public class ActionQuestionaire extends Action {

    private static final long serialVersionUID = -2977490148767458942L;

    private List<Question> questions;

    private String title;

    private String description;

    public ActionQuestionaire() {
        questions = new LinkedList<Question>();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActionFlat convertToActionFlat() {
        return new ActionFlat(this);
    }

    @JsonIgnore
    public int getNextQuestionOrder() {
        if (questions == null) {
            throw new IllegalStateException("questions can't be null");
        }
        if (questions.size() == 0) {
            return 0;
        }
        int max = 0;
        for (Question q : questions) {
            if (q.getQuestionOrder() > max) {
                max = q.getQuestionOrder();
            }
        }
        max = max + 1;
        return max;
    }
}
