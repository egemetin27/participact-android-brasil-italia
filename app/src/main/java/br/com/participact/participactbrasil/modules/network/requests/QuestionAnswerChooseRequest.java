package br.com.participact.participactbrasil.modules.network.requests;

import java.util.List;

public class QuestionAnswerChooseRequest extends QuestionAnswerRequest {

    List<QuestionAnswerChooseData> data;

    public QuestionAnswerChooseRequest(List<QuestionAnswerChooseData> data) {
        this.data = data;
    }
}