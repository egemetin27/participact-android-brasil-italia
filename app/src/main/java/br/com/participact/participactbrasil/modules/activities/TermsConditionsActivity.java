package br.com.participact.participactbrasil.modules.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;

public class TermsConditionsActivity extends BaseActivity {

    public static final String ACCEPTED = "accepted";

    private Campaign campaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        Long id = getIntent().getLongExtra("id", 0);
        if (id > 0) {
            campaign = new CampaignDaoImpl().find(id);
            if (campaign != null) {
                showProgress();
                WebView webView = findViewById(R.id.webView);
                webView.loadDataWithBaseURL(null, campaign.getAgreement(), "text/html", "utf-8", null);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        dismissProgress();
                    }
                });
            }
        }
    }

    public void rejectAction(View view) {
        result(false);
    }

    public void acceptAction(View view) {
        result(true);
    }

    private void result(boolean accepted) {
        if (campaign != null) {
            campaign.setAgreementAccepted(accepted);
            new CampaignDaoImpl().commit(campaign);
        }
        Intent i = new Intent();
        i.putExtra(ACCEPTED, accepted);
        setResult(RESULT_OK, i);
        finish();
    }

}
