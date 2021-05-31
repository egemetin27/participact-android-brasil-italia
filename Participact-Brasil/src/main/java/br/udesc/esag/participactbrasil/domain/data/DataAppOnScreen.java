package br.udesc.esag.participactbrasil.domain.data;

public class DataAppOnScreen extends Data {

    private static final long serialVersionUID = 2447327791041493962L;

    private String appName;

    private long startTime;

    private long endTime;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

}
