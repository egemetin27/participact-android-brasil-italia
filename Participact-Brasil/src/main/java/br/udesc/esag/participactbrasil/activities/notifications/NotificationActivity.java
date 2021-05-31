package br.udesc.esag.participactbrasil.activities.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import br.com.bergmannsoft.activity.BActivity;
import br.udesc.esag.participactbrasil.R;

/**
 * Created by fabiobergmann on 23/02/17.
 */

public class NotificationActivity extends BActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notificações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if (i != null) {
            String text = i.getStringExtra("text");
            if (text != null) {
                setTextViewValue(R.id.text_message, text);
            }
        }

    }
}
