package br.udesc.esag.participactbrasil.activities.webview;

import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.SensorUtils;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.rest.PagesRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.PagesRestResult;
import br.udesc.esag.participactbrasil.network.request.PagesRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.preferences.PASystemPreferences;

/**
 * Created by fabiobergmann on 14/10/16.
 */

public class WebViewFragment extends Fragment {

    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
    private WebView webView;

    public enum WebViewType {
        HELP,
        ABOUT
    }

    private WebViewType type;

    private View fragment;

    public void setType(WebViewType type) {
        this.type = type;
    }

    public static WebViewFragment newInstance(WebViewType type) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.setType(type);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_webview, container, false);

        final PASystemPreferences preferences = PASystemPreferences.getInstance(getContext());
        String content = null;
        switch (type) {
            case HELP:
                content = preferences.getHelpContent();
                break;
            case ABOUT:
                content = preferences.getAboutContent();
                break;
        }

        if (!contentManager.isStarted()) {
            contentManager.start(getActivity());
        }

        webView = (WebView) fragment.findViewById(R.id.webview);

        if (Utils.isValidNotEmpty(content)) {
//            webView.loadData(
//                    content,
//                    "text/html",
//                    "utf-8"
//            );
            webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        } else {
            ProgressDialog.show(getActivity(), null);
        }

        final PagesRequest request = new PagesRequest(getContext(), new PagesRestRequest());
        final String finalContent = content;
        contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<PagesRestResult>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("WebViewFragment", null, spiceException);
                ProgressDialog.dismiss(getActivity());
                AlertDialogUtils.showError(getActivity(), spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(PagesRestResult result) {

                if (result.isSuccess()) {

                    try {
                        String[] pages = result.getData().split(",");

                        preferences.saveHelpUrl(pages[0]);
                        preferences.saveAboutUrl(pages[1]);
                    } catch (Exception e) {
                        Log.e("WebViewFragment", null, e);
                    }

                    getContent();

                } else {
                    ProgressDialog.dismiss(getActivity());
                    if (!Utils.isValidNotEmpty(finalContent)) {
                        AlertDialogUtils.showError(getActivity(), result.getMessage());
                    }
                }

            }
        });

        return fragment;
    }

    private void getContent() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                PASystemPreferences preferences = PASystemPreferences.getInstance(getContext());
                String url = null;
                switch (type) {
                    case HELP:
                        url = preferences.getHelpUrl();
                        break;
                    case ABOUT:
                        url = preferences.getAboutUrl();
                        break;
                }

                InputStream in = null;
                try {
                    URL page = new URL(url);
                    in = page.openStream();

                    String inputLine;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024 * 10];
                    int read;

                    while ((read = in.read(buffer, 0, buffer.length)) > 0) {
                        baos.write(buffer, 0, read);
                    }

                    String content = new String(baos.toByteArray(), Charset.forName("utf-8"));
                    baos.close();

                    switch (type) {
                        case HELP:
                            preferences.saveHelpContent(content);
                            break;
                        case ABOUT:
                            preferences.saveAboutContent(content);
                            break;
                    }

                    final String finalContent = content;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ProgressDialog.dismiss(getActivity());

                            if (Utils.isValidNotEmpty(finalContent)) {

                                String content = finalContent;

                                if (type == WebViewType.HELP) {
                                    // TODO - remove this
                                    content += "<br><br><b>Available Sensors</b><br><br>" + SensorUtils.getAvailableSensors(getContext()).replaceAll(",", "<br>") + "<br><br>";
                                } else if (type == WebViewType.ABOUT) {
                                    String version = "";
                                    try {
                                        PackageInfo pInfo = ParticipActApplication.getInstance().getPackageManager().getPackageInfo(ParticipActApplication.getInstance().getPackageName(), 0);
                                        version = pInfo.versionName + " (" + pInfo.versionCode + ")";
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    content = "V " + version + "<br><br>" + content;
                                }

//                                webView.loadData(
//                                        content,
//                                        "text/html",
//                                        "utf-8"
//                                );

                                webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in!=null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return null;
            }
        }.execute();

    }
}
