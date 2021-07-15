package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.annotation.*;

import java.util.List;
import br.com.participact.participactbrasil.modules.db.DaoSession;
import org.greenrobot.greendao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "CAMPAIGN".
 */
import com.google.gson.annotations.SerializedName;
@Entity(active = true)
public class Campaign {

    @Id
    private Long id;
    private String name;
    @SerializedName("description")
    private String text;
    @SerializedName("start")
    private String startDateString;
    @SerializedName("deadline")
    private String deadlineDateString;
    private Long duration;
    private Long sensingDuration;
    @SerializedName("canBeRefused")
    private Boolean refusable;
    private String notificationArea;
    private String activationArea;
    private String agreement;
    private Boolean sensingWeekSun;
    private Boolean sensingWeekMon;
    private Boolean sensingWeekTue;
    private Boolean sensingWeekWed;
    private Boolean sensingWeekThu;
    private Boolean sensingWeekFri;
    private Boolean sensingWeekSat;
    private Boolean cardOpen;
    private String rawState;
    @SerializedName("color")
    private String cardColor;
    @SerializedName("iconUrl")
    private String cardIconUrl;
    private Boolean agreementAccepted;
    private Boolean archived;
    private Integer archivedCount;

    /** Used to resolve relations */
    @Generated
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated
    private transient CampaignDao myDao;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "campaignId")
    })
    private List<Action> actions;

    @Generated
    public Campaign() {
    }

    public Campaign(Long id) {
        this.id = id;
    }

    @Generated
    public Campaign(Long id, String name, String text, String startDateString, String deadlineDateString, Long duration, Long sensingDuration, Boolean refusable, String notificationArea, String activationArea, String agreement, Boolean sensingWeekSun, Boolean sensingWeekMon, Boolean sensingWeekTue, Boolean sensingWeekWed, Boolean sensingWeekThu, Boolean sensingWeekFri, Boolean sensingWeekSat, Boolean cardOpen, String rawState, String cardColor, String cardIconUrl, Boolean agreementAccepted, Boolean archived, Integer archivedCount) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.startDateString = startDateString;
        this.deadlineDateString = deadlineDateString;
        this.duration = duration;
        this.sensingDuration = sensingDuration;
        this.refusable = refusable;
        this.notificationArea = notificationArea;
        this.activationArea = activationArea;
        this.agreement = agreement;
        this.sensingWeekSun = sensingWeekSun;
        this.sensingWeekMon = sensingWeekMon;
        this.sensingWeekTue = sensingWeekTue;
        this.sensingWeekWed = sensingWeekWed;
        this.sensingWeekThu = sensingWeekThu;
        this.sensingWeekFri = sensingWeekFri;
        this.sensingWeekSat = sensingWeekSat;
        this.cardOpen = cardOpen;
        this.rawState = rawState;
        this.cardColor = cardColor;
        this.cardIconUrl = cardIconUrl;
        this.agreementAccepted = agreementAccepted;
        this.archived = archived;
        this.archivedCount = archivedCount;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCampaignDao() : null;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getDeadlineDateString() {
        return deadlineDateString;
    }

    public void setDeadlineDateString(String deadlineDateString) {
        this.deadlineDateString = deadlineDateString;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSensingDuration() {
        return sensingDuration;
    }

    public void setSensingDuration(Long sensingDuration) {
        this.sensingDuration = sensingDuration;
    }

    public Boolean getRefusable() {
        return refusable;
    }

    public void setRefusable(Boolean refusable) {
        this.refusable = refusable;
    }

    public String getNotificationArea() {
        return notificationArea;
    }

    public void setNotificationArea(String notificationArea) {
        this.notificationArea = notificationArea;
    }

    public String getActivationArea() {
        return activationArea;
    }

    public void setActivationArea(String activationArea) {
        this.activationArea = activationArea;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public Boolean getSensingWeekSun() {
        return sensingWeekSun;
    }

    public void setSensingWeekSun(Boolean sensingWeekSun) {
        this.sensingWeekSun = sensingWeekSun;
    }

    public Boolean getSensingWeekMon() {
        return sensingWeekMon;
    }

    public void setSensingWeekMon(Boolean sensingWeekMon) {
        this.sensingWeekMon = sensingWeekMon;
    }

    public Boolean getSensingWeekTue() {
        return sensingWeekTue;
    }

    public void setSensingWeekTue(Boolean sensingWeekTue) {
        this.sensingWeekTue = sensingWeekTue;
    }

    public Boolean getSensingWeekWed() {
        return sensingWeekWed;
    }

    public void setSensingWeekWed(Boolean sensingWeekWed) {
        this.sensingWeekWed = sensingWeekWed;
    }

    public Boolean getSensingWeekThu() {
        return sensingWeekThu;
    }

    public void setSensingWeekThu(Boolean sensingWeekThu) {
        this.sensingWeekThu = sensingWeekThu;
    }

    public Boolean getSensingWeekFri() {
        return sensingWeekFri;
    }

    public void setSensingWeekFri(Boolean sensingWeekFri) {
        this.sensingWeekFri = sensingWeekFri;
    }

    public Boolean getSensingWeekSat() {
        return sensingWeekSat;
    }

    public void setSensingWeekSat(Boolean sensingWeekSat) {
        this.sensingWeekSat = sensingWeekSat;
    }

    public Boolean getCardOpen() {
        return cardOpen;
    }

    public void setCardOpen(Boolean cardOpen) {
        this.cardOpen = cardOpen;
    }

    public String getRawState() {
        return rawState;
    }

    public void setRawState(String rawState) {
        this.rawState = rawState;
    }

    public String getCardColor() {
        return cardColor;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }

    public String getCardIconUrl() {
        return cardIconUrl;
    }

    public void setCardIconUrl(String cardIconUrl) {
        this.cardIconUrl = cardIconUrl;
    }

    public Boolean getAgreementAccepted() {
        return agreementAccepted;
    }

    public void setAgreementAccepted(Boolean agreementAccepted) {
        this.agreementAccepted = agreementAccepted;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Integer getArchivedCount() {
        return archivedCount;
    }

    public void setArchivedCount(Integer archivedCount) {
        this.archivedCount = archivedCount;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    @Generated
    public List<Action> getActions() {
        if (actions == null) {
            __throwIfDetached();
            ActionDao targetDao = daoSession.getActionDao();
            List<Action> actionsNew = targetDao._queryCampaign_Actions(id);
            synchronized (this) {
                if(actions == null) {
                    actions = actionsNew;
                }
            }
        }
        return actions;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated
    public synchronized void resetActions() {
        actions = null;
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