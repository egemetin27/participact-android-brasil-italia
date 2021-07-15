/*
 * Copyright (C) 2014 University of Bologna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.most;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.most.pipeline.Pipeline;
import org.most.utils.Utils;

//import com.bugsense.trace.BugSenseHandler;

public class MainActivity extends Activity implements OnItemSelectedListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btStart;
    private Button btStop;
    private Button trainingButton;
    private Spinner spinner;
    boolean registered = false;
    private boolean training_mode = false;
    private String selected_activity;
    private int toast_duration = Toast.LENGTH_SHORT;
    private int version;
    private Utils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BugSenseHandler.initAndStartSession(MainActivity.this, "301cb3a3");
//        setContentView(R.layout.activity_main);
//
//        trainingButton = (Button) findViewById(R.id.trainin_btn);
//        trainingButton.setEnabled(false);
//        btStart = (Button) findViewById(R.id.btStart);
//        btStop = (Button) findViewById(R.id.btStop);
//        spinner = (Spinner) findViewById(R.id.spinner);
//        spinner.setOnItemSelectedListener(this);
//        utils = new Utils(getApplicationContext());
//        version = utils.getPole("user_activity");
//        Intent i = new Intent(MainActivity.this, MoSTService.class);
//        i.setAction(MoSTService.START);
//        i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.DR.toInt());
//        startService(i);
//
//        btStart.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                /*
//				Intent i = new Intent(MainActivity.this, MoSTService.class);
//				i.setAction(MoSTService.START);
//
//				if(!training_mode)
//				{
//					i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.DR.toInt());
//				}
//				else
//				{
//					i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.TRAINING.toInt());
//				}
//				startService(i);
//				*/
//                Toast toast = Toast.makeText(getApplicationContext(), "Service started for Activity: " + selected_activity, toast_duration);
//                toast.show();
//                utils.appendLog("[" + System.currentTimeMillis() / 1000 + "] START " + selected_activity, "user_activity", version);
//                spinner.setEnabled(false);
//                btStart.setEnabled(false);
//                trainingButton.setEnabled(false);
//            }
//        });
//
//
//        btStop.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//				/*
//				Intent i = new Intent(MainActivity.this, MoSTService.class);
//				i.setAction(MoSTService.STOP);
//				if(!training_mode)
//				{
//					i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.DR.toInt());
//				}
//				else
//				{
//					i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.TRAINING.toInt());
//				}
//				startService(i);
//				*/
//                Toast toast = Toast.makeText(getApplicationContext(), "Service ended for Activity: " + selected_activity, toast_duration);
//                toast.show();
//                utils.appendLog("[" + System.currentTimeMillis() / 1000 + "] END " + selected_activity, "user_activity", version);
//                spinner.setEnabled(true);
//                btStart.setEnabled(true);
//                trainingButton.setEnabled(true);
//            }
//        });
//
//        trainingButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                training_mode = !training_mode;
//            }
//        });
    }

    @Override
    public void onDestroy() {
        Intent i = new Intent(MainActivity.this, MoSTService.class);
        i.setAction(MoSTService.STOP);
        i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.DR.toInt());
        startService(i);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selected_activity = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

}
