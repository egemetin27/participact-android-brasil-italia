package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.bergmannsoft.dialog.ColoredDialog;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.db.ActionDaoImpl;
import br.com.participact.participactbrasil.modules.db.ActionWrapper;
import br.com.participact.participactbrasil.modules.db.Action;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;

public class QuestionnaireRepeatDialog extends ColoredDialog {

    private final Long campaignId;
    private final Long actionId;

    public interface QuestionnaireDialogListener {
        void onQuestionnaireAvailable();
        void onOKClick();
    }

    private QuestionnaireDialogListener mListener;

    private boolean canceled = false;

    public QuestionnaireRepeatDialog(Context context, Long campaignId, Long actionId, QuestionnaireDialogListener listener) {
        super(context, null, R.layout.dialog_questionnaire_repeat);

        this.campaignId = campaignId;
        this.actionId = actionId;
        mListener = listener;

        view.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canceled = true;
                dismiss();
                mListener.onOKClick();
            }
        });

        checkAvailable();

    }

    private void checkAvailable() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Campaign campaign = new CampaignDaoImpl().find(campaignId);
                Action action = new ActionDaoImpl().find(actionId);
                ActionWrapper actionWrapper = ActionWrapper.wrap(campaign, action);
                if (actionWrapper.getNextQuestion() != null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!canceled) {
                                mListener.onQuestionnaireAvailable();
                                dismiss();
                            }
                        }
                    });
                } else {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!canceled) {
                        checkAvailable();
                    }
                }
            }
        }).start();


    }

}
