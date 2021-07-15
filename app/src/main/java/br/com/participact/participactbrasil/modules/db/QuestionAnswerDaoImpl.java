package br.com.participact.participactbrasil.modules.db;

import com.bergmannsoft.util.Utils;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class QuestionAnswerDaoImpl {

    QuestionAnswerDao dao = App.getInstance().getDaoSession().getQuestionAnswerDao();

    public void commit(QuestionAnswer entity) {
        dao.insert(entity);
    }

    public List<QuestionAnswer> findToUpload() {
        return dao.queryBuilder().where(QuestionAnswerDao.Properties.ReadyToUpload.eq(true)).build().list();
    }

    public void makeReadyToUpload(Long actionId) {
        long timestamp = System.currentTimeMillis() / 1000;
        List<QuestionAnswer> entities = dao.queryBuilder().where(QuestionAnswerDao.Properties.ActionId.eq(actionId)).whereOr(QuestionAnswerDao.Properties.ReadyToUpload.isNull(), QuestionAnswerDao.Properties.ReadyToUpload.eq(false)).build().list();
//        PhotoDaoImpl photoDao = new PhotoDaoImpl();
        for (QuestionAnswer entity : entities) {
            entity.setAnswerGroupId(timestamp);
            entity.setReadyToUpload(true);
            //photoDao.makePhotosReadyForQuestionId(entity.getQuestionId());
            dao.update(entity);
        }
    }

    public void remove(QuestionAnswer entity) {
        dao.delete(entity);
    }
}
