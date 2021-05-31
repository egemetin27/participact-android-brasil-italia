package br.udesc.esag.participactbrasil.domain.data;

import br.udesc.esag.participactbrasil.domain.persistence.ClosedAnswer;
import br.udesc.esag.participactbrasil.domain.persistence.Question;


public class DataQuestionaireClosedAnswer extends Data {

    private static final long serialVersionUID = -1900866387953714053L;

    private ClosedAnswer closedAnswer;

    private boolean answer_value;

    public ClosedAnswer getClosedAnswer() {
        return closedAnswer;
    }

    public void setClosedAnswer(ClosedAnswer closedAnswer) {
        this.closedAnswer = closedAnswer;
    }

    public boolean isAnswer_value() {
        return answer_value;
    }

    public void setAnswer_value(boolean answer_value) {
        this.answer_value = answer_value;
    }

    public Question getQuestion() {
        return closedAnswer.getQuestion();
    }


}
