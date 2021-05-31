package br.udesc.esag.participactbrasil;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import br.com.bergmannsoft.application.BApplication;
import br.com.bergmannsoft.application.BUncaughtExceptionHandler;
import io.fabric.sdk.android.Fabric;
import org.most.MoSTApplication;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import br.udesc.esag.participactbrasil.broadcastreceivers.AlarmBroadcastReceiver;
import br.udesc.esag.participactbrasil.support.UploadAlarm;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.StatusPrinter;

public class ParticipActApplication extends BApplication {

    Map<Long, AlarmBroadcastReceiver> alarmBR;

    private static boolean isUploadingPhoto;

    private static ParticipActApplication instance;

    public static ParticipActApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configureLogback();
        configureImageLoader();

        UploadAlarm.getInstance(this).start();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Fabric.with(this, new Crashlytics());

        Thread.setDefaultUncaughtExceptionHandler(new BUncaughtExceptionHandler());
    }

    private void configureImageLoader() {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

    public ParticipActApplication() {
        super();
        alarmBR = new HashMap<Long, AlarmBroadcastReceiver>();
    }

    public Map<Long, AlarmBroadcastReceiver> getAlarmBR() {
        return alarmBR;
    }

    public void setAlarmBR(Map<Long, AlarmBroadcastReceiver> alarmBR) {
        this.alarmBR = alarmBR;
    }

    private void configureLogback() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        //context.reset();

        final String PA_LOG_DIR = getExternalFilesDir(null).getAbsolutePath();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(context);

        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.
        rollingFileAppender.setFile(PA_LOG_DIR + "/pa.log");

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setFileNamePattern(PA_LOG_DIR + "/pa.%i.log");
        rollingPolicy.setMinIndex(1);
        rollingPolicy.setMaxIndex(2);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);

        SizeBasedTriggeringPolicy<ILoggingEvent> triggerPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
        triggerPolicy.setMaxFileSize("10MB");

        rollingFileAppender.setTriggeringPolicy(triggerPolicy);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%date [%thread] %-5level %logger{36}.%method - %msg%n");
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
//	    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//	    root.setLevel(Level.DEBUG);
//	    root.addAppender(rollingFileAppender);

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("br.udesc.esag.participactbrasil.participact");
        logger.setLevel(Level.DEBUG);
        logger.addAppender(rollingFileAppender);

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(context);
    }

    public static boolean isUploadingPhoto() {
        return isUploadingPhoto;
    }

    public static void setIsUploadingPhoto(boolean isUploadingPhoto) {
        ParticipActApplication.isUploadingPhoto = isUploadingPhoto;
    }

}
