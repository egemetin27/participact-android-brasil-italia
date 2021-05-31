package br.udesc.esag.participactbrasil.domain.persistence;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.List;

@DatabaseTable
public class Question implements Serializable {

    private static final long serialVersionUID = -21876732993994461L;

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField
    private String question;

    @DatabaseField
    private Integer question_order;

    @JsonIgnore
    @ForeignCollectionField(eager = true, maxEagerLevel = 2)
    private ForeignCollection<ClosedAnswer> closed_answersDB;

    private List<ClosedAnswer> closed_answers;

    @DatabaseField
    private Boolean isClosedAnswers;

    @DatabaseField
    private Boolean isMultipleAnswers;

    @DatabaseField(foreign = true)
    private ActionFlat actionFlat;

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

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestionOrder() {
        return question_order;
    }

    public void setQuestionOrder(int question_order) {
        this.question_order = question_order;
    }

    public ForeignCollection<ClosedAnswer> getClosed_answersDB() {
        return closed_answersDB;
    }

    public void setClosed_answersDB(ForeignCollection<ClosedAnswer> closed_answersDB) {
        this.closed_answersDB = closed_answersDB;
    }

    public ActionFlat getActionFlat() {
        return actionFlat;
    }

    public void setActionFlat(ActionFlat actionFlat) {
        this.actionFlat = actionFlat;
    }

    @JsonIgnore
    public int getNextClosedAnswerOrder() {
        if (closed_answersDB == null) {
            throw new IllegalStateException("closed answers can't be null");
        }
        if (closed_answersDB.size() == 0) {
            return 0;
        }
        int max = 0;
        for (ClosedAnswer ca : closed_answersDB) {
            if (ca.getAnswerOrder() > max) {
                max = ca.getAnswerOrder();
            }
        }
        max = max + 1;
        return max;
    }

    public List<ClosedAnswer> getClosed_answers() {
        return closed_answers;
    }

    public void setClosed_answers(List<ClosedAnswer> closed_answers) {
        this.closed_answers = closed_answers;
    }
}
