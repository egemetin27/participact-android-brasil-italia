package br.com.participact.participactbrasil.modules.db;

import com.bergmannsoft.location.BSLocationManager;
import com.bergmannsoft.util.Utils;

import java.util.List;

import br.com.participact.participactbrasil.modules.support.UserSettings;

public class ActionSensing extends ActionWrapper {

    protected ActionSensing(Campaign campaign, Action action) {
        super(campaign, action);
    }

    public static ActionSensing wrap(Campaign campaign, Action action) {
        return new ActionSensing(campaign, action);
    }

    @Override
    public int getProgress() {
        if (action.getSensorDuration() == null)
            return 0;
        if (campaign.getStartDateString() == null)
            return 0;
        if (action.getInputType() == null)
            return 0;
        long timeInterval = UserSettings.getInstance().getUrbanProblemsGPSInterval() / 1000; // BSLocationManager.UPDATE_LOCATION_TIME_INTERVAL;
        //List<Sensor> entities = new SensorDaoImpl().findByPipelineType(getInputType(), Utils.stringToDate(campaign.getStartDateString(), "yyyy-MM-dd HH:mm:ss"));
        long entitiesCount = new SensorDaoImpl().countByPipelineType(getInputType(), Utils.stringToDate(campaign.getStartDateString(), "yyyy-MM-dd HH:mm:ss"));
        long max = action.getSensorDuration() / timeInterval;
        if (max == 0) {
            return 0;
        }
        int progress = (int)(entitiesCount * 100 / max);
        return Math.min(progress, 100);
    }

}
