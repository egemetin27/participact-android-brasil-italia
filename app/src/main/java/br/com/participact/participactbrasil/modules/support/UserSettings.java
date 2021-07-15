package br.com.participact.participactbrasil.modules.support;

import android.graphics.Bitmap;

import com.bergmannsoft.util.FileCache;

import java.util.HashMap;

import br.com.participact.participactbrasil.modules.App;

public class UserSettings {

    private static UserSettings instance;

    private UserSettings() {

    }

    private final String id = "id";
    private final String accessToken = "access_token";
    private final String username = "username";
    private final String password = "password";
    private final String campaignsVersion = "campaigns_version";
    private final String campaignsVersionDeleted = "campaigns_version_deleted";
    private final String lastTimeNetworkUpdate = "last_time_network_update";
    private final String name = "name";
    private final String email = "email";
    private final String facebookAccessToken = "facebook_access_token";
    private final String googleAccessToken = "google_access_token";
    //private final String pictureFilePath = "picture_file_path";
    private final String pictureUrl = "picture_url";
    private final String about = "about";
    private final String isFirstLogin = "is_first_login";
    private final String education = "education";
//    private final String university = "university";
//    private final String course = "course";
//    private final String job = "job";
    private final String phone = "phone";
    private final String authorizeContact = "authorizeContact";
    private final String gender = "gender";
    private final String ageRange = "ageRange";
    private final String facebookUrl = "facebookUrl";
    private final String googleUrl = "googleUrl";
    private final String twitterUrl = "twitterUrl";
    private final String instagramUrl = "instagramUrl";
    private final String youtubeUrl = "youtubeUrl";
    private final String identified = "identified";
    private final String hasProfile = "hasProfile";
    private final String sendDataOnlyOverWifi = "sendDataOnlyOverWifi";
    private final String showedHoldMapHelp = "showedHoldMapHelp";
    private final String skipTutorial = "skipTutorial";
    private final String regid = "regid";
    private final String sendRegid = "sendRegid";
    private final String urbanProblemsGPSInterval = "urbanProblemsGPSInterval";
    private final String urbanProblemsGPSEnabled = "urbanProblemsGPSEnabled";
    private final String urbanProblemsRangeDays = "urbanProblemsRangeDays";

    public String[] keys = new String[] {
            id,
            campaignsVersion,
            campaignsVersionDeleted,
            lastTimeNetworkUpdate,
            name,
            email,
            facebookAccessToken,
            googleAccessToken,
            pictureUrl,
            about,
            isFirstLogin,
            education,
//            university,
//            course,
//            job,
            phone,
            authorizeContact,
            gender,
            ageRange,
            facebookUrl,
            googleUrl,
            twitterUrl,
            instagramUrl,
            youtubeUrl,
            identified,
            hasProfile,
            showedHoldMapHelp,
            urbanProblemsGPSInterval,
            urbanProblemsGPSEnabled,
            urbanProblemsRangeDays
    };

    public static UserSettings getInstance() {
        if (instance == null) {
            instance = new UserSettings();
        }
        return instance;
    }

    public Long getId() {
        return App.getInstance().getPreferenceLong(id, 0);
    }

    public void setId(Long id) {
        App.getInstance().savePreferenceLong(this.id, id);
    }

    public String getAccessToken() {
        return App.getInstance().getPreferenceString(accessToken, null);
    }

    public void setAccessToken(String accessToken) {
        App.getInstance().savePreferenceString(this.accessToken, accessToken);
    }

    public String getUsername() {
        return App.getInstance().getPreferenceString(username, null);
    }

    public void setUsername(String username) {
        App.getInstance().savePreferenceString(this.username, username);
    }

    public String getPassword() {
        return App.getInstance().getPreferenceString(password, null);
    }

    public void setPassword(String password) {
        App.getInstance().savePreferenceString(this.password, password);
    }

    public String getCampaignsVersion() {
        return App.getInstance().getPreferenceString(campaignsVersion, null);
    }

    public String getCampaignsVersionDeleted() {
        return App.getInstance().getPreferenceString(campaignsVersionDeleted, null);
    }

    public void setCampaignsVersion(String campaignsVersion) {
        App.getInstance().savePreferenceString(this.campaignsVersion, campaignsVersion);
    }

