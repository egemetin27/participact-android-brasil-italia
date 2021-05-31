package br.com.bergmannsoft.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public abstract class CustomDialog {

    protected static final String TAG = "CustomDialog";
    protected Context context;
    protected View customView;
    protected DialogInterface.OnClickListener positiveListener;
    protected DialogInterface.OnClickListener negativeListener;
    protected String positiveLabel;
    protected String negativeLabel;
    protected String title;
    protected Dialog mDialog;

    public CustomDialog(Context context, String title, View customView) {
        super();
        this.context = context;
        this.title = title;
        this.customView = customView;

    }

    protected AlertDialog createCustomDialog(String title, View view) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        if (title != null)
            alert.setTitle(title);
        alert.setView(view);
        if (positiveListener != null)
            alert.setButton(AlertDialog.BUTTON_POSITIVE, positiveLabel, positiveListener);
        if (negativeListener != null)
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, negativeLabel, negativeListener);
        return alert;
    }

    public void setPositiveButton(String positiveLabel, DialogInterface.OnClickListener positiveListener) {
        this.positiveLabel = positiveLabel;
        this.positiveListener = positiveListener;
    }

    public void setNegativeButton(String negativeLabel, DialogInterface.OnClickListener negativeListener) {
        this.negativeLabel = negativeLabel;
        this.negativeListener = negativeListener;
    }

    public Dialog show() {
        mDialog = createCustomDialog(title, customView);
        mDialog.show();
        return mDialog;
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void dismiss() {
        if (mDialog != null)
            mDialog.dismiss();
    }

}