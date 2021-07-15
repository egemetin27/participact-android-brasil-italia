package br.com.participact.participactbrasil.modules.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bergmannsoft.dialog.AlertDialogUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.QuestionDaoImpl;
import br.com.participact.participactbrasil.modules.db.QuestionOption;

public class CampaignTaskQuestionChooseActivity extends CampaignTaskBaseActivity {

    LinearLayout linearLayoutOptions;
    List<QuestionOption> options;
    List<Long> optionsSelected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_task_question_choose);

        createUI();

    }

    @Override
    protected void createUI() {
        if (!isValid()) {
            return;
        }

        setupDescriptionHeader();

        linearLayoutOptions = findViewById(R.id.linearLayoutOptions);

        options = question.getClosed_answers();
        if (options == null || options.size() == 0) {
            AlertDialogUtils.showAlert(this, "Questão sem opções.");
            return;
        }

        if (questionWrapper.isReadyToUpload()) {
            Button send = findViewById(R.id.buttonSend);
            send.setEnabled(false);
            send.setAlpha(0.4f);
        }

        optionsSelected = questionWrapper.getAnswerIds();

        updateUI();
    }

    private void updateUI() {
        linearLayoutOptions.removeAllViews();
        Button send = findViewById(R.id.buttonSend);
        String sendText = "ENVIAR RESPOSTA";
        if (question.getRequired() != null && !question.getRequired()) {
            sendText = "PULAR QUESTÃO";
        }
        for (QuestionOption option : options) {
            View view = App.getInstance().getLayoutInflater().inflate(R.layout.item_campaign_task_choose, null);
            ImageView radio = view.findViewById(R.id.radio);
            if (optionsSelected.contains(option.getId())) {
                radio.setImageResource(R.mipmap.ic_bt_radio_on);
                sendText = "ENVIAR RESPOSTA";
            } else {
                radio.setImageResource(R.mipmap.ic_bt_radio_off);
            }
            TextView text = view.findViewById(R.id.option);
            text.setText(option.getOption());
            view.setTag(option);
            if (!CampaignWrapper.wrap(campaign).isStatusEnded() && !questionWrapper.isReadyToUpload()) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QuestionOption option = (QuestionOption) view.getTag();
                        if (questionWrapper.isMultiChoice()) {
                            if (optionsSelected.contains(option.getId())) {
                                optionsSelected.remove(option.getId());
                            } else {
                                optionsSelected.add(option.getId());
                            }
                        } else {
                            optionsSelected.clear();
                            optionsSelected.add(option.getId());
                        }
                        updateUI();
                    }
                });
            }
            linearLayoutOptions.addView(view);
        }
        send.setText(sendText);
    }

    public void sendClick(View view) {
        if (questionWrapper.isReadyToUpload()) {
            return;
        }
        if (CampaignWrapper.wrap(campaign).isStatusEnded()) {
            AlertDialogUtils.showAlert(this, "Campanha finalizada.");
            return;
        }
        if (optionsSelected.size() == 0) {
            if (question.getRequired() != null && !question.getRequired()) {
                question.setSkipped(true);
            } else {
                AlertDialogUtils.showAlert(this, "Por favor, escolha uma opção.");
                return;
            }
        }
        prepareToSend(optionsSelected);
    }
}
