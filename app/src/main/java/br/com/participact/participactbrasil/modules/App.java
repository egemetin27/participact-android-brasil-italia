package br.com.participact.participactbrasil.modules;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.bergmannsoft.application.BApplication;

import com.bergmannsoft.location.BSLocationListener;
import com.bergmannsoft.location.BSLocationManager;
import com.bergmannsoft.util.LocationUtils;
import com.bergmannsoft.util.UniqueID;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.Sensor;
import br.com.participact.participactbrasil.modules.db.UPSensor;
import br.com.participact.participactbrasil.modules.db.UPSensorDaoImpl;
import br.com.participact.participactbrasil.modules.support.UserSettings;
import io.fabric.sdk.android.Fabric;
import org.greenrobot.greendao.database.Database;
import org.most.MoSTApplication;
import org.most.input.FusionLocationInput;
import org.most.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.DaoMaster;
import br.com.participact.participactbrasil.modules.db.DaoSession;
import br.com.participact.participactbrasil.modules.db.UPFileDaoImpl;
import br.com.participact.participactbrasil.modules.support.DataUploader;
import br.com.participact.participactbrasil.modules.support.UPCategory;

public class App extends MoSTApplication {

    public static final boolean ENCRYPTED = false;

    private static App instance;
    private boolean closeCategories;
    private List<UPCategory> categories;
    private Long reportId;
    private UPCategory subcategory;
    private boolean hasReportSent;
    private UPCategory category;
    private String deviceUuid;

    public static App getInstance() {
        return instance;
    }

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        Fabric.with(this, new Crashlytics());
        instance = this;

        String test = "02:00:00:00:00:00".replaceAll(":", "");
        System.out.println(test);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "pa-db-encrypted" : "pa-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("9r3Q8Jk2J49NU9d3128736458J6s87K") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                DataUploader.getInstance().uploadAll();
                checkUrbanProblemsGPS();
            }
        },2000, 30000);

        MoSTApplication.getInstance();

//        getFileCache().clearCache();

//        List<Campaign> entities = daoSession.getCampaignDao().queryBuilder().build().list();
//        for (Campaign entity : entities) {
//            if (entity.getName().contains("Finaliza em pouco tempo 2")) {
//                entity.setRawState("AVAILABLE");
//                daoSession.getCampaignDao().update(entity);
//                break;
//            }
//        }

        CampaignWrapper.resumeAll();

        //GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(activity);

//        new UPFileDaoImpl().reuploadVideos();

        UserSettings.getInstance().setSendRegid(true);

        Crashlytics.setUserIdentifier(UserSettings.getInstance().getUsername() != null ? UserSettings.getInstance().getUsername() : "");
        Crashlytics.setUserEmail(UserSettings.getInstance().getUsername() != null ? UserSettings.getInstance().getUsername() : "");
        Crashlytics.setUserName(UserSettings.getInstance().getName() != null ? UserSettings.getInstance().getName() : "");

//        String city = LocationUtils.getCity(-27.593282, -48.543223);
//        Log.d(TAG, city);

    }

    public String getDeviceUuid() {
        if (deviceUuid == null) {
            deviceUuid = UniqueID.get(getBaseContext());
        }
        return deviceUuid;
    }

    public void setCloseCategories(boolean closeCategories) {
        this.closeCategories = closeCategories;
    }

    public boolean isCloseCategories() {
        return closeCategories;
    }

    public List<UPCategory> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    public void setCategories(List<UPCategory> categories) {
        getCategories().clear();
        for (UPCategory category : categories) {
            category.init();
            this.categories.add(category);
        }
    }

    public List<List<UPCategory>> getSubcategoriesForList(UPCategory category) {
        if (category == null)
            return new ArrayList<>();
        return getCategoriesForList(category.getSubcategories());
    }

    public List<List<UPCategory>> getCategoriesForList() {
        return getCategoriesForList(getCategories());
    }

    public List<List<UPCategory>> getCategoriesForList(List<UPCategory> categories) {
        List<List<UPCategory>> ccc = new ArrayList<>();
        int i = 0;
        int il = 0;
        for (UPCategory category : categories) {
            List<UPCategory> line = ccc.size() > il ? ccc.get(il) : null;
            if (line == null) {
                line = new ArrayList<>();
                ccc.add(line);
            }
            line.add(category);
            i++;
            if (i > 2) {
                il++;
                i = 0;
            }
        }
        return ccc;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void onReportSent(Long reportId, UPCategory category, UPCategory subcategory) {
        this.reportId = reportId;
        this.category = category;
        this.subcategory = subcategory;
        this.hasReportSent = true;
    }

    public Long getReportId() {
        return reportId;
    }

    public UPCategory getCategory() {
        return category;
    }

    public UPCategory getSubcategory() {
        return subcategory;
    }

    public boolean hasReportSent() {
        return hasReportSent;
    }

    public void setHasReportSent(boolean hasReportSent) {
        this.hasReportSent = hasReportSent;
    }


    private boolean gpsUrbanProblemsRunning = false;

    private void checkUrbanProblemsGPS() {
        if (!BSLocationManager.getInstance(this).hasPermission()) {
            gpsUrbanProblemsRunning = false;
        } else if (!UserSettings.getInstance().isUrbanProblemsGPSEnabled()) {
            if (gpsUrbanProblemsRunning) {
                gpsUrbanProblemsRunning = false;
                BSLocationManager.getInstance(this).removeListener(mBSLocationListener);
            }
        } else if (!gpsUrbanProblemsRunning) {
            gpsUrbanProblemsRunning = true;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BSLocationManager.getInstance(getApplicationContext()).requestLocationUpdates(mBSLocationListener);
                }
            });
        }
    }

    private BSLocationListener mBSLocationListener = new BSLocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Map<String, Object> map = new HashMap<>();
            map.put("latitude", location.getLatitude());
            map.put("longitude", location.getLongitude());
            map.put("altitude", location.getAltitude());
            map.put("horizontalAccuracy", location.getAccuracy());
            map.put("verticalAccuracy", location.getAccuracy());
            map.put("course", 0);
            map.put("speed", 0);
            map.put("floor", 0);
            map.put("provider", "GPS");
            map.put("timestamp", System.currentTimeMillis() / 1000);

            String data = new Gson().toJson(map);

            UPSensor sensor = new UPSensor();
            sensor.setPipelineTypeValue(Pipeline.Type.LOCATION.toInt());
            sensor.setUploaded(false);
            sensor.setData(data);
            new UPSensorDaoImpl().commit(sensor);
        }

        @Override
        public long getUpdateTimeInterval() {
            return UserSettings.getInstance().getUrbanProblemsGPSInterval();
        }
    };

}
