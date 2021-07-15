package br.com.participact.participactbrasil.modules.db;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class PendingRequestDaoImpl {

    PendingRequestDao dao = App.getInstance().getDaoSession().getPendingRequestDao();

    public void save(PendingRequest entity) {
        dao.insert(entity);
    }

    public PendingRequest pop() {
        List<PendingRequest> entities = dao.queryBuilder().build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public void save(Campaign campaign) {
        PendingRequest entity = new PendingRequest();
        entity.setCampaignId(campaign.getId());
        entity.setRequestType("participate");
        save(entity);
    }

    public void remove(PendingRequest entity) {
        dao.delete(entity);
    }
}
