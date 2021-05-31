package br.udesc.esag.participactbrasil.domain.data;


public class DataAccelerometer extends Data {

    private static final long serialVersionUID = 7793293804630922508L;

    private float x;

    private float y;

    private float z;


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

}
