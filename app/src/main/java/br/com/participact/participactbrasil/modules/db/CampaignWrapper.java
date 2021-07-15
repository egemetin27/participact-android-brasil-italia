package br.com.participact.participactbrasil.modules.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.bergmannsoft.util.FileCache;
import com.bergmannsoft.util.Utils;

import org.most.pipeline.Pipeline;

import java.util.Date;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.support.CampaignService;

public class CampaignWrapper {

    private Campaign campaign;
    private CampaignStatus campaignStatus;

    private CampaignWrapper(Campaign campaign) {
        this.campaign = campaign;
        this.campaignStatus = new CampaignStatusDaoImpl().findByCampaignId(campaign.getId());
    }

    public static CampaignWrapper wrap(Campaign campaign) {
        return new CampaignWrapper(campaign);
    }

    private boolean isCompleted() {
        return getProgress() >= 100;
    }

    public boolean isStatusCompleted() {
        return campaignStatus != null && campaignStatus.getCampaignProgress() != null ? campaignStatus.getCampaignProgress() >= 100 : false;
    }

    public boolean shouldArchiveAutomatically() {
        State st = State.fromNetwork(campaign.getRawState());
        return st == State.available || st == State.hidden;
    }

    public void archive() {
        campaign.setArchived(true);
    }

    public Integer getStatusProgress() {
        return campaignStatus != null && campaignStatus.getCampaignProgress() != null ? campaignStatus.getCampaignProgress() : 0;
    }

