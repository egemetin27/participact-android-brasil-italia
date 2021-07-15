package br.com.participact.participactbrasil.modules.support;

import android.util.Log;

import com.bergmannsoft.rest.Response;
import com.bergmannsoft.retrofit.ProgressRequestBody;

import org.most.pipeline.Pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Action;
import br.com.participact.participactbrasil.modules.db.ActionQuestionnaire;
import br.com.participact.participactbrasil.modules.db.ActionWrapper;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.LogDaoImpl;
import br.com.participact.participactbrasil.modules.db.PendingRequest;
import br.com.participact.participactbrasil.modules.db.PendingRequestDaoImpl;
import br.com.participact.participactbrasil.modules.db.Photo;
import br.com.participact.participactbrasil.modules.db.PhotoDaoImpl;
import br.com.participact.participactbrasil.modules.db.PhotoWrapper;
import br.com.participact.participactbrasil.modules.db.Question;
import br.com.participact.participactbrasil.modules.db.QuestionAnswer;
import br.com.participact.participactbrasil.modules.db.QuestionAnswerDaoImpl;
import br.com.participact.participactbrasil.modules.db.QuestionDaoImpl;
import br.com.participact.participactbrasil.modules.db.UPFileDB;
import br.com.participact.participactbrasil.modules.db.UPFileDBWrapper;
import br.com.participact.participactbrasil.modules.db.UPFileDaoImpl;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.ParticipateResponse;
import br.com.participact.participactbrasil.modules.network.requests.StorageFilePhotoRequest;
import br.com.participact.participactbrasil.modules.network.requests.StorageFileRequest;

public class DataUploader {

    private static final String TAG = DataUploader.class.getSimpleName();
    private static DataUploader instance;

    private DataUploader() {

    }

    public static DataUploader getInstance() {
        if (instance == null) {
            instance = new DataUploader();
        }
        return instance;
    }

    private boolean updatingStatus = false;
    private boolean uploadingUPFiles = false;
    private boolean uploadingQuestions = false;
    private boolean uploadingQuestionAnswers = false;
    private boolean uploadingPhotos = false;
    private boolean uploadingSensing = false;
    private boolean uploadingUPSensing = false;
    private boolean checkingCompleted = false;
    private boolean uploadingLogs = false;
    private boolean uploadingPendingRequests = false;

    private List<Question> questionsUploading = new ArrayList<>();
    private List<QuestionAnswer> questionAnswersUploading = new ArrayList<>();

    public void uploadAll() {
        uploadPendingRequest();
        updateStatus();
        uploadUPFiles();
        uploadQuestions();
        uploadQuestionAnswers();
        uploadPhotos();
        uploadSensing();
        uploadUPSensing();
        checkCompleted();
        uploadLogs();
    }

    private void uploadPendingRequest() {
        if (!uploadingPendingRequests) {
            uploadingPendingRequests = true;
            doUploadPendingRequest();
        }
    }

