package br.udesc.esag.participactbrasil.domain.data;


public class DataMagneticField extends Data {

    private static final long serialVersionUID = -7734250913687773940L;

    private float magnetic_field_x;

    private float magnetic_field_y;

    private float magnetic_field_z;

    public float getMagnetic_field_x() {
        return magnetic_field_x;
    }

    public void setMagnetic_field_x(float magnetic_field_x) {
        this.magnetic_field_x = magnetic_field_x;
    }

    public float getMagnetic_field_y() {
        return magnetic_field_y;
    }

    public void setMagnetic_field_y(float magnetic_field_y) {
        this.magnetic_field_y = magnetic_field_y;
    }

    public float getMagnetic_field_z() {
        return magnetic_field_z;
    }

    public void setMagnetic_field_z(float magnetic_field_z) {
        this.magnetic_field_z = magnetic_field_z;
    }

}
