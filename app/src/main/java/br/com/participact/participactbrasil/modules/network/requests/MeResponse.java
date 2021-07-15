package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;

import br.com.participact.participactbrasil.modules.support.UserSettings;

public class MeResponse extends Response {

    class Item {
        Long id;
        String name;
        String phone;
        String photo;
        String gender;
        String facebookUrl;
        String googleUrl;
        String twitterUrl;
        String instagramUrl;
        String youtubeUrl;
        String ageRange;
        String emailSecondary;
    }

    Item item;

    public void save() {
        if (item != null) {
            UserSettings user = UserSettings.getInstance();
            user.setId(item.id);
            if (item.name != null && !item.name.contains("@participact.com.br")) {
                user.setName(item.name);
            }
            user.setEmail(item.emailSecondary);
            if (item.phone != null && item.phone.trim().length() > 0) {
                user.setPhone(item.phone);
            }
            user.setPictureUrl(item.photo);
            user.setAgeRange(item.ageRange);
            user.setFacebookUrl(item.facebookUrl);
            user.setGoogleUrl(item.googleUrl);
            user.setTwitterUrl(item.twitterUrl);
            user.setInstagramUrl(item.instagramUrl);
            user.setGender(item.gender != null ? item.gender.toLowerCase() : "male");
            user.setYoutubeUrl(item.youtubeUrl);
            user.setHasProfile(item.name != null && item.name.trim().length() > 0 && !item.name.contains("@participact.com.br"));
            user.setIdentified(
                    user.getName() != null && user.getName().trim().length() > 0 &&
                    user.getEmail() != null && user.getEmail().trim().length() > 0 &&
                    user.getPhone() != null && user.getPhone().trim().length() > 0);
        }
    }

}
