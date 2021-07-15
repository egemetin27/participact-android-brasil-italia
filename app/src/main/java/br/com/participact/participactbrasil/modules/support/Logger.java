package br.com.participact.participactbrasil.modules.support;

import android.content.pm.PackageInfo;
import android.os.Build;

import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Log;
import br.com.participact.participactbrasil.modules.db.LogDaoImpl;

public class Logger {

    private String tag;
    private LogDaoImpl dao = new LogDaoImpl();

    private Logger(String tag) {
        this.tag = tag;
    }

    public static Logger getLogger(Class clazz) {
        return new Logger(clazz.getSimpleName());
    }

    public void info(String message) {
        log("INFO", message, null);
    }

    public void error(String message, Throwable t) {
        log("ERROR", message, t);
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(Throwable t) {
        error(null, t);
    }

    private void log(String type, String message, Throwable t) {
        String msg = String.format("%s|%s|%s|%s", tag, type, message != null ? message.replaceAll("\\|", "") : "", t != null ? t.toString() : "");
        String user = defaultData() + "," + UserSettings.getInstance().getEmail() + "," + UserSettings.getInstance().getName();
        final Log log = new Log();
        log.setMessage(msg);
        log.setUser(user);
        dao.save(log);
    }

    public String defaultData() {

        int versionCode = -1;
        String versionName = "";

        try {
            PackageInfo packageInfo = App.getInstance().getPackageManager().getPackageInfo(App.getInstance().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Android," + String.valueOf(Build.VERSION.SDK_INT) + "," + Build.MANUFACTURER + "," + Build.MODEL + "," + versionName + "," + String.valueOf(versionCode) + "," + App.getInstance().getDeviceUuid() + ",PA";

    }

}
