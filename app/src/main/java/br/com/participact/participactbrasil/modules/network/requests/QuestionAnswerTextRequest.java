package br.com.participact.participactbrasil.modules.network.requests;

import java.util.List;

public class QuestionAnswerTextRequest extends QuestionAnswerRequest {

    List<QuestionAnswerTextData> data;

    public QuestionAnswerTextRequest(List<QuestionAnswerTextData> data) {
        this.data = data;
    }
}