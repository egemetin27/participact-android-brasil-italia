package br.com.participact.participactbrasil.modules.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bergmannsoft.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.participact.participactbrasil.BuildConfig;
import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTextViewValue(R.id.version, "VERSÃO " + BuildConfig.VERSION_NAME);
        setTextViewValue(R.id.userId, App.getInstance().getDeviceUuid());
        final WebView webView = findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        jobHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
                    HttpGet httpget = new HttpGet("https://bagheera.participact.com.br/v2/pages/PAGE_ABOUT"); // Set the action you want to do
                    HttpResponse response = httpclient.execute(httpget); // Executeit
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent(); // Create an InputStream with the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) // Read line by line
                        sb.append(line + "\n");

                    final String resString = sb.toString(); // Result is here

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadDataWithBaseURL(null, resString.replace("<body", "<body style=\"color: #fff\""), "text/html", "utf-8", null);
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    dismissProgress();
                                }
                            });
                        }
                    });

                    is.close(); // Close the stream
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void closeClick(View view) {
        finish();
    }

    public void twitterClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.participact.com.br"));
        startActivity(browserIntent);
    }

    public void facebookClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.participact.com.br"));
        startActivity(browserIntent);
    }

    public void instagramClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.participact.com.br"));
        startActivity(browserIntent);

    }

    public void siteClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.participact.com.br"));
        startActivity(browserIntent);
    }

    public void termsServiceClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.participact.com.br"));
        startActivity(browserIntent);
    }

    public void copyUserIdClick(View view) {
        String userId = App.getInstance().getDeviceUuid();
        Utils.copyToClipboard(this, userId);
        Toast.makeText(this, "ID " + userId + " copiado.", Toast.LENGTH_LONG).show();
    }
}
