package br.udesc.esag.participactbrasil.domain.rest;

/**
 * Created by fabiobergmann on 25/10/16.
 */

public class ProfileUpdateRestRequest extends RestRequest {

    private String name;
    private String surname;
    private String phone;
    private String currentAddress;
    private String currentNumber;
    private String currentCity;
    private String currentProvince;
    private String currentCountry;
    private String currentZipCode;
    private String password;
    private String gender;
    private String birthdate;

    public ProfileUpdateRestRequest(String name, String surname, String phone, String currentAddress, String currentNumber, String currentCity, String currentProvince, String currentCountry, String currentZipCode, String password, String gender, String birthday) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.currentAddress = currentAddress;
        this.currentNumber = currentNumber;
        this.currentCity = currentCity;
        this.currentProvince = currentProvince;
        this.currentCountry = currentCountry;
        this.currentZipCode = currentZipCode;
        this.password = password;
        this.gender = gender;
        this.birthdate = birthday;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getCurrentNumber() {
        return currentNumber;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public String getCurrentProvince() {
        return currentProvince;
    }

    public String getCurrentCountry() {
        return currentCountry;
    }

    public String getCurrentZipCode() {
        return currentZipCode;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }
}
