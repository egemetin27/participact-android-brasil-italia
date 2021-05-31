package br.udesc.esag.participactbrasil.domain.data;


public class DataGyroscope extends Data {

    private static final long serialVersionUID = 9078245648320258665L;

    private float rotation_x;

    private float rotation_y;

    private float rotation_z;

    public float getRotation_x() {
        return rotation_x;
    }

    public void setRotation_x(float rotation_x) {
        this.rotation_x = rotation_x;
    }

    public float getRotation_y() {
        return rotation_y;
    }

    public void setRotation_y(float rotation_y) {
        this.rotation_y = rotation_y;
    }

    public float getRotation_z() {
        return rotation_z;
    }

    public void setRotation_z(float rotation_z) {
        this.rotation_z = rotation_z;
    }

}
