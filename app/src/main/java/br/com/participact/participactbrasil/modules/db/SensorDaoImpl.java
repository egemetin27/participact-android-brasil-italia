package br.com.participact.participactbrasil.modules.db;

import android.database.Cursor;

import org.most.pipeline.Pipeline;

import java.util.Date;
import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class SensorDaoImpl {

    private SensorDao dao = App.getInstance().getDaoSession().getSensorDao();

    public List<Sensor> findByPipelineType(Pipeline.Type type, int limit) {
        return dao.queryBuilder().where(SensorDao.Properties.PipelineTypeValue.eq(type.toInt())).whereOr(SensorDao.Properties.Uploaded.eq(false), SensorDao.Properties.Uploaded.isNull()).limit(limit).build().list();
    }

    public List<Sensor> findByPipelineType(Pipeline.Type type, Date fromDate) {
        return dao.queryBuilder().where(SensorDao.Properties.PipelineTypeValue.eq(type.toInt()), SensorDao.Properties.DateWhen.ge(fromDate)).build().list();
    }

    public long countByPipelineType(Pipeline.Type type, Date fromDate) {
        Cursor cursor = dao.getDatabase().rawQuery("SELECT COUNT(*) FROM " + SensorDao.TABLENAME + " WHERE " + SensorDao.Properties.PipelineTypeValue.columnName + " = " + type.toInt() + ";", null);
        long count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getLong(0);
        }
        return count;
    }

    public long commit(Sensor sensor) {
        sensor.setDateWhen(new Date());
        return dao.insert(sensor);
    }

    public void remove(Sensor sensor) {
        sensor.setUploaded(true);
        sensor.setDateUploaded(new Date());
        dao.update(sensor);
    }

}
