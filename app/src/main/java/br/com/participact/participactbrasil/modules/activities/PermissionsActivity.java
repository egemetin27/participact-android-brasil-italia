package br.com.participact.participactbrasil.modules.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.util.Utils;

import br.com.participact.participactbrasil.R;

/**
 * Created by fabiobergmann on 23/03/18.
 */

public class PermissionsActivity extends BaseActivity {

    private static final String[] INITIAL_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.READ_CONTACTS,
//            Manifest.permission.CALL_PHONE
    };
    private static final int INITIAL_REQUEST = 1337;

    public final static int REQUEST_CODE = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
    }

    @Override
    public void onBackPressed() {
        // Prevent
    }

    public void doPermissionAction(View view) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        else
            present(MainActivity.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case INITIAL_REQUEST:
                if (Utils.hasAllNecessaryPermissions(this)) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        present(MainActivity.class);
                        if (Utils.requiresRestartForWritePermission()) {
                            Utils.doRestart(this);
                        }
                    }
                } else {
                    AlertDialogUtils.showAlert(this, "Você negou uma ou mais permissões necessárias para o funcionamento do ParticipACT. Por favor, pressiona OK e aceite as permissões.");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                present(MainActivity.class);
                if (Utils.requiresRestartForWritePermission()) {
                    Utils.doRestart(this);
                }
            } else {
                present(MainActivity.class);
            }
        }
    }

}
