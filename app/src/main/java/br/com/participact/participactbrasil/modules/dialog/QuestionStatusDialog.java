package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.util.ConnectionUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.db.QuestionAnswer;
import br.com.participact.participactbrasil.modules.db.QuestionAnswerDaoImpl;
import br.com.participact.participactbrasil.modules.support.DataUploader;

public class QuestionStatusDialog extends ColoredDialog implements View.OnClickListener {

    TextView answersNotSent;
    TextView status;
    boolean sending = false;

    public QuestionStatusDialog(Context context) {
        super(context, null, R.layout.dialog_question_status);

        view.findViewById(R.id.buttonSend).setOnClickListener(this);
        view.findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.textStatus).setVisibility(View.INVISIBLE);

        List<QuestionAnswer> answers = new QuestionAnswerDaoImpl().findToUpload();

        answersNotSent = view.findViewById(R.id.textAnswersNotSent);
        answersNotSent.setText("Respostas pendentes de envio: " + answers.size());

        status = view.findViewById(R.id.textStatus);

        view.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.buttonSend:
                List<QuestionAnswer> answers = new QuestionAnswerDaoImpl().findToUpload();
                if (answers.size() > 0) {
                    if (sending) {
                        return;
                    }
                    sending = true;
                    view.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                    status.setText("Inicializando...");
                    status.setVisibility(View.VISIBLE);
                    ConnectionUtils.isOnline(context, new ConnectionUtils.CheckOnlineCallback() {
                        @Override
                        public void onResponse(boolean isOnline) {
                            if (isOnline) {
                                DataUploader.getInstance().uploadAll();
                                view.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                                status.setText("Enviando...");
                                updateStatus();
                            } else {
                                view.findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                status.setText("Não foi possível conectar ao servidor do ParticipACT. Talvez seja um problema com sua conexão. Verifique sua conexão com a internet e tente novamente.");
                                status.setVisibility(View.VISIBLE);
                                sending = false;
                            }
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(context, "Não há respostas para enviar.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
        }
    }

    private void updateStatus() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<QuestionAnswer> answers = new QuestionAnswerDaoImpl().findToUpload();
                answersNotSent.setText("Respostas pendentes de envio: " + answers.size());
                if (answers.size() == 0) {
                    view.findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                    status.setVisibility(View.INVISIBLE);
                    sending = false;
                } else {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            updateStatus();
                        }
                    }, 5000);
                }
            }
        });
    }
}
