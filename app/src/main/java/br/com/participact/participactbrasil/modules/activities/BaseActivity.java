package br.com.participact.participactbrasil.modules.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.bergmannsoft.activity.BActivity;
import com.bergmannsoft.util.FileCache;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.db.PANotification;
import br.com.participact.participactbrasil.modules.db.PANotificationDaoImpl;
import br.com.participact.participactbrasil.modules.dialog.NotificationDialog;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.NotificationsResponse;
import br.com.participact.participactbrasil.modules.support.MessageType;
import br.com.participact.participactbrasil.modules.support.PA;
import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends BActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileData();
//        updateData();
    }

    protected void updateProfileData() {
        try {
            final CircleImageView profileImage = findViewById(R.id.profile_image);
            TextView profileMessage = findViewById(R.id.profile_message);
            if (profileImage != null && profileMessage != null) {
                profileImage.setImageBitmap(PA.getProfile().picture(this, new FileCache.OnBitmapDownloadedListener() {
                    @Override
                    public void onDownloaded(Bitmap bmp) {
                        profileImage.setImageBitmap(bmp);
                    }
                }));
                String msg = PA.getProfile().welcomeMessage();
                profileMessage.setText(msg);
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "Profile image not found in this screen.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateCampaigns() {

    }

    protected void updateBadge() {

    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MessageType.NOTIFICATION:
                Log.i(TAG, "Notification received.");
//                updateData();
                return true;
        }
        return super.handleMessage(message);
    }

}
