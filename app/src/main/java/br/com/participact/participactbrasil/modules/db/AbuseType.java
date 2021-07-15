package br.com.participact.participactbrasil.modules.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ABUSE_TYPE".
 */
import com.google.gson.annotations.SerializedName;
@Entity
public class AbuseType {

    @Id
    private Long id;
    @SerializedName("name")
    private String text;
    private Boolean selected;

    @Generated
    public AbuseType() {
    }

    public AbuseType(Long id) {
        this.id = id;
    }

    @Generated
    public AbuseType(Long id, String text, Boolean selected) {
        this.id = id;
        this.text = text;
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}