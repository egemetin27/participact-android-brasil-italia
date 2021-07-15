package br.com.participact.participactbrasil.modules.db;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class PANotificationDaoImpl {

    PANotificationDao dao = App.getInstance().getDaoSession().getPANotificationDao();

    public List<PANotification> fetch() {
        return dao.queryBuilder().orderDesc(PANotificationDao.Properties.Id).build().list();
    }

    public void save(PANotification entity) {
        entity.setRead(true);
        dao.insert(entity);
    }

    public int getUnreadCount() {
        return dao.queryBuilder().where(PANotificationDao.Properties.Read.eq(false)).build().list().size();
    }

    public void markAllRead() {
        List<PANotification> entities = dao.queryBuilder().where(PANotificationDao.Properties.Read.eq(false)).build().list();
        for (PANotification entity : entities) {
            entity.setRead(true);
            dao.update(entity);
        }
    }
}
