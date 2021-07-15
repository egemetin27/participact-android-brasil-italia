package br.com.participact.participactbrasil.modules.network.requests.sensing;

public abstract class SensingData {

    public static final String TAG = SensingData.class.getSimpleName();

    Long timestamp;

    public SensingData() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public abstract boolean isValid();

}
