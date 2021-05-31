package br.udesc.esag.participactbrasil.domain.rest;

import org.parceler.Parcel;

/**
 * Created by alessandro on 26/11/14.
 */
@Parcel
public class ClosedAnswerRequest {


    String answerDescription;

    Integer answerOrder;


    public String getAnswerDescription() {
        return answerDescription;
    }

    public void setAnswerDescription(String answerDescription) {
        this.answerDescription = answerDescription;
    }

    public Integer getAnswerOrder() {
        return answerOrder;
    }

    public void setAnswerOrder(Integer answerOrder) {
        this.answerOrder = answerOrder;
    }
}
