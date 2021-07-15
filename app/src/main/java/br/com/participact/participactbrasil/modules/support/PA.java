package br.com.participact.participactbrasil.modules.support;

import com.bergmannsoft.util.Utils;

import java.io.File;

public class PA {

    private static PAProfile profile;

    public static PAProfile getProfile() {
        if (profile == null) {
            profile = new PAProfile();
        }
        return profile;
    }

    public static File getPhotosDir() {
        String dirString = Utils.getExternalStorageAbsolutePath() + "/participact/photos/";
        File dir = new File(dirString);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

}