    private void doUploadPendingRequest() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final PendingRequestDaoImpl dao = new PendingRequestDaoImpl();
                final PendingRequest request = dao.pop();
                if (request != null) {
                    SessionManager.getInstance().participate(request.getCampaignId(), new SessionManager.RequestCallback<ParticipateResponse>() {
                        @Override
                        public void onResponse(ParticipateResponse response) {
                            if (response != null && response.isSuccess()) {
                                dao.remove(request);
                                doUploadPendingRequest();
                            } else {
                                uploadingPendingRequests = false;
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            uploadingPendingRequests = false;
                        }
                    });
                } else {
                    uploadingPendingRequests = false;
                }
            }
        });
        thread.setName("Upload Pending Requests");
        thread.start();
    }

    private void updateStatus() {
        if (!updatingStatus) {
            updatingStatus = true;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long t1 = System.currentTimeMillis();
                    List<Campaign> entities = new CampaignDaoImpl().findAllNotSetAsCompleted();
                    for (Campaign entity : entities) {
                        CampaignWrapper.wrap(entity).updateStatus();
//                        if (entity.getActions() != null) {
//                            for (Action action : entity.getActions()) {
//                                if (action.getRepeat() != null && action.getRepeat()) {
//                                    ActionDaoImpl actionDao = new ActionDaoImpl();
//                                    if (actionDao.isRepeatAndAllUploaded(action.getId())) {
//                                        actionDao.prepareToRepeat(action.getId());
//                                    }
//                                }
//                            }
//                        }
                    }
                    Log.d(TAG, "updateStatus time: " + (System.currentTimeMillis() - t1));
                    updatingStatus = false;
                }
            });
            thread.setName("Update Status");
            thread.start();
        }
    }

    private void uploadLogs() {
        if (!uploadingLogs) {
            uploadingLogs = true;
            doUploadLogs();
        }
    }

    private void doUploadLogs() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                nextLog();
            }
        });
        thread.setName("Upload Logs");
        thread.start();
    }

    private void nextLog() {
        final br.com.participact.participactbrasil.modules.db.Log log = new LogDaoImpl().pop();
        if (log != null) {
            SessionManager.getInstance().sendLog(log, new SessionManager.RequestCallback<Response>() {
                @Override
                public void onResponse(Response response) {
                    if (response != null && response.isSuccess()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new LogDaoImpl().delete(log);
                                nextLog();
                            }
                        }).start();

                    } else {
                        uploadingLogs = false;
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    uploadingLogs = false;
                }
            });
        } else {
            uploadingLogs = false;
        }
    }

    private void checkCompleted() {
        if (!checkingCompleted) {
            checkingCompleted = true;
            doCheckCompleted();
        }
    }

    private void doCheckCompleted() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Pipeline.Type[] types = Pipeline.Type.values();
                for (Pipeline.Type type : types) {
                    boolean found = false;
                    List<Campaign> entities = new CampaignDaoImpl().findByState(CampaignWrapper.State.running);
                    for (Campaign entity : entities) {
                        if (entity.getActions() != null) {
                            for (Action action : entity.getActions()) {
                                ActionWrapper actionWrapper = ActionWrapper.wrap(entity, action);
                                if (actionWrapper.isSensing() && actionWrapper.getInputType() != null && actionWrapper.getInputType() == type) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found)
                            break;
                    }
                    if (!found) {
                        CampaignService.forceStop(type);
                    }
                }
                sendCampaignCompletion();
            }
        });
        thread.setName("Check completed");
        thread.start();
    }

    private void sendCampaignCompletion() {
        List<Campaign> entities = new CampaignDaoImpl().findAllNotSetAsCompleted();
        if (entities != null && entities.size() > 0) {
            final Campaign campaign = entities.get(0);
            final CampaignWrapper wrapper = CampaignWrapper.wrap(campaign);
            if (wrapper.isStatusEnded() || wrapper.isStatusCompleted()) {
                SessionManager.getInstance().complete(campaign, new SessionManager.RequestCallback<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null && response.isSuccess()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean shouldArchive = wrapper.shouldArchiveAutomatically();
                                    if (wrapper.isStatusCompleted()) {
                                        wrapper.setState(CampaignWrapper.State.completedWithSuccess);
                                    } else {
                                        wrapper.setState(CampaignWrapper.State.completedWithFailure);
                                    }
                                    if (shouldArchive) {
                                        wrapper.archive();
                                    }
                                    wrapper.commit();
                                    sendCampaignCompletion();
                                }
                            }).start();

                        } else {
                            checkingCompleted = false;
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        checkingCompleted = false;
                    }
                });
            } else {
                checkingCompleted = false;
            }
        } else {
            checkingCompleted = false;
        }
    }

    private void uploadSensing() {
        if (!uploadingSensing) {
            uploadingSensing = true;
            nextSenseData(0);
        }
    }

    private void nextSenseData(final int index) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Pipeline.Type[] types = Pipeline.Type.values();
                if (index >= types.length) {
                    uploadingSensing = false;
                    return;
                }
                Pipeline.Type type = types[index];
                SessionManager.getInstance().sendSensingData(type, new SessionManager.RequestCallback<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        nextSenseData(index + 1);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, null, t);
                        nextSenseData(index + 1);
                    }
                });
            }
        });
        thread.setName("Sensing uploader");
        thread.start();
    }

    private void uploadUPSensing() {
        if (!uploadingUPSensing) {
            uploadingUPSensing = true;
            nextUPSenseData();
        }
    }

    private void nextUPSenseData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SessionManager.getInstance().sendUPSensingData(new SessionManager.RequestCallback<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        uploadingUPSensing = false;
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, null, t);
                        uploadingUPSensing = false;
                    }
                });
            }
        });
        thread.setName("Sensing uploader");
        thread.start();
    }

    // region Photos

    private void uploadPhotos() {
        if (!uploadingPhotos) {
            uploadingPhotos = true;
            nextPhoto();
        }
    }

    private void nextPhoto() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PhotoDaoImpl photoDao = new PhotoDaoImpl();
                Photo photo = photoDao.findToUpload();
                if (photo != null) {
                    doUploadPhoto(photo);
                } else {
                    uploadingPhotos = false;
                }
            }
        });
        thread.setName("Photos Uploader");
        thread.start();
    }

    private void doUploadPhoto(Photo photo) {
        doUploadPhoto(photo, null);
    }

    private void doUploadPhoto(final Photo photo, SessionManager.RequestCallback<Response> answerCallback) {
        StorageFilePhotoRequest request = PhotoWrapper.wrap(photo).getRequest();

        SessionManager.RequestCallback<Response> callback = new SessionManager.RequestCallback<Response>() {
            @Override
            public void onResponse(Response response) {
                try {
                    if (response != null && response.isSuccess()) {
                        Log.i(TAG, "doUploadPhoto uploaded");
                        photo.setUploaded(true);
                        new PhotoDaoImpl().commit(photo);
                        if (photo.getQuestionId() != null && photo.getQuestionId() > 0) {
                            Question question = new QuestionDaoImpl().find(photo.getQuestionId());
                            if (question != null) {
//                                ActionDaoImpl actionDao = new ActionDaoImpl();
//                                if (actionDao.isRepeatAndAllUploaded(question.getActionId())) {
//                                    actionDao.prepareToRepeat(question.getActionId());
//                                }
                            }
                        }
                        nextPhoto();
                    } else {
                        Log.e(TAG, "Error uploading campaign photo: " + response.getMessage());
                        uploadingPhotos = false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, null, e);
                    uploadingPhotos = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, null, t);
                uploadingPhotos = false;
            }
        };

        if (answerCallback != null) {
            callback = answerCallback;
        }

        SessionManager.getInstance().uploadFile(request, callback, new ProgressRequestBody.ProgressCallback() {
            @Override
            public void onProgress(int percentage) {
                App.getInstance().dispatchMessage(MessageType.DATA_UPLOADER_PHOTO_PROGRESS, new PhotoProgress(photo, percentage));
            }
        });
    }

    // endregion

    // region Questions

    private void uploadQuestions() {
        if (!uploadingQuestions) {
            uploadingQuestions = true;
            nextQuestion();
        }
    }

    private void uploadQuestionAnswers() {
        if (!uploadingQuestionAnswers) {
            uploadingQuestionAnswers = true;
            nextQuestionAnswer();
        }
    }

    private void nextQuestion() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                questionsUploading.clear();
                List<Campaign> campaigns = new CampaignDaoImpl().fetchWithoutUP();
                for (Campaign campaign : campaigns) {
                    if (campaign.getActions() != null) {
                        for (Action action : campaign.getActions()) {
                            if (ActionWrapper.wrap(campaign, action).isQuestion()) {
                                ActionQuestionnaire wrapper = ActionQuestionnaire.wrap(campaign, action);
                                List<Question> uploadItems = wrapper.getQuestionsToUpload();
                                for (Question question : uploadItems) {
                                    questionsUploading.add(question);
                                }
                            }
                        }
                    }
                }
                if (questionsUploading.size() > 0) {
                    doUploadQuestion(0);
                } else {
                    uploadingQuestions = false;
                }
            }
        });
        thread.setName("Questions Uploader");
        thread.start();
    }

    private void nextQuestionAnswer() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                questionAnswersUploading.clear();
                questionAnswersUploading = new QuestionAnswerDaoImpl().findToUpload();
                if (questionAnswersUploading.size() > 0) {
                    doUploadQuestionAnswer(0);
                } else {
                    uploadingQuestionAnswers = false;
                }
            }
        });
        thread.setName("Question Answers");
        thread.start();
    }

    private void doUploadQuestion(int index) {
        if (index < questionsUploading.size()) {
            doUploadQuestion(questionsUploading.get(index), index);
        } else {
            uploadingQuestions = false;
        }
    }

    private void doUploadQuestionAnswer(int index) {
        if (index < questionAnswersUploading.size()) {
            doUploadQuestionAnswer(questionAnswersUploading.get(index), index);
        } else {
            uploadingQuestionAnswers = false;
        }
    }

    private void doUploadQuestion(final Question question, final int index) {
        SessionManager.getInstance().sendQuestion(question, new SessionManager.RequestCallback<Response>() {
            @Override
            public void onResponse(Response response) {
                if (response != null && response.isSuccess()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            question.setUploaded(true);
                            new QuestionDaoImpl().update(question);
//                            ActionDaoImpl actionDao = new ActionDaoImpl();
//                            if (actionDao.isRepeatAndAllUploaded(question.getActionId())) {
//                                actionDao.prepareToRepeat(question.getActionId());
//                            }
                        }
                    }).start();
                }
                doUploadQuestion(index + 1);
            }

            @Override
            public void onFailure(Throwable t) {
                doUploadQuestion(index + 1);
            }
        });
    }

    private void doUploadQuestionAnswer(final QuestionAnswer question, final int index) {
        if (question.getPhoto() != null && question.getPhoto()) {
            Photo photo = PhotoWrapper.build(question);
            doUploadPhoto(photo, new SessionManager.RequestCallback<Response>() {
                @Override
                public void onResponse(Response response) {
                    try {
                        if (response != null && response.isSuccess()) {
                            new QuestionAnswerDaoImpl().remove(question);
                        }
                        doUploadQuestionAnswer(index + 1);
                    } catch (Exception e) {
                        Log.e(TAG, null, e);
                        uploadingQuestionAnswers = false;
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, null, t);
                    uploadingQuestionAnswers = false;
                }
            });
        } else {
            SessionManager.getInstance().sendQuestionAnswer(question, new SessionManager.RequestCallback<Response>() {
                @Override
                public void onResponse(Response response) {
                    if (response != null && response.isSuccess()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new QuestionAnswerDaoImpl().remove(question);
                            }
                        }).start();
                        doUploadQuestionAnswer(index + 1);
                    } else {
                        uploadingQuestionAnswers = false;
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    //doUploadQuestion(index + 1);
                    uploadingQuestionAnswers = false;
                }
            });
        }
    }

    // endregion

    // region Urban Problem files

    private void uploadUPFiles() {
        if (!uploadingUPFiles) {
            uploadingUPFiles = true;
            nextUPFile();
        }
    }

    private void nextUPFile() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final UPFileDaoImpl dao = new UPFileDaoImpl();
                final UPFileDB entity = dao.findToUpload();
                if (entity != null) {
                    File file = new File(entity.getFilePath());
                    if (!file.exists()) {
                        Log.e(TAG, "UPFile does not exist: " + entity.getFilePath());
                        dao.delete(entity);
                        nextUPFile();
                        return;
                    }
                    StorageFileRequest request = UPFileDBWrapper.wrap(entity).getRequest();
                    Log.i(TAG, "Uploading " + request.getClass().getSimpleName());
                    SessionManager.getInstance().uploadFile(request, new SessionManager.RequestCallback<Response>() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                if (response != null && response.isSuccess()) {
                                    Log.i(TAG, "UPFile uploaded");
                                    App.getInstance().dispatchMessage(MessageType.DATA_UPLOADER_UP_FILE_PROGRESS, new UPFileProgress(entity, 100));
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dao.setUploaded(entity);
                                            nextUPFile();
                                        }
                                    }).start();
                                } else {
                                    Log.e(TAG, "Error uploading UPFile: " + response.getMessage());
                                    uploadingUPFiles = false;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, null, e);
                                uploadingUPFiles = false;
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, null, t);
                            uploadingUPFiles = false;
                        }
                    }, new ProgressRequestBody.ProgressCallback() {
                        @Override
                        public void onProgress(int percentage) {
                            App.getInstance().dispatchMessage(MessageType.DATA_UPLOADER_UP_FILE_PROGRESS, new UPFileProgress(entity, percentage));
                        }
                    });
                } else {
                    uploadingUPFiles = false;
                }
            }
        });
        thread.setName("UPFiles Thread");
        thread.start();
    }

    public class PhotoProgress {
        Photo entity;
        int percentage;

        public PhotoProgress(Photo entity, int percentage) {
            this.entity = entity;
            this.percentage = percentage;
        }

        public int getPercentage() {
            return percentage;
        }
    }

    public class UPFileProgress {

        UPFileDB entity;
        int percentage;

        public UPFileProgress(UPFileDB entity, int percentage) {
            this.entity = entity;
            this.percentage = percentage;
        }

        public int getPercentage() {
            return percentage;
        }

        public String getType() {
            return entity.getType();
        }
    }

    // endregion

}
