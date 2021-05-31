package br.udesc.esag.participactbrasil.activities.dashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.udesc.esag.participactbrasil.activities.dashboard.CampaignHistoryFragment;
import br.udesc.esag.participactbrasil.activities.dashboard.CampaignActiveFragment;

/**
 * Created by felipe on 15/05/2016.
 */
public class CampaignPageAdapter extends FragmentStatePagerAdapter {

    private CampaignActiveFragment campaignActiveFragment;
    private CampaignHistoryFragment campaignHistoryFragment;

    public CampaignPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            campaignActiveFragment = CampaignActiveFragment.newInstance();
            return campaignActiveFragment;
        } else {
            campaignHistoryFragment = CampaignHistoryFragment.newInstance();
            return campaignHistoryFragment;
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
                return "Ativas";
            case 1:
                return "Histórico";
        }
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (campaignActiveFragment != null) {
            campaignActiveFragment.performRequestForAvailableTasks();
        }
        if (campaignHistoryFragment != null) {
            campaignHistoryFragment.reloadTaskList();
        }
    }
}
