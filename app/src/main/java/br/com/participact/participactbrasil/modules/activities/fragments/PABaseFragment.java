package br.com.participact.participactbrasil.modules.activities.fragments;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.bergmannsoft.application.BaseFragment;
import com.bergmannsoft.util.FileCache;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.support.MessageType;
import br.com.participact.participactbrasil.modules.support.PA;
import de.hdodenhof.circleimageview.CircleImageView;

public class PABaseFragment extends BaseFragment {

    @Override
    public void onShow() {
        super.onShow();
        updateHeader();
    }

    public void updateHeader() {
        if (view != null) {
            final CircleImageView profileImage = view.findViewById(R.id.profile_image);
            TextView profileMessage = view.findViewById(R.id.profile_message);
            if (profileImage != null && profileMessage != null) {
                profileImage.setImageBitmap(PA.getProfile().picture(getActivity(), new FileCache.OnBitmapDownloadedListener() {
                    @Override
                    public void onDownloaded(Bitmap bmp) {
                        profileImage.setImageBitmap(bmp);
                    }
                }));
                profileMessage.setText(PA.getProfile().welcomeMessage());
            }
            View headerLayout = view.findViewById(R.id.headerLayout);
            if (headerLayout != null) {
                headerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        App.getInstance().dispatchMessage(MessageType.SHOW_PROFILE);
                    }
                });
            }
        }
    }
}
