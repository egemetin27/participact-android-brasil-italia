package br.udesc.esag.participactbrasil.domain.persistence;


public class LocationAwareTask extends Task {

    private static final long serialVersionUID = -4925173819979133102L;

    private Double latitude;

    private Double longitude;

    private Double radius;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public String toString() {
        String actions = "";
        for (Action act : getActions()) {
            actions += act.getName() + " ";
        }
        return String.format("%s Id:%s Name:%s DeadLine:%s Points:%s Latitude:%s Longitude:%s Actions:%s", Task.class.getSimpleName(), getId(), getName(), getDeadline(), getPoints(), getLatitude(), getLongitude(), actions);
    }

    public TaskFlat convertToTaskFlat() {
        return new TaskFlat(this);
    }
}
