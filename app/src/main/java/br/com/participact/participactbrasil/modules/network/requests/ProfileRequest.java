package br.com.participact.participactbrasil.modules.network.requests;

public class ProfileRequest {

    /*
    "name" : name,
            "about" : about,
            "education" : education,
            "email" : email,
            "phone" : phone,
            "authorizeContact" : authorizeContact,
            "gender" : gender,
            "ageRange" : ageRange,
            "facebookUrl" : facebookUrl,
            "googleUrl" : googleUrl,
            "twitterUrl" : twitterUrl,
            "instagramUrl" : instagramUrl,
            "youtubeUrl" : youtubeUrl
     */

    String name;
    String about;
    String email;
    String eduration;
    String phone;
    Boolean authorizeContact;
    String gender;
    String ageRange;
    String facebookUrl;
    String googleUrl;
    String twitterUrl;
    String instagramUrl;
    String youtubeUrl;

    public ProfileRequest(String name, String about, String email, String eduration, String phone, Boolean authorizeContact, String gender, String ageRange, String facebookUrl, String googleUrl, String twitterUrl, String instagramUrl, String youtubeUrl) {
        this.name = name;
        this.about = about;
        this.email = email;
        this.eduration = eduration;
        this.phone = phone;
        this.authorizeContact = authorizeContact;
        this.gender = gender;
        this.ageRange = ageRange;
        this.facebookUrl = facebookUrl;
        this.googleUrl = googleUrl;
        this.twitterUrl = twitterUrl;
        this.instagramUrl = instagramUrl;
        this.youtubeUrl = youtubeUrl;
    }
}
