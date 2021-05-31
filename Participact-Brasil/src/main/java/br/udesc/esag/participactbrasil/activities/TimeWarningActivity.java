package br.udesc.esag.participactbrasil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import br.udesc.esag.participactbrasil.R;

public class TimeWarningActivity extends ActionBarActivity {

    private static String ALERT_TITLE;
    private static String ALERT_MSG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        ALERT_TITLE = getString(R.string.attention);
        ALERT_MSG = getString(R.string.wrong_time_details);
        setContentView(R.layout.activity_time_warning);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ALERT_TITLE).setMessage(ALERT_MSG)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }

}
