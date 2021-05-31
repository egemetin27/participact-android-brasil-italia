package br.udesc.esag.participactbrasil.support;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.network.request.LoginRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 09/12/16.
 */

public class LoginRetry {

    public interface LoginRetryListener {
        void onSuccess();

        void onError();
    }

    public LoginRetry(final Context context, final SpiceManager contentManager, final LoginRetryListener listener) {

        contentManager.shouldStop();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int maxWait = 40;
                while (contentManager.isStarted()) {
                    maxWait--;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (maxWait <= 0) {
                        break;
                    }
                }

                contentManager.start(context);

                UserAccount user = UserAccountPreferences.getInstance(context).getUserAccount();

                LoginRequest request = new LoginRequest(user.getUsername(), user.getPassword());
                contentManager.execute(request, request.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED,
                        new RequestListener<Boolean>() {
                            @Override
                            public void onRequestFailure(SpiceException spiceException) {
                                listener.onError();
                            }

                            @Override
                            public void onRequestSuccess(Boolean aBoolean) {
                                listener.onSuccess();
                            }
                        });

            }
        }).start();


    }

}
