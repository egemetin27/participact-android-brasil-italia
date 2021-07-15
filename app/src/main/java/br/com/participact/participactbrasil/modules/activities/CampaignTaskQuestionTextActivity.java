package br.com.participact.participactbrasil.modules.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.util.Utils;

import java.util.Date;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;

public class CampaignTaskQuestionTextActivity extends CampaignTaskBaseActivity {

    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_task_question_text);

        createUI();

    }

    @Override
    protected void createUI() {
        if (!isValid()) {
            return;
        }

        send = findViewById(R.id.buttonSend);

        setupDescriptionHeader();

        if (question.getRequired() != null && !question.getRequired()) {
            send.setText("PULAR QUESTÃO");
        }

        EditText answer = findViewById(R.id.answer);
        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                send.setText("ENVIAR RESPOSTA");
            }
        });
        answer.setText("");
        if (questionWrapper.isDate()) {
            String date = Utils.dateToString("dd/MM/yyyy", new Date());
            answer.setText(date);
            answer.setHint(date);
        }
        if (question.getAnswer() != null) {
            answer.setText(question.getAnswer());
        }

        if (CampaignWrapper.wrap(campaign).isStatusEnded() || questionWrapper.isReadyToUpload()) {
            answer.setEnabled(false);
        }

        if (questionWrapper.isReadyToUpload()) {
            send.setEnabled(false);
            send.setAlpha(0.4f);
        } else {
            send.setEnabled(true);
            send.setAlpha(1.0f);
        }
    }

    public void sendClick(View view) {
        if (questionWrapper.isReadyToUpload()) {
            return;
        }
        if (CampaignWrapper.wrap(campaign).isStatusEnded()) {
            AlertDialogUtils.showAlert(this, "Campanha finalizada.");
            return;
        }
        EditText answer = findViewById(R.id.answer);
        if (answer.getText().toString().trim().length() == 0 && (question.getRequired() == null || (question.getRequired() != null && question.getRequired()))) {
            AlertDialogUtils.showAlert(this, "Por favor, escreva sua resposta.");
        } else {
            if (answer.getText().toString().trim().length() == 0) {
                question.setSkipped(true);
            }
            prepareToSend(answer.getText().toString());
        }
    }

}
