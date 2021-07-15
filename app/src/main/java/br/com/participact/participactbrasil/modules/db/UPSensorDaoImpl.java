package br.com.participact.participactbrasil.modules.db;

import org.most.pipeline.Pipeline;

import java.util.Date;
import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class UPSensorDaoImpl {

    private UPSensorDao dao = App.getInstance().getDaoSession().getUPSensorDao();

    public List<UPSensor> findByPipelineType(int limit) {
        return dao.queryBuilder().whereOr(UPSensorDao.Properties.Uploaded.eq(false), UPSensorDao.Properties.Uploaded.isNull()).limit(limit).build().list();
    }

    public List<UPSensor> findByPipelineType(Pipeline.Type type, Date fromDate) {
        return dao.queryBuilder().where(UPSensorDao.Properties.PipelineTypeValue.eq(type.toInt()), UPSensorDao.Properties.DateWhen.ge(fromDate)).build().list();
    }

    public long commit(UPSensor sensor) {
        sensor.setDateWhen(new Date());
        return dao.insert(sensor);
    }

    public void remove(UPSensor sensor) {
        dao.delete(sensor);
    }

}
