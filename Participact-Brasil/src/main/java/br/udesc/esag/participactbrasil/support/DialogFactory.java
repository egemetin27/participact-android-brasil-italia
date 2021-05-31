package br.udesc.esag.participactbrasil.support;

import android.app.AlertDialog;
import android.content.Context;

import br.udesc.esag.participactbrasil.R;

public class DialogFactory {

    public static void showCommunicationErrorWithServer(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.server_comunication_error);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

    public static void showTimeError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Attenzione");
        builder.setMessage("Abbiamo rilevato che la data o l'ora impostata sul dispositivo è stata modificata. " +
                "Il normale funzionamento di PartcipAct sarà ripristinato una volta verificata la validità di tali modifiche. È necessaria una connessione internet.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}
