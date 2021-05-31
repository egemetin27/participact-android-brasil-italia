package br.udesc.esag.participactbrasil.domain.persistence;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.List;

@DatabaseTable
public class ActionFlat implements Serializable {

    private static final long serialVersionUID = -5740158687680689204L;

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private Integer numeric_threshold;

    @DatabaseField
    private Integer duration_threshold;

    @DatabaseField
    private Integer input_type;

    @JsonIgnore
    @ForeignCollectionField(eager = true, orderColumnName = "question_order")
    private ForeignCollection<Question> questionsDB;

    private List<Question> questions;

    @DatabaseField(dataType = DataType.ENUM_STRING)
    private ActionType type;

    @DatabaseField
    private String title;

    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TaskFlat task;

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

    public ActionFlat() {
    }

    public ActionFlat(Action action) {
    }

    public ActionFlat(ActionSensing action) {
        init(action);
        this.input_type = action.getInput_type();
        this.setType(ActionType.SENSING_MOST);
    }

    public ActionFlat(ActionPhoto action) {
        init(action);
        this.setType(ActionType.PHOTO);
    }

    public ActionFlat(ActionQuestionaire action) {
        init(action);
        this.questions = action.getQuestions();
        this.setTitle(action.getTitle());
        this.setDescription(action.getDescription());
        this.setType(ActionType.QUESTIONNAIRE);
    }

    public ActionFlat(ActionActivityDetection action) {
        init(action);
        this.setType(ActionType.ACTIVITY_DETECTION);
        this.duration_threshold = action.getDuration_threshold();
    }

    private void init(Action action) {
        this.id = action.getId();
        this.name = action.getName();
        this.duration_threshold = action.getDuration_threshold();
        this.numeric_threshold = action.getNumeric_threshold();
        this.input_type = -1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ForeignCollection<Question> getQuestionsDB() {
        return questionsDB;
    }

    public void setQuestionsDB(ForeignCollection<Question> questionsDB) {
        this.questionsDB = questionsDB;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public TaskFlat getTask() {
        return task;
    }

    public void setTask(TaskFlat task) {
        this.task = task;
    }


    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