    public void setCampaignsVersionDeleted(String campaignsVersionDeleted) {
        App.getInstance().savePreferenceString(this.campaignsVersionDeleted, campaignsVersionDeleted);
    }

//    public String getLastTimeNetworkUpdate() {
//        return App.getInstance().getPreferenceString(accessToken, null);lastTimeNetworkUpdate;
//    }
//
//    public void setLastTimeNetworkUpdate(String lastTimeNetworkUpdate) {
//        this.lastTimeNetworkUpdate = lastTimeNetworkUpdate;
//    }

    public String getName() {
        return App.getInstance().getPreferenceString(name, null);
    }

    public void setName(String name) {
        App.getInstance().savePreferenceString(this.name, name);
    }

    public String getEmail() {
        return App.getInstance().getPreferenceString(email, null);
    }

    public void setEmail(String email) {
        App.getInstance().savePreferenceString(this.email, email);
    }

    public String getFacebookAccessToken() {
        return App.getInstance().getPreferenceString(facebookAccessToken, null);
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        App.getInstance().savePreferenceString(this.facebookAccessToken, facebookAccessToken);
    }

    public String getGoogleAccessToken() {
        return App.getInstance().getPreferenceString(googleAccessToken, null);
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        App.getInstance().savePreferenceString(this.googleAccessToken, googleAccessToken);
    }

    public String getPictureUrl() {
        return App.getInstance().getPreferenceString(pictureUrl, null);
    }

    public void setPictureUrl(String pictureUrl) {
        App.getInstance().savePreferenceString(this.pictureUrl, pictureUrl);
    }

    public String getAbout() {
        return App.getInstance().getPreferenceString(about, null);
    }

    public void setAbout(String about) {
        App.getInstance().savePreferenceString(this.about, about);
    }

    public Boolean getIsFirstLogin() {
        return App.getInstance().getPreferenceBoolean(isFirstLogin, false);
    }

    public void setIsFirstLogin(Boolean isFirstLogin) {
        App.getInstance().savePreferenceBoolean(this.isFirstLogin, isFirstLogin);
    }

    public String getEducation() {
        return App.getInstance().getPreferenceString(education, null);
    }

    public void setEducation(String education) {
        App.getInstance().savePreferenceString(this.education, education);
    }

//    public String getCourse() {
//        return App.getInstance().getPreferenceString(course, null);
//    }
//
//    public void setCourse(String course) {
//        App.getInstance().savePreferenceString(this.course, course);
//    }
//
//    public String getJob() {
//        return App.getInstance().getPreferenceString(job, null);
//    }
//
//    public void setJob(String job) {
//        App.getInstance().savePreferenceString(this.job, job);
//    }

    public String getPhone() {
        return App.getInstance().getPreferenceString(phone, null);
    }

    public void setPhone(String phone) {
        App.getInstance().savePreferenceString(this.phone, phone);
    }

    public Boolean getAuthorizeContact() {
        return App.getInstance().getPreferenceBoolean(authorizeContact, true);
    }

    public void setAuthorizeContact(Boolean authorizeContact) {
        App.getInstance().savePreferenceBoolean(this.authorizeContact, authorizeContact);
    }

    public String getGender() {
        return App.getInstance().getPreferenceString(gender, null);
    }

    public void setGender(String gender) {
        App.getInstance().savePreferenceString(this.gender, gender);
    }

    public String getAgeRange() {
        return App.getInstance().getPreferenceString(ageRange, null);
    }

    public void setAgeRange(String ageRange) {
        App.getInstance().savePreferenceString(this.ageRange, ageRange);
    }

    public String getFacebookUrl() {
        return App.getInstance().getPreferenceString(facebookUrl, null);
    }

    public void setFacebookUrl(String facebookUrl) {
        App.getInstance().savePreferenceString(this.facebookUrl, facebookUrl);
    }

    public String getGoogleUrl() {
        return App.getInstance().getPreferenceString(googleUrl, null);
    }

    public void setGoogleUrl(String googleUrl) {
        App.getInstance().savePreferenceString(this.googleUrl, googleUrl);
    }

    public String getTwitterUrl() {
        return App.getInstance().getPreferenceString(twitterUrl, null);
    }

    public void setTwitterUrl(String twitterUrl) {
        App.getInstance().savePreferenceString(this.twitterUrl, twitterUrl);
    }

