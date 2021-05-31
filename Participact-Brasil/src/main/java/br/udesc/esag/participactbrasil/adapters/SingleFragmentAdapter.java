package br.udesc.esag.participactbrasil.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.udesc.esag.participactbrasil.activities.notifications.FragmentNotifications;
import br.udesc.esag.participactbrasil.activities.profile.ProfileEditFragment;
import br.udesc.esag.participactbrasil.activities.profile.ProfileFragment;
import br.udesc.esag.participactbrasil.activities.settings.SettingsFragment;
import br.udesc.esag.participactbrasil.activities.webview.WebViewFragment;

/**
 * Created by fabiobergmann on 14/10/16.
 */

public class SingleFragmentAdapter extends FragmentStatePagerAdapter {

    public enum SingleFragmentType {
        SETTINGS,
        PROFILE,
        PROFILE_EDIT,
        NOTIFICATION,
        HELP,
        ABOUT
    }

    private final Context context;
    private final SingleFragmentType type;

    private Fragment currentFragment;

    public SingleFragmentAdapter(FragmentManager fm, Context context, SingleFragmentType type) {
        super(fm);
        this.context = context;
        this.type = type;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (type) {
            case SETTINGS:
                currentFragment = SettingsFragment.newInstance();
                break;
            case PROFILE:
                currentFragment = ProfileFragment.newInstance();
                break;
            case PROFILE_EDIT:
                currentFragment = ProfileEditFragment.newInstance();
                break;
            case ABOUT:
                currentFragment = WebViewFragment.newInstance(WebViewFragment.WebViewType.ABOUT);
                break;
            case HELP:
                currentFragment = WebViewFragment.newInstance(WebViewFragment.WebViewType.HELP);
                break;
            case NOTIFICATION:
                currentFragment = FragmentNotifications.newInstance();
                break;
            default:
                return null;
        }
        return currentFragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

}
