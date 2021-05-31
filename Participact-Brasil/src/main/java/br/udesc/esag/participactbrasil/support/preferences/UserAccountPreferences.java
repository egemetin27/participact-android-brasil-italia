/** UserAccountPreferences.java
 *  author: Andrea Cirri
 *  mail : andreacirri@gmail.com
 */
package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;

public class UserAccountPreferences {


    private static UserAccountPreferences instance;
    private static SharedPreferences sharedPreferences;

    private static int MODE = Context.MODE_PRIVATE;
    private static final String FILENAME = "USER_ACCOUNT";
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "password";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String ADDRESS_NUMBER = "addressNumber";
    private static final String ADDRESS_CITY = "addressCity";
    private static final String ADDRESS_PROVINCE = "addressProvince";
    private static final String ADDRESS_COUNTRY = "addressCountry";
    private static final String ZIPCODE = "zipcode";
    private static final String PHOTO = "photo";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String GENDER = "gender";
    private static final String BIRTHDAY = "birthday";
    private static final String REGISTRATION_ID = "registrationId";
    private static final String IS_SET = "isSet";
    private static final String IS_REGISTRATION_ID_SET_ON_SERVER = "isSetOnServer";
    private static final String ON_SERVER_EXPIRATION_TIME = "gcmOnServerExpirationTime";


    public static synchronized UserAccountPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new UserAccountPreferences();
            sharedPreferences = context.getSharedPreferences(FILENAME, MODE);
        }
        return instance;
    }

    public void saveUserAccount(UserAccount u) {
        Editor e = sharedPreferences.edit();
        e.putString(USERNAME, u.getUsername());
        if (u.getPassword() != null && u.getPassword().length() > 0) {
            e.putString(PASSWORD, u.getPassword());
        }
        e.putString(REGISTRATION_ID, u.getRegistrationId());
        e.putString(PHONE, u.getPhone());
        e.putString(ADDRESS, u.getAddress());
        e.putString(ADDRESS_NUMBER, u.getAddressNumber());
        e.putString(ADDRESS_CITY, u.getAddressCity());
        e.putString(ADDRESS_PROVINCE, u.getAddressProvince());
        e.putString(ADDRESS_COUNTRY, u.getAddressCountry());
        e.putString(ZIPCODE, u.getZipCode());
        e.putString(PHOTO, u.getPhoto());
        e.putString(NAME, u.getName());
        e.putString(SURNAME, u.getSurname());
        e.putString(GENDER, u.getGender());
        e.putString(BIRTHDAY, Utils.dateToString("yyyy-MM-dd", u.getBirthday()));
        e.putBoolean(IS_SET, true);
        e.apply();
    }

    public void updateUserAccount(UserAccount u) {
        Editor e = sharedPreferences.edit();
        e.putString(USERNAME, u.getUsername());
        if (u.getPassword() != null && u.getPassword().length() > 0) {
            e.putString(PASSWORD, u.getPassword());
        }
        e.putString(REGISTRATION_ID, u.getRegistrationId());
        e.putString(PHONE, u.getPhone());
        e.putString(ADDRESS, u.getAddress());
        e.putString(ADDRESS_NUMBER, u.getAddressNumber());
        e.putString(ADDRESS_CITY, u.getAddressCity());
        e.putString(ADDRESS_PROVINCE, u.getAddressProvince());
        e.putString(ADDRESS_COUNTRY, u.getAddressCountry());
        e.putString(ZIPCODE, u.getZipCode());
        e.putString(PHOTO, u.getPhoto());
        e.putString(NAME, u.getName());
        e.putString(SURNAME, u.getSurname());
        e.putString(GENDER, u.getGender());
        e.putString(BIRTHDAY, Utils.dateToString("yyyy-MM-dd", u.getBirthday()));
        e.putBoolean(IS_SET, true);
        e.apply();
    }

    public void deleteUserAccount() {
        Editor e = sharedPreferences.edit();
        e.remove(USERNAME);
        e.remove(PASSWORD);
        e.remove(PHONE);
        e.remove(PHOTO);
        e.remove(NAME);
        e.remove(SURNAME);
        e.remove(GENDER);
        e.remove(BIRTHDAY);
        e.remove(ADDRESS);
        e.remove(ADDRESS_NUMBER);
        e.remove(ADDRESS_CITY);
        e.remove(ADDRESS_PROVINCE);
        e.remove(ADDRESS_COUNTRY);
        e.remove(ZIPCODE);
        e.remove(REGISTRATION_ID);
        e.remove(IS_SET);
        e.remove(IS_REGISTRATION_ID_SET_ON_SERVER);
        e.apply();
    }

    public boolean isUserAccountValid() {
        return sharedPreferences.getBoolean(IS_SET, false);
    }

    public boolean isGCMSetOnServer() {
        return sharedPreferences.getBoolean(IS_REGISTRATION_ID_SET_ON_SERVER, false);
    }

    public void setGCMSetOnServer(boolean isSet) {
        Editor e = sharedPreferences.edit();
        e.putBoolean(IS_REGISTRATION_ID_SET_ON_SERVER, isSet);
        e.apply();
    }

    public void setGcmOnServerExpirationTime(long expirationTime) {
        Editor e = sharedPreferences.edit();
        e.putLong(ON_SERVER_EXPIRATION_TIME, expirationTime);
        e.apply();
    }

    public long getgcmOnServerExpirationTime() {
        return sharedPreferences.getLong(ON_SERVER_EXPIRATION_TIME, -1);
    }

    public UserAccount getUserAccount() {
        UserAccount u = new UserAccount();
        u.setUsername(sharedPreferences.getString(USERNAME, "notRegistered"));
        u.setPassword(sharedPreferences.getString(PASSWORD, "noPassword"));
        u.setPhone(sharedPreferences.getString(PHONE, ""));
        u.setAddress(sharedPreferences.getString(ADDRESS, ""));
        u.setAddressNumber(sharedPreferences.getString(ADDRESS_NUMBER, ""));
        u.setAddressCity(sharedPreferences.getString(ADDRESS_CITY, ""));
        u.setAddressProvince(sharedPreferences.getString(ADDRESS_PROVINCE, ""));
        u.setAddressCountry(sharedPreferences.getString(ADDRESS_COUNTRY, ""));
        u.setZipCode(sharedPreferences.getString(ZIPCODE, ""));
        u.setPhoto(sharedPreferences.getString(PHOTO, ""));
        u.setRegistrationId(sharedPreferences.getString(REGISTRATION_ID, ""));
        u.setName(sharedPreferences.getString(NAME, ""));
        u.setSurname(sharedPreferences.getString(SURNAME, ""));
        u.setGender(sharedPreferences.getString(GENDER, "NONE"));
        u.setBirthday(Utils.stringToDate(sharedPreferences.getString(BIRTHDAY, ""), "yyyy-MM-dd"));
        return u;
    }
}
