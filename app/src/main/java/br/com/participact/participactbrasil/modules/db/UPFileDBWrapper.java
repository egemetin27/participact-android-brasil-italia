package br.com.participact.participactbrasil.modules.db;

import java.io.File;

import br.com.participact.participactbrasil.modules.network.requests.StorageFileAudioRequest;
import br.com.participact.participactbrasil.modules.network.requests.StorageFilePhotoRequest;
import br.com.participact.participactbrasil.modules.network.requests.StorageFileRequest;
import br.com.participact.participactbrasil.modules.network.requests.StorageFileVideoRequest;

public class UPFileDBWrapper {

    UPFileDB entity;

    private UPFileDBWrapper(UPFileDB entity) {
        this.entity = entity;
    }

    public static UPFileDBWrapper wrap(UPFileDB entity) {
        UPFileDBWrapper wrapper = new UPFileDBWrapper(entity);
        return wrapper;
    }

    public StorageFileRequest getRequest() {
        switch (entity.getType()) {
            case "audio":
                return new StorageFileAudioRequest(new File(entity.getFilePath()), entity.getReportId(), entity.getDuration());
            case "video":
                return new StorageFileVideoRequest(new File(entity.getFilePath()), entity.getReportId(), entity.getDuration());
            case "photo":
                return new StorageFilePhotoRequest(new File(entity.getFilePath()), entity.getReportId());
        }
        return null;
    }

}
