package br.udesc.esag.participactbrasil.activities.campaign;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignDetailsFragment;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignResultsFragment;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignSensorsFragment;
import br.udesc.esag.participactbrasil.activities.campaign.CampaignTasksFragment;

/**
 * Created by felipe on 14/05/2016.
 */
public class CampaignActivityPageAdapter extends FragmentPagerAdapter {

    private TaskFlat task;
    private int fragmentsCount = 4;
    boolean hasSensingActions = true;
    boolean hasDirectActions = true;

    private List<Fragment> fragments = new ArrayList<>();
    private CampaignDetailsFragment detailsFragment;
    private CampaignTasksFragment tasksFragment;
    private CampaignSensorsFragment sensorsFragment;
    private CampaignResultsFragment resultsFragment;

    public CampaignActivityPageAdapter(FragmentManager fm, TaskFlat taskFlat) {
        super(fm);
        task = taskFlat;

        if(task.getDirectActions() != null && task.getDirectActions().size() <=0){
            fragmentsCount--;
            hasDirectActions = false;
        }

        if(task.getSensingActions() != null && task.getSensingActions().size() <=0){
            fragmentsCount--;
            hasSensingActions = false;
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                detailsFragment = CampaignDetailsFragment.newInstance(task);
                return detailsFragment;

            case 1:
                if(hasDirectActions) {
                    tasksFragment = CampaignTasksFragment.newInstance(task);
                    return tasksFragment;
                }else if(hasSensingActions){
                    sensorsFragment = CampaignSensorsFragment.newInstance(task);
                    return sensorsFragment;
                }else{
                    resultsFragment = CampaignResultsFragment.newInstance(task);
                    return resultsFragment;
                }

            case 2:
                if(hasDirectActions && hasSensingActions) {
                    sensorsFragment = CampaignSensorsFragment.newInstance(task);
                    return sensorsFragment;
                } else{
                    resultsFragment = CampaignResultsFragment.newInstance(task);
                    return resultsFragment;
                }

            case 3:
                resultsFragment =  CampaignResultsFragment.newInstance(task);
                return resultsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragmentsCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DETALHES";
            case 1:
                if(hasDirectActions) {
                    return "TAREFAS";
                }else if(hasSensingActions){
                    return "SENSORES";
                }else{
                    return "RESULTADOS";
                }

            case 2:
                if(hasDirectActions && hasSensingActions) {
                    return "SENSORES";
                } else{
                    return "RESULTADOS";
                }

            case 3:
                return "RESULTADOS";
        }
        return "";
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (resultsFragment != null) {
            resultsFragment.notifyDataSetChanged();
        }
        if (sensorsFragment != null) {
            sensorsFragment.notifyDataSetChanged();
        }
        if (detailsFragment != null) {
            detailsFragment.notifyDataSetChanged();
        }
        if (tasksFragment != null) {
            tasksFragment.notifyDataSetChanged();
        }
    }
}
