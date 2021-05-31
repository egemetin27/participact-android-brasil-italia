package br.udesc.esag.participactbrasil.activities.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.welcome.WelcomeActivity;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 13/10/16.
 */

public class SettingsFragment extends Fragment {

    private View fragment;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create view
        fragment = inflater.inflate(R.layout.fragment_settings, container, false);
        // get switch instance
        Switch sw = (Switch) fragment.findViewById(R.id.switchWiFi);
        // set checked or not according saved preferences
        sw.setChecked(Settings.getInstance(getActivity()).isUseWiFiOnly());
        // setup listener to save preferences
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.getInstance(getActivity()).setUseWiFiOnly(isChecked);
            }
        });
        fragment.findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtils.createDialog(getActivity(), getString(R.string.alert), getString(R.string.logout_alert_message), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        return fragment;
    }

    private void logout() {
        try {
            StateUtility.clearDataBase(getContext().getApplicationContext());
            UserAccountPreferences.getInstance(getContext()).deleteUserAccount();
            startActivity(new Intent(getContext(), WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            getActivity().finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
