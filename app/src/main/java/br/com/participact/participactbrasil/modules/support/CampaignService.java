package br.com.participact.participactbrasil.modules.support;

import android.content.Intent;
import android.util.Log;

//import org.most.MoSTService;

import org.most.MoSTService;
import org.most.pipeline.Pipeline;

import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Action;
import br.com.participact.participactbrasil.modules.db.Campaign;

public class CampaignService {

    private static final String TAG = CampaignService.class.getSimpleName();

    public static void forceStop(Pipeline.Type type) {
        Intent i = new Intent(App.getInstance(), MoSTService.class);
        i.setAction(MoSTService.STOP);
        i.putExtra(MoSTService.KEY_PIPELINE_TYPE, type.toInt());
        try {
            App.getInstance().startService(i);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
    }

    public enum MostAction {
        start,
        stop
    }

    public static void perform(MostAction action, Campaign campaign) {
        if (campaign.getActions() != null) {
            for (Action act : campaign.getActions()) {
                if (act.getInputType() != null && act.getInputType() > 0) {
                    Intent i = new Intent(App.getInstance(), MoSTService.class);
                    switch (action) {
                        case start:
                            i.setAction(MoSTService.START);
                            break;
                        case stop:
                            i.setAction(MoSTService.STOP);
                            break;
                    }
                    i.putExtra(MoSTService.KEY_PIPELINE_TYPE, act.getInputType());
                    App.getInstance().startService(i);
                }
            }
        }
    }

}
