package com.bergmannsoft.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.Locale;

//import global.call2action.R;

/**
 * Created by fabiobergmann on 27/12/16.
 */

public class SpeechUtils {

    public static final int REQ_CODE_SPEECH_INPUT = 10000;

    public static void promptSpeechInput(Activity activity, String speechPrompt) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, speechPrompt);
        try {
            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
//            Toast.makeText(activity.getApplicationContext(),
//                    activity.getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
        }
    }

}
