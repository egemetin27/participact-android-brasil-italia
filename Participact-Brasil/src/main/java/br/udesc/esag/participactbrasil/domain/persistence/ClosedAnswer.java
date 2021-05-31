package br.udesc.esag.participactbrasil.domain.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class ClosedAnswer implements Serializable {

    private static final long serialVersionUID = 7301710346796107097L;

    @DatabaseField(id = true)
    private Long id;

    @JsonIgnore
    @DatabaseField(foreign = true, maxForeignAutoRefreshLevel = 2)
    private Question question;

    @DatabaseField
    private String answerDescription;

    @DatabaseField
    private Integer answerOrder;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
