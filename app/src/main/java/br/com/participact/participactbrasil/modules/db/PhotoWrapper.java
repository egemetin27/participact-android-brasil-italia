package br.com.participact.participactbrasil.modules.db;

import com.bergmannsoft.util.Utils;

import java.io.File;

import br.com.participact.participactbrasil.modules.network.requests.StorageFilePhotoRequest;
import br.com.participact.participactbrasil.modules.support.PA;

public class PhotoWrapper {

    private Photo photo;

    private PhotoWrapper(Photo photo) {
        this.photo = photo;
    }

    public static PhotoWrapper wrap(Photo photo) {
        return new PhotoWrapper(photo);
    }

    public static Photo build(QuestionAnswer question) {
        Photo photo = new Photo();
        photo.setFilename(question.getFilename());
        photo.setLatitude(question.getLatitude());
        photo.setLongitude(question.getLongitude());
        photo.setAnswerGroupId(question.getAnswerGroupId());
        photo.setIpAddress(question.getIpAddress());
        photo.setQuestionId(question.getQuestionId());
        photo.setCampaignId(question.getCampaignId());
        photo.setActionId(question.getActionId());
        photo.setProvider(question.getProvider());
        return photo;
    }

    public File getFile() {
        //File file = new File(PA.getPhotosDir(), photo.getFilename());
        File file = new File(photo.getFilename());
        return file;
    }

    public StorageFilePhotoRequest getRequest() {
        StorageFilePhotoRequest request = new StorageFilePhotoRequest(getFile(), photo.getCampaignId(), photo.getActionId(), photo.getQuestionId(), photo.getLatitude(), photo.getLongitude(), photo.getProvider(), photo.getIpAddress(), photo.getAnswerGroupId());
        return request;
    }

}
