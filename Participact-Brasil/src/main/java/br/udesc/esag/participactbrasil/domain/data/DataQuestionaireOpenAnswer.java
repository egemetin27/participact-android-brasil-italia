package br.udesc.esag.participactbrasil.domain.data;

import br.udesc.esag.participactbrasil.domain.persistence.Question;


public class DataQuestionaireOpenAnswer extends Data {

    private static final long serialVersionUID = 1780396465805694468L;

    private Question question;

    private String answer_value;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String isAnswer_value() {
        return answer_value;
    }

    public void setAnswer_value(String answer_value) {
        this.answer_value = answer_value;
    }

}
