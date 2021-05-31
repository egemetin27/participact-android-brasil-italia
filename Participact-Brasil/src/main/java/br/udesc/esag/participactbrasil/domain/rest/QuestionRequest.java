package br.udesc.esag.participactbrasil.domain.rest;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by alessandro on 26/11/14.
 */
@Parcel
public class QuestionRequest {

    String question;

    Integer question_order;

    List<ClosedAnswerRequest> closed_answers;

    Boolean isClosedAnswers;

    Boolean isMultipleAnswers;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestion_order() {
        return question_order;
    }

    public void setQuestion_order(Integer question_order) {
        this.question_order = question_order;
    }

    public Boolean getIsClosedAnswers() {
        return isClosedAnswers;
    }

    public void setIsClosedAnswers(Boolean isClosedAnswers) {
        this.isClosedAnswers = isClosedAnswers;
    }

    public Boolean getIsMultipleAnswers() {
        return isMultipleAnswers;
    }

    public void setIsMultipleAnswers(Boolean isMultipleAnswers) {
        this.isMultipleAnswers = isMultipleAnswers;
    }

    public List<ClosedAnswerRequest> getClosed_answers() {

        return closed_answers;
    }

    public void setClosed_answers(List<ClosedAnswerRequest> closed_answers) {
        this.closed_answers = closed_answers;
    }

    @JsonIgnore
    public int getNextClosedAnswerOrder() {
        if (closed_answers == null) {
            throw new IllegalStateException("closed answers can't be null");
        }
        if (closed_answers.size() == 0) {
            return 0;
        }
        int max = 0;
        for (ClosedAnswerRequest ca : closed_answers) {
            if (ca.getAnswerOrder() > max) {
                max = ca.getAnswerOrder();
            }
        }
        max = max + 1;
        return max;
    }


}
