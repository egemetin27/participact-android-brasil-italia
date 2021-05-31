package br.udesc.esag.participactbrasil.domain.data;


public class DataInstalledApps extends Data {

    private static final long serialVersionUID = 6986458996220319344L;

    private String pkg_name;

    private float version_code;

    private String version_name;

    private String requested_permissions;

    public String getPkg_name() {
        return pkg_name;
    }

    public void setPkg_name(String pkg_name) {
        this.pkg_name = pkg_name;
    }

    public float getVersion_code() {
        return version_code;
    }

    public void setVersion_code(float version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getRequested_permissions() {
        return requested_permissions;
    }

    public void setRequested_permissions(String requested_permissions) {
        this.requested_permissions = requested_permissions;
    }

}
