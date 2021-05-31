package br.udesc.esag.participactbrasil.domain.data;


public class DataCell extends Data {

    private static final long serialVersionUID = -7540541783183107870L;

    private String phone_type;

    private int gsm_cell_id;

    private int gsm_lac;

    private int base_station_id;

    private int base_station_latitude;

    private int base_station_longitude;

    private int base_network_id;

    private int base_system_id;

    public String getPhone_type() {
        return phone_type;
    }

    public void setPhone_type(String phone_type) {
        this.phone_type = phone_type;
    }

    public int getGsm_cell_id() {
        return gsm_cell_id;
    }

    public void setGsm_cell_id(int gsm_cell_id) {
        this.gsm_cell_id = gsm_cell_id;
    }

    public int getGsm_lac() {
        return gsm_lac;
    }

    public void setGsm_lac(int gsm_lac) {
        this.gsm_lac = gsm_lac;
    }

    public int getBase_station_id() {
        return base_station_id;
    }

    public void setBase_station_id(int base_station_id) {
        this.base_station_id = base_station_id;
    }

    public int getBase_station_latitude() {
        return base_station_latitude;
    }

    public void setBase_station_latitude(int base_station_latitude) {
        this.base_station_latitude = base_station_latitude;
    }

    public int getBase_station_longitude() {
        return base_station_longitude;
    }

    public void setBase_station_longitude(int base_station_longitude) {
        this.base_station_longitude = base_station_longitude;
    }

    public int getBase_network_id() {
        return base_network_id;
    }

    public void setBase_network_id(int base_network_id) {
        this.base_network_id = base_network_id;
    }

    public int getBase_system_id() {
        return base_system_id;
    }

    public void setBase_system_id(int base_system_id) {
        this.base_system_id = base_system_id;
    }

}
