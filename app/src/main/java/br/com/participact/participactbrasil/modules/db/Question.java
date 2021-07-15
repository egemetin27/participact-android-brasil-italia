package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.annotation.*;

import java.util.List;
import br.com.participact.participactbrasil.modules.db.DaoSession;
import org.greenrobot.greendao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "QUESTION".
 */
import com.google.gson.annotations.SerializedName;
@Entity(active = true)
public class Question {

    @Id
    private Long id;
    private String question;
    /**
     * This is the MultiChoice (múltipla escolha). User chooses only one option.
     */
    @SerializedName("isClosedAnswers")
    private Boolean closedAnswers;
    /**
     * This is the Checkboxes (caixas de seleção). User can choose one or more options.
     */
    @SerializedName("isMultipleAnswers")
    private Boolean multipleSelect;
    @SerializedName("questionOrder")
    private Integer order;
    private String answer;
    private String answerIds;
    private Long campaignId;
    private Long actionId;
    private Boolean readyToUpload;
    private Boolean uploaded;
    private Boolean required;
    private Boolean skipped;
    private Integer numberPhotos;
    private Boolean photo;
    private Long targetId;
    private java.util.Date answerDate;
    private String ipAddress;
    private Boolean isDate;
    private Long answerGroupId;

    /** Used to resolve relations */
    @Generated
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated
    private transient QuestionDao myDao;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "questionId")
    })
    private List<QuestionOption> closed_answers;

    @Generated
    public Question() {
    }

    public Question(Long id) {
        this.id = id;
    }

    @Generated
    public Question(Long id, String question, Boolean closedAnswers, Boolean multipleSelect, Integer order, String answer, String answerIds, Long campaignId, Long actionId, Boolean readyToUpload, Boolean uploaded, Boolean required, Boolean skipped, Integer numberPhotos, Boolean photo, Long targetId, java.util.Date answerDate, String ipAddress, Boolean isDate, Long answerGroupId) {
        this.id = id;
        this.question = question;
        this.closedAnswers = closedAnswers;
        this.multipleSelect = multipleSelect;
        this.order = order;
        this.answer = answer;
        this.answerIds = answerIds;
        this.campaignId = campaignId;
        this.actionId = actionId;
        this.readyToUpload = readyToUpload;
        this.uploaded = uploaded;
        this.required = required;
        this.skipped = skipped;
        this.numberPhotos = numberPhotos;
        this.photo = photo;
        this.targetId = targetId;
        this.answerDate = answerDate;
        this.ipAddress = ipAddress;
        this.isDate = isDate;
        this.answerGroupId = answerGroupId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getQuestionDao() : null;
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

    public Boolean getClosedAnswers() {
        return closedAnswers;
    }

    public void setClosedAnswers(Boolean closedAnswers) {
        this.closedAnswers = closedAnswers;
    }

    public Boolean getMultipleSelect() {
        return multipleSelect;
    }

    public void setMultipleSelect(Boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(String answerIds) {
        this.answerIds = answerIds;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Boolean getReadyToUpload() {
        return readyToUpload;
    }

    public void setReadyToUpload(Boolean readyToUpload) {
        this.readyToUpload = readyToUpload;
    }

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getSkipped() {
        return skipped;
    }

    public void setSkipped(Boolean skipped) {
        this.skipped = skipped;
    }

    public Integer getNumberPhotos() {
        return numberPhotos;
    }

    public void setNumberPhotos(Integer numberPhotos) {
        this.numberPhotos = numberPhotos;
    }

    public Boolean getPhoto() {
        return photo;
    }

    public void setPhoto(Boolean photo) {
        this.photo = photo;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public java.util.Date getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(java.util.Date answerDate) {
        this.answerDate = answerDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getIsDate() {
        return isDate;
    }

    public void setIsDate(Boolean isDate) {
        this.isDate = isDate;
    }

    public Long getAnswerGroupId() {
        return answerGroupId;
    }

    public void setAnswerGroupId(Long answerGroupId) {
        this.answerGroupId = answerGroupId;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    @Generated
    public List<QuestionOption> getClosed_answers() {
        if (closed_answers == null) {
            __throwIfDetached();
            QuestionOptionDao targetDao = daoSession.getQuestionOptionDao();
            List<QuestionOption> closed_answersNew = targetDao._queryQuestion_Closed_answers(id);
            synchronized (this) {
                if(closed_answers == null) {
                    closed_answers = closed_answersNew;
                }
            }
        }
        return closed_answers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated
    public synchronized void resetClosed_answers() {
        closed_answers = null;
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void delete() {
        __throwIfDetached();
        myDao.delete(this);
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void update() {
        __throwIfDetached();
        myDao.update(this);
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void refresh() {
        __throwIfDetached();
        myDao.refresh(this);
    }

    @Generated
    private void __throwIfDetached() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
    }

}