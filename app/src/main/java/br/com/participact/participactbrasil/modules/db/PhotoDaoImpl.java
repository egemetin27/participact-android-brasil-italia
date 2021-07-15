package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class PhotoDaoImpl {

    private PhotoDao dao = App.getInstance().getDaoSession().getPhotoDao();

    public List<Photo> findByCampaignId(Long id) {
        return dao.queryBuilder().where(PhotoDao.Properties.CampaignId.eq(id)).build().list();
    }

    public long commit(Photo photo) {
        if (photo.getId() == null) {
            return dao.insert(photo);
        } else {
            dao.update(photo);
            return photo.getId();
        }
    }

    public Photo findToUpload() {
        QueryBuilder<Photo> builder = dao.queryBuilder();
        List<Photo> entities = builder.whereOr(PhotoDao.Properties.Uploaded.eq(false), PhotoDao.Properties.Uploaded.isNull())
                .whereOr(PhotoDao.Properties.ReadyToUpload.isNull(), PhotoDao.Properties.ReadyToUpload.eq(true)).limit(1).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public void makePhotosReadyForQuestionId(Long questionId) {
        List<Photo> entities = dao.queryBuilder().where(PhotoDao.Properties.QuestionId.eq(questionId)).build().list();
        for (Photo entity : entities) {
            entity.setReadyToUpload(true);
            dao.update(entity);
        }
    }

    public List<Photo> findByQuestionId(Long questionId) {
        return dao.queryBuilder().where(PhotoDao.Properties.QuestionId.eq(questionId)).build().list();
    }

    public void delete(Photo photo) {
        dao.delete(photo);
    }
}
