package br.com.participact.participactbrasil.modules.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.rest.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.FeedbackType;
import br.com.participact.participactbrasil.modules.network.requests.FeedbackTypeResponse;

public class FeedbackActivity extends BaseActivity {

    List<FeedbackType> types = new ArrayList<>();
    FeedbackType typeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        showProgress();

        SessionManager.getInstance().feedbackTypes(new SessionManager.RequestCallback<FeedbackTypeResponse>() {
            @Override
            public void onResponse(FeedbackTypeResponse response) {
                dismissProgress();
                if (response != null && response.isSuccess()) {
                    if (response.getTypes() != null && response.getTypes().size() > 0) {
                        types = response.getTypes();
                        typeSelected = types.get(0);
                        setEditTextValue(R.id.type, types.get(0).getName());
                    } else {
                        showError("Erro obtendo a lista de tipos.");
                    }
                } else {
                    showError("Erro obtendo a lista de tipos.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });

    }

    public void closeClick(View view) {
        finish();
    }

    public void sendClick(View view) {
        String desc = getEditTextValue(R.id.description);
        if (desc == null || desc.trim().length() == 0) {
            AlertDialogUtils.showError(this, "Por favor, informe uma descrição.");
            return;
        }
        if (typeSelected == null) {
            AlertDialogUtils.showError(this, "Por favor, selecione uma opção.");
            return;
        }
        showProgress();
        SessionManager.getInstance().feedback(desc, typeSelected.getId(), new SessionManager.RequestCallback<Response>() {
            @Override
            public void onResponse(Response response) {
                dismissProgress();
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                dismissProgress();
                finish();
            }
        });
    }

    public void typeClick(View view) {
        final List<String> options = new ArrayList<>();
        for (FeedbackType type : types) {
            options.add(type.getName());
        }
        AlertDialogUtils.showOptionsDialog(this, "Selecione um item", options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setEditTextValue(R.id.type, options.get(i));
                for (FeedbackType type : types) {
                    if (options.get(i).equals(type.getName())) {
                        typeSelected = type;
                        break;
                    }
                }
            }
        });
    }
}
