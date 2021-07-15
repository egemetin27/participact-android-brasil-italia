package br.com.participact.participactbrasil.modules.support;

import android.graphics.Bitmap;

import com.bergmannsoft.util.FileCache;
import com.google.gson.annotations.SerializedName;

import br.com.participact.participactbrasil.modules.App;

public class UPFile {

    private Long id;
    @SerializedName("fileName")
    private String name;
    @SerializedName("assetUrl")
    private String url;
    private String fileExtension;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public boolean isAudio() {
        return "m4a".equalsIgnoreCase(fileExtension) || "mp3".equalsIgnoreCase(fileExtension);
    }

    public boolean isVideo() {
        return "mp4".equalsIgnoreCase(fileExtension);
    }

    public boolean isPhoto() {
        return "png".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension);
    }

    public String photoKey() {
        return "file_photo_key" + id;
    }

    public Bitmap photo(final FileCache.OnBitmapDownloadedListener listener) {
        Bitmap bmp = App.getInstance().getFileCache().getBitmap(photoKey());
        if (bmp == null) {
            App.getInstance().getFileCache().downloadAsync(photoKey(), url, new FileCache.FileCacheCallback() {
                @Override
                public void onDownloadDone(String key, Bitmap bmp) {
                    listener.onDownloaded(bmp);
                }
            });
        }
        return bmp;
    }

}
