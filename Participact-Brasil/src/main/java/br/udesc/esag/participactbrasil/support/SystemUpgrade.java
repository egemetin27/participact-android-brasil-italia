package br.udesc.esag.participactbrasil.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.local.TaskStatus;

public class SystemUpgrade {

    private static final String UPGRADE_PREFERENCES = "UPGRADE_PREFERENCES";
    private static final String VERSION = "VERSION";


    public static void upgrade(Context context) {

        context = context.getApplicationContext();

        SharedPreferences pref = context.getSharedPreferences(UPGRADE_PREFERENCES, Context.MODE_PRIVATE);
        int old = pref.getInt(VERSION, -1);

        if (old == -1) {
            SupportStateUtility.deleteState(context);
            org.most.StateUtility.deleteState(context);
        }

        switch (old) {
            case 1:
            case 2:
                SupportStateUtility.deleteState(context);
                org.most.StateUtility.deleteState(context);
            case 17:
                int i = 0;
                File file = new File(context.getFilesDir(), "state.raw");
                if (file.exists()) {
                    try {
                        FileInputStream fileInputStream = context.openFileInput("state.raw");
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        Object obj = objectInputStream.readObject();
                        objectInputStream.close();
                        State oldState = null;
                        if (obj instanceof State) {
                            oldState = (State) obj;
                        }

                        if (oldState != null) {
                            for (TaskStatus task : oldState.getTaskStatusByState(TaskState.RUNNING)) {
                                br.udesc.esag.participactbrasil.domain.persistence.StateUtility.convertTaskStatus(context, task);
                                Log.i(SystemUpgrade.class.getSimpleName(), "Updating task");
                                i++;
                            }
                            for (TaskStatus task : oldState.getTaskStatusByState(TaskState.SUSPENDED)) {
                                br.udesc.esag.participactbrasil.domain.persistence.StateUtility.convertTaskStatus(context, task);
                                Log.i(SystemUpgrade.class.getSimpleName(), "Updating task");
                                i++;
                            }
                            for (TaskStatus task : oldState.getTaskStatusByState(TaskState.ERROR)) {
                                br.udesc.esag.participactbrasil.domain.persistence.StateUtility.convertTaskStatus(context, task);
                                Log.i(SystemUpgrade.class.getSimpleName(), "Updating task");
                                i++;
                            }
                            Log.i(SystemUpgrade.class.getSimpleName(), "Successfully updated " + i + " task.");
                            file.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }

        Editor editor = pref.edit();
        editor.putInt(VERSION, ParticipActConfiguration.VERSION);
        editor.apply();
    }
}
