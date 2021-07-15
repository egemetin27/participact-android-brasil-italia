package br.com.participact.participactbrasil.modules.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.support.PA;
import br.com.participact.participactbrasil.modules.support.PAProfile;

public class ProfileCreateActivity extends ProfileBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile_create);
        super.onCreate(savedInstanceState);
    }

    public void facebookClick(View view) {
        PA.getProfile().facebookLogin(this, new PAProfile.SocialCompletion() {
            @Override
            public void completion(boolean success, Bitmap profileImage) {
                updateData();
                finish();
                present(ProfileEditActivity.class);
            }
        });
    }

    public void googleClick(View view) {
        PA.getProfile().googleLogin(this, new PAProfile.SocialCompletion() {
            @Override
            public void completion(boolean success, Bitmap profileImage) {
                if (success) {
                    updateData();
                    finish();
                    present(ProfileEditActivity.class);
                } else {
                    showError(PA.getProfile().lastGoogleSigninError != null ? PA.getProfile().lastGoogleSigninError.getMessage() : "Erro desconhecido logando com Google.");
                }
            }
        });
    }


}
