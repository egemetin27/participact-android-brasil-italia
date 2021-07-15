package br.com.participact.participactbrasil.modules.db;

import android.util.Log;

import org.greenrobot.greendao.DaoException;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class CampaignDaoImpl {

    private static final String TAG = CampaignDaoImpl.class.getSimpleName();
    private CampaignDao dao = App.getInstance().getDaoSession().getCampaignDao();

    public void save(Campaign campaign) {
        Campaign old = find(campaign.getId());
        if (old != null) {
            campaign.setRawState(old.getRawState());
            campaign.setCardOpen(old.getCardOpen());
            if (old.getActions() != null && campaign.getActions() != null) {
                for (Action oldAction : old.getActions()) {
                    for (Action action : campaign.getActions()) {
                        if (oldAction.getId().longValue() == action.getId().longValue()) {
                            try {
                                if (oldAction.getQuestions() != null && action.getQuestions() != null) {
                                    for (Question oldQuestion : oldAction.getQuestions()) {
                                        for (Question question : action.getQuestions()) {
                                            if (oldQuestion.getId().longValue() == question.getId().longValue()) {
                                                question.setAnswer(oldQuestion.getAnswer());
                                                question.setAnswerIds(oldQuestion.getAnswerIds());
                                                question.setReadyToUpload(oldQuestion.getReadyToUpload());
                                                question.setUploaded(oldQuestion.getUploaded());
                                                question.setAnswerDate(oldQuestion.getAnswerDate());
                                            }
                                        }
                                    }
                                }
                            } catch (DaoException e) {
                                Log.i(TAG, "old questions is null");
                            }
                        }
                    }
                }
            }
        }
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                action.setCampaignId(campaign.getId());
                try {
                    if (action.getQuestions() != null) {
                        for (Question question : action.getQuestions()) {
                            question.setActionId(action.getId());
                            question.setCampaignId(campaign.getId());
                            try {
                                if (question.getClosed_answers() != null) {
                                    for (QuestionOption option : question.getClosed_answers()) {
                                        option.setQuestionId(question.getId());
                                    }
                                }
                            } catch (DaoException e) {
                                Log.i(TAG, "question options is null");
                            }
                        }
                    }
                } catch (DaoException e) {
                    Log.i(TAG, "questions is null");
                }
            }
        }
        if (campaign.getCardColor() == null || campaign.getCardColor().trim().length() != 7) {
            campaign.setCardColor("#4EC500");
        }
        if (campaign.getCardOpen() == null) {
            campaign.setCardOpen(false);
        }
        if (old != null) {
            update(campaign);
        } else {
            campaign.setRawState("AVAILABLE");
            insert(campaign);
        }
    }

    public void commit(Campaign campaign) {
        if (find(campaign.getId()) != null) {
            update(campaign);
        } else {
            insert(campaign);
        }
    }

    private void update(Campaign campaign) {
        dao.update(campaign);
        ActionDao actionDao = App.getInstance().getDaoSession().getActionDao();
        QuestionDao questionDao = App.getInstance().getDaoSession().getQuestionDao();
        QuestionOptionDao questionOptionDao = App.getInstance().getDaoSession().getQuestionOptionDao();
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                try {
                    if (action.getQuestions() != null) {
                        for (Question question : action.getQuestions()) {
                            try {
                                if (question.getClosed_answers() != null) {
                                    for (QuestionOption option : question.getClosed_answers()) {
                                        questionOptionDao.update(option);
                                    }
                                }
                            } catch (DaoException e) {
                                Log.i(TAG, "question options is null");
                            }
                            questionDao.update(question);
                        }
                    }
                } catch (DaoException e) {
                    Log.i(TAG, "questions is null");
                }
                actionDao.update(action);
            }
        }
    }

    private long insert(Campaign campaign) {
        long id = dao.insert(campaign);
        ActionDao actionDao = App.getInstance().getDaoSession().getActionDao();
        QuestionDao questionDao = App.getInstance().getDaoSession().getQuestionDao();
        QuestionOptionDao questionOptionDao = App.getInstance().getDaoSession().getQuestionOptionDao();
        if (campaign.getActions() != null) {
            for (Action action : campaign.getActions()) {
                try {
                    if (action.getQuestions() != null) {
                        for (Question question : action.getQuestions()) {
                            try {
                                if (question.getClosed_answers() != null) {
                                    for (QuestionOption option : question.getClosed_answers()) {
                                        questionOptionDao.insert(option);
                                    }
                                }
                            } catch (DaoException e) {
                                Log.i(TAG, "question options is null");
                            }
                            questionDao.insert(question);
                        }
                    }
                } catch (DaoException e) {
                    Log.i(TAG, "questions is null");
                }
                actionDao.insert(action);
            }
        }
        return id;
    }

    public Campaign find(Long id) {
        List<Campaign> entities = dao.queryBuilder().where(CampaignDao.Properties.Id.eq(id)).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public void delete(Long id) {
        Campaign campaign = find(id);
        if (campaign != null) {
            dao.delete(campaign);
        }
    }

    public List<Campaign> findByState(CampaignWrapper.State state) {
        return dao.queryBuilder().where(CampaignDao.Properties.RawState.eq(state.toNetwork())).build().list();
    }

    public List<Campaign> fetchArchived() {
        return dao.queryBuilder().where(CampaignDao.Properties.Archived.isNotNull(), CampaignDao.Properties.Archived.eq(true)).orderDesc(CampaignDao.Properties.StartDateString).build().list();
    }

    public List<Campaign> fetch() {

        List<Campaign> entities = dao.queryBuilder().whereOr(CampaignDao.Properties.Archived.isNull(), CampaignDao.Properties.Archived.eq(false)).orderDesc(CampaignDao.Properties.StartDateString).build().list();
        Campaign map = find(1L);
        if (map == null) {
            map = new Campaign();
            map.setId(1L);
            map.setRawState("AVAILABLE");
            map.setName("Problemas urbanos\nStatus geral da cidade");
        }
        if (entities.size() == 0) {
            entities.add(map);
        } else {
            entities.add(0, map);
        }
        List<Campaign> archived = fetchArchived();
        if (archived.size() > 0) {
            Campaign ar = new Campaign();
            ar.setId(0L);
            ar.setRawState("AVAILABLE");
            ar.setName("Arquivadas");
            ar.setArchived(true);
            ar.setArchivedCount(archived.size());
            entities.add(0, ar);
        }
        return entities;
    }

    public List<Campaign> fetchWithoutUP() {
        return dao.queryBuilder().where(CampaignDao.Properties.Id.gt(1L)).build().list();
    }

//    public List<Campaign> findToUpdateStatus() {
//        List<Campaign> entities = findByState(CampaignWrapper.State.running);
//        entities.addAll(findByState(CampaignWrapper.State.geoNotifiedAvailable));
//        entities.addAll(findByState(CampaignWrapper.State.accepted));
//        entities.addAll(findByState(CampaignWrapper.State.available));
//        entities.addAll(findByState(CampaignWrapper.State.runningButNotExec));
//        entities.addAll(findByState(CampaignWrapper.State.suspended));
//        entities.addAll(findByState(CampaignWrapper.State.interrupted));
//        entities.addAll(findByState(CampaignWrapper.State.error));
//        entities.addAll(findByState(CampaignWrapper.State.hidden));
//        return entities;
//    }

    public List<Campaign> findAllNotSetAsCompleted() {
        List<Campaign> entities = findByState(CampaignWrapper.State.running);
        entities.addAll(findByState(CampaignWrapper.State.geoNotifiedAvailable));
        entities.addAll(findByState(CampaignWrapper.State.accepted));
        entities.addAll(findByState(CampaignWrapper.State.available));
        entities.addAll(findByState(CampaignWrapper.State.runningButNotExec));
        entities.addAll(findByState(CampaignWrapper.State.suspended));
        entities.addAll(findByState(CampaignWrapper.State.interrupted));
        entities.addAll(findByState(CampaignWrapper.State.error));
        entities.addAll(findByState(CampaignWrapper.State.hidden));
        return entities;
    }
}
