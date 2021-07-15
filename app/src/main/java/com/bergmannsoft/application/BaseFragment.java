package com.bergmannsoft.application;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fabiobergmann on 1/7/15.
 */
public abstract class BaseFragment extends Fragment {

    protected static final String TAG = BaseFragment.class.getName();
    protected BApplication app;
    protected Handler jobHandler;

    protected View view;

    protected View inflateLayout(int layoutId, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        view = inflater.inflate(layoutId, container, false);
        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (BApplication) getActivity().getApplication();
        jobHandler = app.getJobHandler();
        app.setBaseFragment(this);
    }

    protected void present(Class<? extends Activity> activity) {
        Intent i = new Intent(getActivity(), activity);
        getActivity().startActivity(i);
    }

    @Override
    public void onDestroy() {
        app.setBaseFragment(null);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        app.setBaseFragment(this);
    }

    @Override
    public void onPause() {
        app.setBaseFragment(null);
        super.onPause();
    }

    public void onShow() {
        app.setBaseFragment(this);
    }

    public void onHide() {
        app.setBaseFragment(null);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            default:
                return false;
        }
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

    protected void setImageView(View view, int id, Bitmap bmp) {
        ImageView i = (ImageView) view.findViewById(id);
        if (i != null)
            i.setImageBitmap(bmp);
    }

//    protected void handlePush(int type, Push push) {
//        // @Override it!
//        Log.d(TAG, "handlePush " + push.getBundle().toString());
//        switch (type) {
//            default:
//        }
//    }
}