package br.udesc.esag.participactbrasil.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import br.udesc.esag.participactbrasil.activities.campaign.CampaignSensorsFragment;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignTasksFragment;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;

/**
 * Created by felipe on 15/05/2016.
 */
public class SensorsPageAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ActionFlat> actionFlatHistory;
    private ArrayList<ActionFlat> actionFlatPending;
    public SensorsPageAdapter(FragmentManager fm, Context context) {
        super(fm);

        actionFlatPending = new ArrayList<>(StateUtility.getAllOpenSensorsActions(context));
        actionFlatHistory = new ArrayList<>(StateUtility.getDoneSensorsActions(context));
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return CampaignSensorsFragment.newInstance(actionFlatPending, CampaignSensorsFragment.SensorsType.PENDING);
        }else{
            return CampaignSensorsFragment.newInstance(actionFlatHistory, CampaignSensorsFragment.SensorsType.HISTORY);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Pendentes";
            case 1:
                return "Histórico";
        }
        return null;
    }
}
