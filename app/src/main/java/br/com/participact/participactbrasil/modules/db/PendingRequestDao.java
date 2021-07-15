package br.com.participact.participactbrasil.modules.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PENDING_REQUEST".
*/
public class PendingRequestDao extends AbstractDao<PendingRequest, Long> {

    public static final String TABLENAME = "PENDING_REQUEST";

    /**
     * Properties of entity PendingRequest.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CampaignId = new Property(1, Long.class, "campaignId", false, "CAMPAIGN_ID");
        public final static Property RequestType = new Property(2, String.class, "requestType", false, "REQUEST_TYPE");
    }


    public PendingRequestDao(DaoConfig config) {
        super(config);
    }
    
    public PendingRequestDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PENDING_REQUEST\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CAMPAIGN_ID\" INTEGER," + // 1: campaignId
                "\"REQUEST_TYPE\" TEXT);"); // 2: requestType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PENDING_REQUEST\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PendingRequest entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long campaignId = entity.getCampaignId();
        if (campaignId != null) {
            stmt.bindLong(2, campaignId);
        }
 
        String requestType = entity.getRequestType();
        if (requestType != null) {
            stmt.bindString(3, requestType);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PendingRequest entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long campaignId = entity.getCampaignId();
        if (campaignId != null) {
            stmt.bindLong(2, campaignId);
        }
 
        String requestType = entity.getRequestType();
        if (requestType != null) {
            stmt.bindString(3, requestType);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PendingRequest readEntity(Cursor cursor, int offset) {
        PendingRequest entity = new PendingRequest( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // campaignId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // requestType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PendingRequest entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCampaignId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setRequestType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PendingRequest entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PendingRequest entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PendingRequest entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}