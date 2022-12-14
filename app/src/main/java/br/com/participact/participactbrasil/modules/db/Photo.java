package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "PHOTO".
 */
@Entity
public class Photo {

    @Id(autoincrement = true)
    private Long id;
    private String filename;
    private Double latitude;
    private Double longitude;
    private String provider;
    private Boolean uploaded;
    private Boolean readyToUpload;
    private Long questionId;
    private Long actionId;
    private Long campaignId;
    private java.util.Date dateUploaded;
    private String ipAddress;
    private Long answerGroupId;

    @Generated
    public Photo() {
    }

    public Photo(Long id) {
        this.id = id;
    }

    @Generated
    public Photo(Long id, String filename, Double latitude, Double longitude, String provider, Boolean uploaded, Boolean readyToUpload, Long questionId, Long actionId, Long campaignId, java.util.Date dateUploaded, String ipAddress, Long answerGroupId) {
        this.id = id;
        this.filename = filename;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.uploaded = uploaded;
        this.readyToUpload = readyToUpload;
        this.questionId = questionId;
        this.actionId = actionId;
        this.campaignId = campaignId;
        this.dateUploaded = dateUploaded;
        this.ipAddress = ipAddress;
        this.answerGroupId = answerGroupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Boolean getReadyToUpload() {
        return readyToUpload;
    }

    public void setReadyToUpload(Boolean readyToUpload) {
        this.readyToUpload = readyToUpload;
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

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public java.util.Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(java.util.Date dateUploaded) {
        this.dateUploaded = dateUploaded;
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

}
