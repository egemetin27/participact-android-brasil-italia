package br.udesc.esag.participactbrasil.domain.local;

import java.util.Date;

import br.com.bergmannsoft.util.Utils;

public class UserAccount {

    private String username;
    private String registrationId;
    private String password;
    private String phone;
    private String address;
    private String addressNumber;
    private String addressCity;
    private String addressProvince;
    private String addressCountry;
    private String zipCode;
    private String photo;
    private String name;
    private String surname;
    private String gender;
    private Date birthday;

    public UserAccount() {
    }

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        if (zipCode == null)
            return "";
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasCompletedData() {
        return Utils.isValidNotEmpty(name) && Utils.isValidNotEmpty(address) && Utils.isValidNotEmpty(phone);
    }

    public String getPhotoKey() {
        return username.replace("@", "").replace(".", "").replace("-", "");
    }

    public boolean hasPhoto() {
        return photo != null && photo.trim().length() > 0;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBirthdayServer() {
        if (birthday != null) {
            return Utils.dateToString("yyyy-MM-dd", birthday);
        }
        return "";
    }

    public String getFullname() {
        String str = name;
        if (Utils.isValidNotEmpty(surname)) {
            str = String.format("%s %s", name, surname);
        }
        return str;
    }

    public String getAddress1() {
        String str = address;
        if (Utils.isValidNotEmpty(addressNumber)) {
            str = String.format("%s, %s", address, addressNumber);
        }
        return str;
    }

    public String getAddress2() {
        String str = addressCity;
        if (Utils.isValidNotEmpty(zipCode)) {
            str = String.format("%s - %s", addressCity, zipCode);
        }
        return str;
    }
}
