package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class UPFileDaoImpl {

    private UPFileDBDao dao = App.getInstance().getDaoSession().getUPFileDBDao();

    public void save(UPFileDB entity) {
        if (entity.getId() != null && find(entity.getId()) != null) {
            dao.update(entity);
        } else {
            dao.insert(entity);
        }
    }

    public UPFileDB find(long id) {
        List<UPFileDB> entities = dao.queryBuilder().where(UPFileDBDao.Properties.Id.eq(id)).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public UPFileDB findToUpload() {
        QueryBuilder<UPFileDB> builder = dao.queryBuilder();
        builder = builder.whereOr(UPFileDBDao.Properties.Uploaded.isNull(), UPFileDBDao.Properties.Uploaded.eq(false));
        List<UPFileDB> entities = builder.build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public void reuploadVideos() {
        List<UPFileDB> entities = dao.queryBuilder().where(UPFileDBDao.Properties.Type.eq("video")).build().list();
        for (UPFileDB file : entities) {
            file.setUploaded(false);
            save(file);
        }
    }

    public void setUploaded(UPFileDB entity) {
        entity.setUploaded(true);
        dao.update(entity);
    }

    public void delete(UPFileDB entity) {
        dao.delete(entity);
    }

}
