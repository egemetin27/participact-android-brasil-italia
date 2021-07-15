package com.bergmannsoft.dialog;

import android.content.Context;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import br.com.participact.participactbrasil.R;

public class BProgressDialog extends Dialog {
    private static final String TAG = "BProgressDialog";
    private static BProgressDialog mBProgressDialog;
    private final TextView mMessage;
    private BProgressDialog mProgressbar;
    private OnDismissListener mOnDissmissListener;

    private BProgressDialog(Context context, String message) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progressbar);
        mMessage = (TextView) findViewById(R.id.message);
        if (message != null) {
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(message);
        } else {
            mMessage.setVisibility(View.GONE);
        }
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOnDissmissListener != null) {
            mOnDissmissListener.onDismiss(this);
        }
    }

    public static void showProgressBar(Context context, boolean cancelable) {
        showProgressBar(context, cancelable, null);
    }

    public static void showProgressBar(Context context, boolean cancelable, String message) {
        if (mBProgressDialog != null && mBProgressDialog.isShowing()) {
            try {
                mBProgressDialog.cancel();
            } catch (Exception e) {
                Crashlytics.log(Log.ERROR, TAG, e.toString());
            }
        }
        try {
            mBProgressDialog = new BProgressDialog(context, message);
            mBProgressDialog.setCancelable(cancelable);
            mBProgressDialog.show();
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, TAG, e.toString());
        }
    }

    public static void showProgressBar(Context context, OnDismissListener listener) {

        if (mBProgressDialog != null && mBProgressDialog.isShowing()) {
            mBProgressDialog.cancel();
        }
        mBProgressDialog = new BProgressDialog(context, null);
        mBProgressDialog.setListener(listener);
        mBProgressDialog.setCancelable(Boolean.TRUE);
        mBProgressDialog.show();
    }

    public static void updateMessage(String message) {
        if (mBProgressDialog != null) {
            try {
                mBProgressDialog.doUpdateMessage(message);
            } catch (Exception e) {
                Crashlytics.log(Log.ERROR, TAG, e.toString());
            }
        }
    }

    public void doUpdateMessage(String message) {
        if (mMessage != null) {
            mMessage.setText(message);
        }
    }

    public static void hideProgressBar() {
        if (mBProgressDialog != null) {
            try {
                mBProgressDialog.dismiss();
            } catch (Exception e) {
                Crashlytics.log(Log.ERROR, TAG, e.toString());
            }
        }
    }

    private void setListener(OnDismissListener listener) {
        mOnDissmissListener = listener;

    }

    public static void showListViewBottomProgressBar(View view) {
        if (mBProgressDialog != null) {
            mBProgressDialog.dismiss();
        }

        view.setVisibility(View.VISIBLE);
    }

    public static void hideListViewBottomProgressBar(View view) {
        if (mBProgressDialog != null) {
            mBProgressDialog.dismiss();
        }

        view.setVisibility(View.GONE);
    }

    public void showProgress(Context context, boolean cancelable, String message) {

        if (mProgressbar != null && mProgressbar.isShowing()) {
            mProgressbar.cancel();
        }
        mProgressbar.setCancelable(cancelable);
        mProgressbar.show();
    }

}