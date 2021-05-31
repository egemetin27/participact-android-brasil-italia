package br.udesc.esag.participactbrasil.domain.data;


public class DataPhoneCallEvent extends Data {

    private static final long serialVersionUID = -837842733892409270L;

    private boolean is_start;

    private boolean is_incoming;

    private String phone_number;

    public boolean isIs_start() {
        return is_start;
    }

    public void setIs_start(boolean is_start) {
        this.is_start = is_start;
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