    private Integer getProgress() {
        if (campaign.getActions() != null && campaign.getActions().size() > 0) {
            int progress = 0;
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isQuestion() && wrapper.isRepeat()) {
                    progress += percentDateProgress();
                } else {
                    progress += wrapper.getProgress();
                }
            }
            return Math.min(progress / campaign.getActions().size(), 100);
        }
        return 0;
    }

    public Integer getSensingProgress() {
        if (hasSensing()) {
            int progress = 0;
            int total = 0;
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isSensing()) {
                    progress += wrapper.getProgress();
                    total++;
                }
            }
            return Math.min(progress / total, 100);
        }
        return 100;
    }

    public Integer getPhotoProgress() {
        if (hasPhotos()) {
            int progress = 0;
            int total = 0;
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isPhoto()) {
                    progress += wrapper.getProgress();
                    total++;
                }
            }
            return Math.min(progress / total, 100);
        }
        return 100;
    }

    public Integer getQuestionnaireProgress() {
        if (hasQuestions()) {
            int progress = 0;
            int total = 0;
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isQuestion()) {
                    if (wrapper.isRepeat()) {
                        progress += percentDateProgress();
                    } else {
                        progress += wrapper.getProgress();
                    }
                    total++;
                }
            }
            return Math.min(progress / total, 100);
        }
        return 100;
    }

    public boolean isMap() {
        return campaign.getId() == 1;
    }

    public boolean isArchivedHeader() {
        return campaign.getId() == 0 && campaign.getArchived();
    }

    public State getState() {
        return State.fromNetwork(campaign.getRawState());
    }

    public void setState(State state) {
        campaign.setRawState(state.toNetwork());
    }

    @NonNull
    private String iconKey() {
        return "campaign_icon" + campaign.getId();
    }

    public Bitmap icon(final Context context, final FileCache.OnBitmapDownloadedListener listener) {
        Bitmap bmp = App.getInstance().getFileCache().getBitmap(iconKey());
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo_symbol);
            if (campaign.getCardIconUrl() != null) {
                App.getInstance().getFileCache().downloadAsync(iconKey(), campaign.getCardIconUrl(), new FileCache.FileCacheCallback() {
                    @Override
                    public void onDownloadDone(String key, Bitmap bmp) {
                        if (bmp == null) {
                            bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo_symbol);
                        }
                        listener.onDownloaded(bmp);
                    }
                });
            }
        }
        return bmp;
    }

    public String getStartDateFormatted() {
//        Date dt = Utils.stringToDate(campaign.getStartDateString(), "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        Date dt = Utils.stringToDate(campaign.getStartDateString(), "yyyy-MM-dd HH:mm:ss");
        if (dt != null) {
            String sdt = Utils.dateToString("dd/MM/yy", dt);
            if (sdt != null) {
                return sdt;
            }
        }
        return "";
    }

    public String getEndDateFormatted() {
//        Date dt = Utils.stringToDate(campaign.getDeadlineDateString(), "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        Date dt = Utils.stringToDate(campaign.getDeadlineDateString(), "yyyy-MM-dd HH:mm:ss");
        if (dt != null) {
            String sdt = Utils.dateToString("dd/MM/yy", dt);
            if (sdt != null) {
                return sdt;
            }
        }
        return "";
    }

    public void setAgreementAccepted(boolean accepted) {
        campaign.setAgreementAccepted(accepted);
        new CampaignDaoImpl().commit(campaign);
    }

    public int percentStatusDateProgress() {
        return campaignStatus != null && campaignStatus.getCampaignDateProgress() != null ? campaignStatus.getCampaignDateProgress() : 0;
    }

    private int percentDateProgress() {
        if (getStartDate() != null && getEndDate() != null) {
            Date now = new Date();
            Date start = getStartDate();
            Date end = getEndDate();
            long remaining = end.getTime() - now.getTime();
            if (remaining < 0)
                remaining = 0;
            long total = end.getTime() - start.getTime();
            long current = total - remaining;
            int percent = (int) (current * 100 / total);
            return percent;
        }
        return 0;
    }

    public Date getStartDate() {
        Date d = Utils.stringToDate(campaign.getStartDateString(), "yyyy-MM-dd HH:mm:ss");
        if (d == null) {
            d = Utils.stringToDate(campaign.getStartDateString(), "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        }
        return d;
    }

    public Date getEndDate() {
        Date d = Utils.stringToDate(campaign.getDeadlineDateString(), "yyyy-MM-dd HH:mm:ss");
        if (d == null) {
            d = Utils.stringToDate(campaign.getDeadlineDateString(), "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        }
        return d;
    }

    private boolean isEnded() {
        return percentDateProgress() >= 100;
    }

    public boolean isStatusEnded() {
        return campaignStatus != null && campaignStatus.getCampaignDateProgress() != null ? campaignStatus.getCampaignDateProgress() >= 100 : false;
    }

    public void pauseResume() {
        if (getState() == State.running) {
            stop();
        } else {
            resume();
        }
    }

    public static void resumeAll() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Campaign> campaigns = new CampaignDaoImpl().findByState(State.running);
                for (Campaign campaign : campaigns) {
                    CampaignWrapper.wrap(campaign).resume();
                }
            }
        });
        thread.setName("Resume All");
        thread.start();
    }

    public void resume() {
        if (isEnded())
            return;

        setState(State.running);
        commit();

        if (hasSensing()) {
            CampaignService.perform(CampaignService.MostAction.start, campaign);
        }

    }

    public void stop() {
        setState(State.suspended);
        commit();
        if (hasSensing()) {
            CampaignService.perform(CampaignService.MostAction.stop, campaign);
        }
    }

    public boolean hasSensing() {
        return hasSensing(null);
    }

    public boolean hasSensing(Pipeline.Type type) {
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isSensing()) {
                    if (type != null) {
                        if (wrapper.getInputType() == type) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasPhotos() {
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isPhoto()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTasks() {
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isPhoto() || wrapper.isQuestion()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasQuestions() {
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
                if (wrapper.isQuestion()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void commit() {
        new CampaignDaoImpl().commit(campaign);
    }

    public boolean isAgreementAccepted() {
        return campaign.getAgreementAccepted() != null && campaign.getAgreementAccepted();
    }

    public void setCardOpen(boolean open) {
        campaign.setCardOpen(open);
        commit();
    }

    public void toggleCardOpen() {
        setCardOpen(!isCardOpen());
    }

    public boolean isArchived() {
        return campaign.getArchived() != null && campaign.getArchived();
    }

    public enum State {
        available,
        geoNotifiedAvailable,
        accepted,
        running,
        runningButNotExec,
        suspended,
        rejected,
        failed,
        interrupted,
        error,
        hidden,
        completedButNotSyncedWithServer,
        completedWithSuccess,
        completedWithFailure;

        public static State fromNetwork(String str) {
            switch (str) {
                case "AVAILABLE":
                    return available;
                case "GEO_NOTIFIED_AVAILABLE":
                    return geoNotifiedAvailable;
                case "ACCEPTED":
                    return accepted;
                case "RUNNING":
                    return running;
                case "RUNNING_BUT_NOT_EXEC":
                    return runningButNotExec;
                case "SUSPENDED":
                    return suspended;
                case "REJECTED":
                    return rejected;
                case "FAILED":
                    return failed;
                case "INTERRUPTED":
                    return interrupted;
                case "ERROR":
                    return error;
                case "HIDDEN":
                    return hidden;
                case "COMPLETED_NOT_SYNC_WITH_SERVER":
                    return completedButNotSyncedWithServer;
                case "COMPLETED_WITH_SUCCESS":
                    return completedWithSuccess;
                case "COMPLETED_WITH_UNSUCCESS":
                    return completedWithFailure;
                default:
                    return available;
            }
        }

        public String toNetwork() {
            switch (this) {
                case available:
                    return "AVAILABLE";
                case geoNotifiedAvailable:
                    return "GEO_NOTIFIED_AVAILABLE";
                case accepted:
                    return "ACCEPTED";
                case running:
                    return "RUNNING";
                case runningButNotExec:
                    return "RUNNING_BUT_NOT_EXEC";
                case suspended:
                    return "SUSPENDED";
                case rejected:
                    return "REJECTED";
                case failed:
                    return "FAILED";
                case interrupted:
                    return "INTERRUPTED";
                case error:
                    return "ERROR";
                case hidden:
                    return "HIDDEN";
                case completedButNotSyncedWithServer:
                    return "COMPLETED_NOT_SYNC_WITH_SERVER";
                case completedWithSuccess:
                    return "COMPLETED_WITH_SUCCESS";
                case completedWithFailure:
                    return "COMPLETED_WITH_UNSUCCESS";
            }
            return "";
        }

        public String description() {
            switch (this) {
                case available:
                    return "Disponível";
                case geoNotifiedAvailable:
                    return "Geo notificação disponível";
                case accepted:
                    return "Aceito";
                case running:
                    return "Rodando";
                case runningButNotExec:
                    return "Rodando mas não executando";
                case suspended:
                    return "Pausado";
                case rejected:
                    return "Rejeitado";
                case failed:
                    return "Falhou";
                case interrupted:
                    return "Interrompido";
                case error:
                    return "Error";
                case hidden:
                    return "Escondido";
                case completedButNotSyncedWithServer:
                    return "Completado mas não sincronizado";
                case completedWithSuccess:
                    return "Completado com sucesso";
                case completedWithFailure:
                    return "Completado com falha";
            }
            return "";
        }

    }

    public boolean isCardOpen() {
        return campaign.getCardOpen() != null && campaign.getCardOpen();
    }

    public void updateStatus() {
        /*
        boolean isEnded = campaignWrapper.isEnded();
        boolean isCompleted = campaignWrapper.isCompleted();
        int progress = campaignWrapper.getProgress();
        int dateProgress = campaignWrapper.percentDateProgress();
         */
        CampaignStatusDaoImpl dao = new CampaignStatusDaoImpl();
        CampaignStatus status = dao.findByCampaignId(campaign.getId());
        if (status == null) {
            status = new CampaignStatus();
            status.setCampaignId(campaign.getId());
            dao.insert(status);
        }
        status.setEnded(isEnded());
        status.setCompleted(isCompleted());
        status.setCampaignProgress(getProgress());
        status.setCampaignDateProgress(percentDateProgress());
        dao.update(status);
    }

}
