package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.view.View;

import com.bergmannsoft.dialog.ColoredDialog;

import br.com.participact.participactbrasil.R;

public class CreateAccountDialog extends ColoredDialog implements View.OnClickListener {

    public interface OnSignListener {
        void onFacebook();
        void onGooglePlus();
        void onSignUp();
    }

    OnSignListener listener;

    public CreateAccountDialog(Context context, OnSignListener listener) {
        super(context, null, R.layout.dialog_create_account);
        this.listener = listener;
        view.findViewById(R.id.buttonClose).setOnClickListener(this);
        view.findViewById(R.id.buttonFacebook).setOnClickListener(this);
        view.findViewById(R.id.buttonGplus).setOnClickListener(this);
        view.findViewById(R.id.buttonSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonFacebook:
                listener.onFacebook();
                break;
            case R.id.buttonGplus:
                listener.onGooglePlus();
                break;
            case R.id.buttonSignUp:
                listener.onSignUp();
                break;
        }
        dismiss();
    }
}
