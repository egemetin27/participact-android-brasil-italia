package br.udesc.esag.participactbrasil.domain.rest;

import br.udesc.esag.participactbrasil.domain.rest.base.RestResult;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class MeRestResult extends RestResult {

    String name;
    String surname;
    String phone;
    String address;
    String photo;
    String currentZipCode;
    String currentNumber;
    String currentCity;
    String currentCountry;
    String currentProvince;
    String gender;
    String birthdate;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCurrentZipCode() {
        return currentZipCode;
    }

    public String getPhoto() {
        return photo;
    }

    public String getSurname() {
        return surname;
    }

    public String getCurrentNumber() {
        return currentNumber;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public String getCurrentCountry() {
        return currentCountry;
    }

    public String getCurrentProvince() {
        return currentProvince;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }
}
