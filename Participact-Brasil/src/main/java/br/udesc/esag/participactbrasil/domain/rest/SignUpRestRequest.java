package br.udesc.esag.participactbrasil.domain.rest;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public class SignUpRestRequest extends RestRequest {

    private String ageRange;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String device;
    private String city;
    private String country;
    private String photo;
    private String social;

    public SignUpRestRequest(String name, String surname, String email, String password, String device, String city, String country, String photo, String social, String ageRange) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.device = device;
        this.city = city;
        this.country = country;
        this.photo = photo;
        this.social = social;
        this.ageRange = ageRange;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDevice() {
        return device;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoto() {
        return photo;
    }

    public String getSocial() {
        return social;
    }

    public String getAgeRange() {
        return ageRange;
    }
}
