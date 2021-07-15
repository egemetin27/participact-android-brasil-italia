package br.com.participact.participactbrasil.modules.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;

import com.bergmannsoft.util.FileCache;
import com.bergmannsoft.util.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;

public class UrbanProblem {

    private Long id;
    private String comment;
    private Double latitude;
    private Double longitude;
    @SerializedName("creationDate")
    private String creationDateString;
    private UPSubcategory subcategory;
    private List<UPFile> files;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCreationDateString() {
        return creationDateString;
    }

    public void setCreationDateString(String creationDateString) {
        this.creationDateString = creationDateString;
    }

    public UPSubcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(UPSubcategory subcategory) {
        this.subcategory = subcategory;
    }

    public List<UPFile> getFiles() {
        return files;
    }

    public void setFiles(List<UPFile> files) {
        this.files = files;
    }

    public Bitmap pin(final FileCache.OnBitmapDownloadedListener listener) {
        if (subcategory != null && subcategory.getCategory() != null) {
            return subcategory.getCategory().pin(listener);
        }
        return BitmapFactory.decodeResource(App.getInstance().getResources(), R.mipmap.pin_outros);
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public String getDateFormatted() {
        Date dt = Utils.stringToDate(creationDateString, "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        if (dt != null) {
            String sdt = Utils.dateToString("dd/MM/yy", dt);
            if (sdt != null) {
                return sdt;
            }
        }
        return "";
    }

    public String getTimeFormatted() {
        Date dt = Utils.stringToDate(creationDateString, "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        if (dt != null) {
            String sdt = Utils.dateToString("HH:mm a", dt);
            if (sdt != null) {
                return sdt;
            }
        }
        return "";
    }

    public String getDateTimeFormatted() {
        Date dt = Utils.stringToDate(creationDateString, "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
        if (dt != null) {
            String sdt = Utils.dateToString("dd/MM/yy HH:mm a", dt);
            if (sdt != null) {
                return sdt;
            }
        }
        return "";
    }

    public boolean hasComments() {
        return comment != null && comment.trim().length() > 0;
    }

    public boolean hasAudio() {
        if (files != null) {
            for (UPFile file : files) {
                if (file.isAudio())
                    return true;
            }
        }
        return false;
    }

    public boolean hasVideo() {
        if (files != null) {
            for (UPFile file : files) {
                if (file.isVideo())
                    return true;
            }
        }
        return false;
    }

    public int photosCount() {
        int count = 0;
        if (files != null) {
            for (UPFile file : files) {
                if (file.isPhoto())
                    count++;
            }
        }
        return count;
    }


}
