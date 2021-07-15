package br.com.participact.participactbrasil.modules.db;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class QuestionDaoImpl {

    private QuestionDao dao = App.getInstance().getDaoSession().getQuestionDao();

    public void update(Question question) {
        dao.update(question);
    }

    public Question getLastAnswered(Long actionId) {
        List<Question> entities = dao.queryBuilder().where(QuestionDao.Properties.AnswerDate.isNotNull(), QuestionDao.Properties.ActionId.eq(actionId)).orderDesc(QuestionDao.Properties.AnswerDate).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public Question find(Long questionId) {
        List<Question> entities = dao.queryBuilder().where(QuestionDao.Properties.Id.eq(questionId)).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }
}
