package br.udesc.esag.participactbrasil.domain.persistence.support;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    //    private static final Class<?>[] classes = new Class[] {
//    	ActionFlat.class
//    };
    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_domain_config.txt");
    }
}