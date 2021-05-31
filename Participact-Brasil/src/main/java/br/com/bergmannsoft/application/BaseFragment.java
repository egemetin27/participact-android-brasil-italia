package br.com.bergmannsoft.application;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;

/**
 * Created by fabiobergmann on 1/7/15.
 */
public abstract class BaseFragment extends Fragment {

    protected static final String TAG = BaseFragment.class.getName();
    protected BApplication app;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (BApplication) getActivity().getApplication();
        app.setBaseFragment(this);
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

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            default:
                return false;
        }
    }

//    protected void handlePush(int type, Push push) {
//        // @Override it!
//        Log.d(TAG, "handlePush " + push.getBundle().toString());
//        switch (type) {
//            default:
//        }
//    }
}