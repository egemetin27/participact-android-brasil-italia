package br.udesc.esag.participactbrasil.domain.persistence.support;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import br.udesc.esag.participactbrasil.domain.persistence.Message;

/**
 * Created by fabiobergmann on 23/02/17.
 */

public class NotificationsDao  implements Serializable {

    private static final String FILE_NAME = "notifications";
    private static final String TAG = "NotificationsDao";
    private static NotificationsDao instance;

    private HashMap<Long, Message> messages = new HashMap<>();

    public static NotificationsDao getInstance(Context context) {

        if (instance != null)
            return instance;

        FileInputStream fis = null;
        ObjectInputStream is = null;
        try {
            fis = context.openFileInput(FILE_NAME);
            is = new ObjectInputStream(fis);
            instance = (NotificationsDao) is.readObject();
            return instance;
        } catch (FileNotFoundException e) {
            // expected
            Log.e(TAG, "Arquivo não foi encontrado. Provavelmente é a primeira vez que será salvo.");
        } catch (IOException e) {
            Log.e(TAG, null, e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, null, e);
            context.deleteFile(FILE_NAME);
        } catch (Exception e) {
            Log.e(TAG, null, e);
            context.deleteFile(FILE_NAME);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
                Log.e(TAG, null, e);
            }
        }
        return new NotificationsDao();
    }

    public void save(Context context, Message message) {

        if (messages.containsKey(message.getId())) {
            return;
        }

        messages.put(message.getId(), message);

        FileOutputStream fos = null;
        ObjectOutputStream os = null;

        try {
            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(this);
        } catch (ConcurrentModificationException e) {
            Log.e(TAG, null, e);
        } catch (IOException e) {
            Log.e(TAG, null, e);
        } finally {
            try {
                if (os != null)
                    os.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                Log.e(TAG, null, e);
            }
        }
    }

    public List<Message> fetchAll() {
        List<Message> list = new ArrayList<>(messages.values());
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message a, Message b) {
                return b.getId().compareTo(a.getId());
            }
        });
        return list;
    }

    public void delete(Context context) {
        try {
            context.deleteFile(FILE_NAME);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
    }

}
