package br.udesc.esag.participactbrasil.domain.rest;

import org.parceler.Parcel;

import java.util.List;

import br.udesc.esag.participactbrasil.domain.persistence.ActionType;

/**
 * Created by alessandro on 25/11/14.
 */
@Parcel
public class ActionFlatRequest {

    String name;

    Integer numeric_threshold;

    Integer duration_threshold;

    Integer input_type;

    List<QuestionRequest> questions;

    ActionType type;

    String title;

    String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumeric_threshold() {
        return numeric_threshold;
    }

    public void setNumeric_threshold(Integer numeric_threshold) {
        this.numeric_threshold = numeric_threshold;
    }

    public Integer getDuration_threshold() {
        return duration_threshold;
    }

    public void setDuration_threshold(Integer duration_threshold) {
        this.duration_threshold = duration_threshold;
    }

    public Integer getInput_type() {
        return input_type;
    }

    public void setInput_type(Integer input_type) {
        this.input_type = input_type;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
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

    public List<QuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRequest> questions) {
        this.questions = questions;
    }

    @org.codehaus.jackson.annotate.JsonIgnore
    public int getNextQuestionOrder() {
        if (questions == null) {
            throw new IllegalStateException("questions can't be null");
        }
        if (questions.size() == 0) {
            return 0;
        }
        int max = 0;
        for (QuestionRequest q : questions) {
            if (q.getQuestion_order() > max) {
                max = q.getQuestion_order();
            }
        }
        max = max + 1;
        return max;
    }
}
