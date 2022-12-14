package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "QUESTION_ANSWER".
 */
@Entity
public class QuestionAnswer {

    @Id(autoincrement = true)
    private Long id;
    private String answer;
    private String answerIds;
    private Long campaignId;
    private Long questionId;
    private Long actionId;
    private Boolean readyToUpload;
    private String ipAddress;
    private Long answerGroupId;
    private String filename;
    private Double latitude;
    private Double longitude;
    private String provider;
    private Boolean photo;

    @Generated
    public QuestionAnswer() {
    }

    public QuestionAnswer(Long id) {
        this.id = id;
    }

    @Generated
    public QuestionAnswer(Long id, String answer, String answerIds, Long campaignId, Long questionId, Long actionId, Boolean readyToUpload, String ipAddress, Long answerGroupId, String filename, Double latitude, Double longitude, String provider, Boolean photo) {
        this.id = id;
        this.answer = answer;
        this.answerIds = answerIds;
        this.campaignId = campaignId;
        this.questionId = questionId;
        this.actionId = actionId;
        this.readyToUpload = readyToUpload;
        this.ipAddress = ipAddress;
        this.answerGroupId = answerGroupId;
        this.filename = filename;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.photo = photo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getAnswerGroupId() {
        return answerGroupId;
    }

    public void setAnswerGroupId(Long answerGroupId) {
        this.answerGroupId = answerGroupId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getPhoto() {
        return photo;
    }

    public void setPhoto(Boolean photo) {
        this.photo = photo;
    }

}
