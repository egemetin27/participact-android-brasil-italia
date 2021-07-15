package br.com.participact.participactbrasil.modules.db;

public class ActionPhoto extends ActionWrapper {

    protected ActionPhoto(Campaign campaign, Action action) {
        super(campaign, action);
    }

    public static ActionPhoto wrap(Campaign campaign, Action action) {
        return new ActionPhoto(campaign, action);
    }

    @Override
    public int getProgress() {
        if (action.getMinimum() == null)
            return 0;
        int progress = new PhotoDaoImpl().findByCampaignId(campaign.getId()).size() * 100 / action.getMinimum();
        return Math.min(progress, 100);
    }
}
