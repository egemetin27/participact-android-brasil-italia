package br.com.participact.participactbrasil.modules.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.widget.BSEditText;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class SendOmbudsmanDialog extends ColoredDialog implements View.OnClickListener {

    public interface OnOptionListener {
        void onSend();
        void onCancel();
    }

    OnOptionListener listener;

    public SendOmbudsmanDialog(Context context, OnOptionListener listener) {
        super(context, null, R.layout.dialog_send_ombudsman);
        this.listener = listener;
        view.findViewById(R.id.buttonCancel).setOnClickListener(this);
        view.findViewById(R.id.buttonSend).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCancel:
                listener.onCancel();
                dismiss();
                break;
            case R.id.buttonSend:
                BSEditText name = view.findViewById(R.id.name);
                BSEditText email = view.findViewById(R.id.email);
                if (name.getText().toString().trim().length() == 0) {
                    AlertDialogUtils.showAlert((Activity) context, "Por favor, digite seu nome.");
                } else if (email.getText().toString().trim().length() == 0) {
                    AlertDialogUtils.showAlert((Activity) context, "Por favor, digite seu e-mail.");
                } else {
                    UserSettings.getInstance().setName(name.getText().toString());
                    UserSettings.getInstance().setEmail(email.getText().toString());
                    listener.onSend();
                    dismiss();
                }
                break;
        }
    }
}
