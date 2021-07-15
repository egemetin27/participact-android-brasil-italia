package br.com.participact.participactbrasil.modules.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        CheckBox checkBox = findViewById(R.id.wifi);
        checkBox.setChecked(UserSettings.getInstance().getSendDataOnlyOverWifi());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UserSettings.getInstance().setSendDataOnlyOverWifi(b);
            }
        });
    }

    public void closeClick(View view) {
        finish();
    }
}