    public String getInstagramUrl() {
        return App.getInstance().getPreferenceString(instagramUrl, null);
    }

    public void setInstagramUrl(String instagramUrl) {
        App.getInstance().savePreferenceString(this.instagramUrl, instagramUrl);
    }

    public String getYoutubeUrl() {
        return App.getInstance().getPreferenceString(youtubeUrl, null);
    }

    public void setYoutubeUrl(String youtubeUrl) {
        App.getInstance().savePreferenceString(this.youtubeUrl, youtubeUrl);
    }

    public Boolean isIdentified() {
        return App.getInstance().getPreferenceBoolean(identified, false) || getFacebookAccessToken() != null || getGoogleAccessToken() != null;
    }

    public void setIdentified(Boolean identified) {
        App.getInstance().savePreferenceBoolean(this.identified, identified);
    }

    public Boolean getHasProfile() {
        return App.getInstance().getPreferenceBoolean(hasProfile, false);
    }

    public void setHasProfile(Boolean hasProfile) {
        App.getInstance().savePreferenceBoolean(this.hasProfile, hasProfile);
    }

    public Boolean getSendDataOnlyOverWifi() {
        return App.getInstance().getPreferenceBoolean(sendDataOnlyOverWifi, false);
    }

    public void setSendDataOnlyOverWifi(Boolean sendDataOnlyOverWifi) {
        App.getInstance().savePreferenceBoolean(this.sendDataOnlyOverWifi, sendDataOnlyOverWifi);
    }

    public Boolean getShowedHoldMapHelp() {
        return App.getInstance().getPreferenceBoolean(showedHoldMapHelp, false);
    }

    public void setShowedHoldMapHelp(Boolean showedHoldMapHelp) {
        App.getInstance().savePreferenceBoolean(this.showedHoldMapHelp, showedHoldMapHelp);
    }

    public Boolean getSkipTutorial() {
        return App.getInstance().getPreferenceBoolean(skipTutorial, false);
    }

    public void setSkipTutorial(Boolean skipTutorial) {
        App.getInstance().savePreferenceBoolean(this.skipTutorial, skipTutorial);
    }

    public void setRegid(String regid) {
        App.getInstance().savePreferenceString(this.regid, regid);
    }

    public String getRegid() {
        return App.getInstance().getPreferenceString(regid, null);
    }

    public void setSendRegid(Boolean sendRegid) {
        App.getInstance().savePreferenceBoolean(this.sendRegid, sendRegid);
    }

    public Boolean getSendRegId() {
        return getRegid() != null && App.getInstance().getPreferenceBoolean(sendRegid, false);
    }

    public long getUrbanProblemsGPSInterval() {
        return Math.max(App.getInstance().getPreferenceLong(urbanProblemsGPSInterval, 60000*6), 60000);
    }

    public boolean isUrbanProblemsGPSEnabled() {
        return App.getInstance().getPreferenceBoolean(urbanProblemsGPSEnabled, true);
    }

    public void setUrbanProblemsGPSInterval(long interval) {
        App.getInstance().savePreferenceLong(urbanProblemsGPSInterval, interval);
    }

    public void setUrbanProblemsGPSEnabled(boolean enabled) {
        App.getInstance().savePreferenceBoolean(urbanProblemsGPSEnabled, enabled);
    }

    public int getUrbanProblemsRangeDays() {
        return App.getInstance().getPreferenceInt(urbanProblemsRangeDays, 30);
    }

    public void setUrbanProblemsRangeDays(int days) {
        App.getInstance().savePreferenceInt(urbanProblemsRangeDays, days);
    }

    public Bitmap picture(final FileCache.OnBitmapDownloadedListener listener) {
        Bitmap bmp = App.getInstance().getFileCache().getBitmap("profile_image");
        if (bmp == null) {
            App.getInstance().getFileCache().downloadAsync("profile_image", getPictureUrl(), new FileCache.FileCacheCallback() {
                @Override
                public void onDownloadDone(String key, Bitmap bmp) {
                    listener.onDownloaded(bmp);
                }
            });
        }
        return bmp;
    }

    public void clear() {
        for (String key : keys) {
            App.getInstance().removePreference(key);
        }
    }

    public boolean hasNameAndEmail() {
        return getName() != null && getName().trim().length() > 0 && getEmail() != null && getEmail().trim().length() > 0;
    }
}
