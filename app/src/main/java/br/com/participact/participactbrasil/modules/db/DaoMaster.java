package br.com.participact.participactbrasil.modules.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 39): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 39;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        UPFileDBDao.createTable(db, ifNotExists);
        CampaignDao.createTable(db, ifNotExists);
        CampaignStatusDao.createTable(db, ifNotExists);
        ActionDao.createTable(db, ifNotExists);
        QuestionDao.createTable(db, ifNotExists);
        QuestionOptionDao.createTable(db, ifNotExists);
        QuestionAnswerDao.createTable(db, ifNotExists);
        SensorDao.createTable(db, ifNotExists);
        UPSensorDao.createTable(db, ifNotExists);
        PhotoDao.createTable(db, ifNotExists);
        LogDao.createTable(db, ifNotExists);
        PANotificationDao.createTable(db, ifNotExists);
        AbuseTypeDao.createTable(db, ifNotExists);
        PendingRequestDao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        UPFileDBDao.dropTable(db, ifExists);
        CampaignDao.dropTable(db, ifExists);
        CampaignStatusDao.dropTable(db, ifExists);
        ActionDao.dropTable(db, ifExists);
        QuestionDao.dropTable(db, ifExists);
        QuestionOptionDao.dropTable(db, ifExists);
        QuestionAnswerDao.dropTable(db, ifExists);
        SensorDao.dropTable(db, ifExists);
        UPSensorDao.dropTable(db, ifExists);
        PhotoDao.dropTable(db, ifExists);
        LogDao.dropTable(db, ifExists);
        PANotificationDao.dropTable(db, ifExists);
        AbuseTypeDao.dropTable(db, ifExists);
        PendingRequestDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UPFileDBDao.class);
        registerDaoClass(CampaignDao.class);
        registerDaoClass(CampaignStatusDao.class);
        registerDaoClass(ActionDao.class);
        registerDaoClass(QuestionDao.class);
        registerDaoClass(QuestionOptionDao.class);
        registerDaoClass(QuestionAnswerDao.class);
        registerDaoClass(SensorDao.class);
        registerDaoClass(UPSensorDao.class);
        registerDaoClass(PhotoDao.class);
        registerDaoClass(LogDao.class);
        registerDaoClass(PANotificationDao.class);
        registerDaoClass(AbuseTypeDao.class);
        registerDaoClass(PendingRequestDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
