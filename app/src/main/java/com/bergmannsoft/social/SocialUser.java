package com.bergmannsoft.social;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fabiobergmann on 23/03/18.
 */

public class SocialUser {

    private String id;
    private String name;
    private String email;
    private String pictureUrl;
    private String accessToken;

    public static SocialUser withFacebook(JSONObject object) throws JSONException {
        SocialUser user = new SocialUser();
        user.name = object.getString("name");
        user.email = object.getString("email");
        user.id = object.getString("id");
        return user;
    }

    public static SocialUser withGoogle(GoogleSignInAccount account) {
        SocialUser user = new SocialUser();
        user.name = account.getDisplayName();
        user.email = account.getEmail();
        user.id = account.getId();
        user.pictureUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean hasProfilePicture() {
        return pictureUrl != null && pictureUrl.trim().length() > 0;
    }

}
