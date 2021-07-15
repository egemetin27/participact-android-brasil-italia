package com.bergmannsoft.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public abstract class CustomDialog {

    protected static final String TAG = "CustomDialog";
    protected Context context;
    protected View view;
    protected DialogInterface.OnClickListener positiveListener;
    protected DialogInterface.OnClickListener negativeListener;
    protected String positiveLabel;
    protected String negativeLabel;
    protected String title;
    protected Dialog mDialog;

    public CustomDialog(Context context, String title, View view) {
        super();
        this.context = context;
        this.title = title;
        this.view = view;

    }

    public Context getContext() {
        return context;
    }

    protected AlertDialog createCustomDialog(String title, View view) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        mDialog = createCustomDialog(title, view);
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

    protected String getEditTextValue(View view, int id) {
        EditText e = (EditText) view.findViewById(id);
        if (e != null)
            return e.getText().toString();
        return null;
    }

    protected void setEditTextValue(View view, int id, String value) {
        EditText e = (EditText) view.findViewById(id);
        if (e != null) {
            e.setText(value);
        }
    }

    protected void setTextViewValue(View view, int id, String value) {
        TextView t = (TextView) view.findViewById(id);
        if (t != null)
            t.setText(value);
    }

}