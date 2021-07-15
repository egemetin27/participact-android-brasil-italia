package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;

public class IPDataResponse extends Response {

    String ip;
    String loc;

    public String getIp() {
        return ip;
    }

    public Double getLatitude() {
        if (loc != null && loc.contains(",")) {
            try {
                return Double.parseDouble(loc.split(",")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public Double getLongitude() {
        if (loc != null && loc.contains(",")) {
            try {
                return Double.parseDouble(loc.split(",")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

}
