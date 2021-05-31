package br.udesc.esag.participactbrasil.support;

import android.content.Context;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.security.KeyStore;

import br.udesc.esag.participactbrasil.R;

public class HttpUtils {

    private static Context context;
    public static HttpClient getHttpClient(Context ctx) {
        context = ctx;
        return new HttpClientLooseSSL();
    }

    private static SSLSocketFactory newSslSocketFactory(Context context) {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = context.getResources().openRawResource(R.raw.keystore_labges);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, "123456".toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private static class HttpClientLooseSSL extends DefaultHttpClient {

        @Override
        protected ClientConnectionManager createClientConnectionManager() {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, 8000);
            HttpConnectionParams.setSoTimeout(params, 18000);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
            // Register for port 443 our SSLSocketFactory with our keystore
            // to the ConnectionManager
            try {
                registry.register(new Scheme("https", newSslSocketFactory(context), 443));
                registry.register(new Scheme("https", newSslSocketFactory(context), 8443));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ThreadSafeClientConnManager(params, registry);
        }
    }
}
