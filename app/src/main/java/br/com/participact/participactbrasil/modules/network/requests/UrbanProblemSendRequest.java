package br.com.participact.participactbrasil.modules.network.requests;

public class UrbanProblemSendRequest {

    Long subcategoryId;
    String comment;
    Float accuracy;
    Double latitude;
    Double longitude;
    Double altitude;
    Float verticalAccuracy;
    Double course;
    Float speed;
    Integer floor;
    String provider;
    Long timestamp;

    String userName;
    String userEmail;
    String city;
    String address;
    int filesCount;

    String gpsInfo;

    public UrbanProblemSendRequest(Long subcategoryId, String comment, double latitude, double longitude, String provider) {
        this.subcategoryId = subcategoryId;
        this.comment = comment;
        this.accuracy = 0.0f;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = 0.0;
        this.verticalAccuracy = 0.0f;
        this.course = 0.0;
        this.speed = 0.0f;
        this.floor = 0;
        this.provider = provider;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    public void setGpsInfo(String gpsInfo) {
        this.gpsInfo = gpsInfo;
    }
}
