package br.com.participact.participactbrasil.modules.db;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class CampaignStatusDaoImpl {

    CampaignStatusDao dao = App.getInstance().getDaoSession().getCampaignStatusDao();

    public CampaignStatus findByCampaignId(Long id) {
        List<CampaignStatus> entities = dao.queryBuilder().where(CampaignStatusDao.Properties.CampaignId.eq(id)).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public void insert(CampaignStatus entity) {
        dao.insert(entity);
    }

    public void update(CampaignStatus entity) {
        dao.update(entity);
    }

}
