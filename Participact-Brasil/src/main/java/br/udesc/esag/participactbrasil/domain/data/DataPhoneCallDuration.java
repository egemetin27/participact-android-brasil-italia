package br.udesc.esag.participactbrasil.domain.data;


public class DataPhoneCallDuration extends Data {

    private static final long serialVersionUID = 4562522041518010186L;

    private long call_start;

    private long call_end;

    private boolean is_incoming;

    private String phone_number;

    public long getCall_start() {
        return call_start;
    }

    public void setCall_start(long call_start) {
        this.call_start = call_start;
    }

    public long getCall_end() {
        return call_end;
    }

    public void setCall_end(long call_end) {
        this.call_end = call_end;
    }

    public boolean isIs_incoming() {
        return is_incoming;
    }

    public void setIs_incoming(boolean is_incoming) {
        this.is_incoming = is_incoming;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

}
