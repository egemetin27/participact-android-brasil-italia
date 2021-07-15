package br.com.participact.participactbrasil.modules.db;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class AbuseTypeDaoImpl {

    AbuseTypeDao dao = App.getInstance().getDaoSession().getAbuseTypeDao();

    public void save(AbuseType entity) {
        entity.setSelected(false);
        dao.insert(entity);
    }

    public List<AbuseType> fetch() {
        return dao.queryBuilder().build().list();
    }

    public void removeAll() {
        dao.deleteAll();
    }

    public AbuseType find(Long id) {
        List<AbuseType> entities = dao.queryBuilder().where(AbuseTypeDao.Properties.Id.eq(id)).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

}
