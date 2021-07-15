package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bergmannsoft.dialog.ColoredDialog;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.db.PANotification;

public class NotificationDialog extends ColoredDialog implements View.OnClickListener {

    public interface OnCallBack {
        void onShowAllNotifications();
    }

    OnCallBack listener;

    public NotificationDialog(Context context, PANotification notification, OnCallBack listener) {
        super(context, null, R.layout.dialog_notification);
        this.listener = listener;
        view.findViewById(R.id.buttonClose).setOnClickListener(this);
        view.findViewById(R.id.buttonNotifications).setOnClickListener(this);
        TextView text = view.findViewById(R.id.text);
        text.setText(notification.getMessage());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonNotifications:
                listener.onShowAllNotifications();
                break;
        }
        dismiss();
    }
}
