package br.udesc.esag.participactbrasil.domain.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = -4920503490628523872L;

    public static final int RESULT_OK = 200;
    public static final int DATA_ALREADY_ON_SERVER = 555;
    public static final int DATA_NOT_REQUIRED = 556;

    private static final String KEY = "key";
    private static final String RESULT_CODE = "resultCode";

    Properties map;

    public ResponseMessage() {
        map = new Properties();
        map.setProperty(KEY, "");
        map.setProperty(RESULT_CODE, -1 + "");
    }

    public ResponseMessage(String key, int resultCode) {
        map = new Properties();
        map.put(KEY, key);
        map.put(RESULT_CODE, resultCode);
    }

    public String getProperty(String key) {
        return map.getProperty(key);
    }

    public void setProperty(String key, String value) {
        map.put(key, value);
    }

    public String getKey() {
        return map.getProperty(KEY);
    }

    public void setKey(String key) {
        map.put(KEY, key);
    }

    public int getResultCode() {
        return Integer.parseInt(map.getProperty(RESULT_CODE));
    }

    public void setResultCode(int resultCode) {
        map.setProperty(RESULT_CODE, resultCode + "");
    }


}
