package br.com.participact.participactbrasil.modules.db;

import java.io.File;
import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class ActionDaoImpl {

    private ActionDao dao = App.getInstance().getDaoSession().getActionDao();

    public Action find(Long actionId) {
        List<Action> entities = dao.queryBuilder().where(ActionDao.Properties.Id.eq(actionId)).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public boolean isRepeat(Long actionId) {
        Action action = find(actionId);
        if (action != null) {
            return action.getRepeat() != null && action.getRepeat();
        }
        return false;
    }

//    public boolean isRepeatAndAllUploaded(Long actionId) {
//        Action action = find(actionId);
//        if (action != null) {
//            if (action.getRepeat() != null && action.getRepeat()) {
//                if (action.getQuestions() != null) {
//                    Campaign campaign = new CampaignDaoImpl().find(action.getCampaignId());
//                    ActionWrapper wrapper = ActionWrapper.wrap(campaign, action);
//                    if (wrapper.getNextQuestion() == null) {
//                        boolean allUploaded = true;
//                        for (Question question : action.getQuestions()) {
//                            if (question.getReadyToUpload() != null && question.getReadyToUpload()) {
//                                if (question.getUploaded() == null || !question.getUploaded()) {
//                                    allUploaded = false;
//                                    break;
//                                }
//                            }
//                            List<Photo> photos = new PhotoDaoImpl().findByQuestionId(question.getId());
//                            for (Photo photo : photos) {
//                                if (photo.getReadyToUpload() != null && photo.getReadyToUpload()) {
//                                    if (photo.getUploaded() == null || !photo.getUploaded()) {
//                                        allUploaded = false;
//                                        break;
//                                    }
//                                }
//                            }
//
//                        }
//                        return allUploaded;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    public void prepareToRepeat(Long actionId) {
        Action action = find(actionId);
        if (action != null) {
            if (action.getRepeat() != null && action.getRepeat()) {
                if (action.getQuestions() != null) {
                    QuestionDaoImpl questionDao = new QuestionDaoImpl();
                    PhotoDaoImpl photoDao = new PhotoDaoImpl();
                    for (Question question : action.getQuestions()) {
                        question.setReadyToUpload(false);
                        question.setUploaded(false);
                        question.setAnswerDate(null);
                        question.setAnswer(null);
                        question.setAnswerIds(null);
                        questionDao.update(question);
                        List<Photo> photos = photoDao.findByQuestionId(question.getId());
                        for (Photo photo : photos) {
//                            if (photo.getFilename() != null) {
//                                File file = new File(photo.getFilename());
//                                if (file.exists()) {
//                                    file.delete();
//                                }
//                            }
                            photoDao.delete(photo);
                        }
                    }
                }
            }
        }
    }
}
