package br.com.bergmannsoft.network;

import android.provider.ContactsContract.Profile;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import br.com.bergmannsoft.util.TextHelper;

public class RestUtils {

    private static final String TAG = "RestUtils";

    public static String doGet(String url) throws IOException {
        return doGet(url, null);
    }

    public static String doGet(String url, String authorization)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient();
        HttpGet get = new HttpGet(url);

        if (authorization != null) {
            get.addHeader("Authorization", authorization);
        }

        HttpResponse response = httpclient.execute(get);
        // Log.d(TAG, "" + response.getAllHeaders().toString());
        InputStream var = response.getEntity().getContent();

        return readHttpResponse(var);
    }

    public static String doPostFile(String filename, String url,
                                    String authorization) throws IOException, JSONException {
        File f = new File(filename);
        FileBody body = new FileBody(f);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("PayloadMetaData",
                new JSONObject().put("filename", f.getName()).toString());
        builder.addPart("payload-data", body);

        return RestUtils.doPost(builder.build(), url, authorization,
                "multipart/form-data");
    }

    // public static String doPost(HttpEntity entity, String url)
    // throws IOException {
    //
    // return doPost(entity, url, null, null, null, false);
    // }

    public static String doPost(HttpEntity entity, String url,
                                String contentType) throws IOException {

        return doPost(entity, url, null, contentType, null, false);
    }

    public static String doPost(HttpEntity entity, String url,
                                String authorization, String contentType) throws IOException {
        return doPost(entity, url, authorization, contentType, null, false);
    }

    public static String doPost(HttpEntity entity, String url,
                                String authorization, String contentType, int timeout) throws IOException {
        return doPost(entity, url, authorization, contentType, null, false, timeout);
    }

    public static String doPost(HttpEntity entity, String url,
                                String authorization, String contentType,
                                String uploadContentLength, boolean setUploadHeader) throws IOException {
        return doPost(entity, url, authorization, contentType, uploadContentLength, setUploadHeader, 240000);
    }

    public static String doPost(HttpEntity entity, String url,
                                String authorization, String contentType,
                                String uploadContentLength, boolean setUploadHeader, int timeout)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient(timeout);
        httpclient.getParams().setParameter("http.protocol.content-charset",
                "UTF-8");
        HttpPost post = new HttpPost(url);
        if (authorization != null) {
            post.addHeader("Auth", /* "Bearer " + bearer */
                    authorization);
        }
        if (contentType != null) {
            post.setHeader("Content-Type", contentType);
        }

        if (setUploadHeader) {
            post.addHeader("X-Upload-Content-Length", uploadContentLength);
            post.addHeader("X-Upload-Content-Type", "video/*");
        }

        post.setEntity(entity);

        HttpResponse response = httpclient.execute(post);
        InputStream var = response.getEntity().getContent();

        return readHttpResponse(var);
    }

    public static String doPostFile(HttpEntity entity, String url,
                                    String authorization, ProgressHttpEntityWrapper.ProgressCallback progressCallback)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient(60000 * 10);
        httpclient.getParams().setParameter("http.protocol.content-charset",
                "UTF-8");
        HttpPost post = new HttpPost(url);
        if (authorization != null) {
            post.addHeader("Authorization", /* "Bearer " + bearer */
                    authorization);
        }
//        if (contentType != null) {
//            post.setHeader("Content-Type", contentType);
//        }
//
//        if (setUploadHeader) {
//            post.addHeader("X-Upload-Content-Length", uploadContentLength);
//            post.addHeader("X-Upload-Content-Type", "video/*");
//        }

        post.setEntity(new ProgressHttpEntityWrapper(entity, progressCallback));

        HttpResponse response = httpclient.execute(post);
        InputStream var = response.getEntity().getContent();

        return readHttpResponse(var);
    }

    public static String doPut(HttpEntity entity, String url)
            throws IOException {

        return doPut(entity, url, null, null, null);
    }

    public static String doPut(HttpEntity entity, String url, String contentType)
            throws IOException {
        return doPut(entity, url, null, contentType, null);
    }

    public static String doPut(HttpEntity entity, String url,
                               String authorization, String contentType) throws IOException {
        return doPut(entity, url, authorization, contentType, null);
    }

    public static String doPut(HttpEntity entity, String url,
                               String authorization, String contentType, String uploadContentLength)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient();
        httpclient.getParams().setParameter("http.protocol.content-charset",
                "UTF-8");
        HttpPut put = new HttpPut(url);
        if (authorization != null) {
            put.addHeader("Authorization", /* "Bearer " + bearer */
                    authorization);
        }
        if (contentType != null) {
            put.setHeader("Content-Type", contentType);
        }

        put.setEntity(entity);

        HttpResponse response = httpclient.execute(put);
        // Log.d(TAG, "Response code: " +
        // response.getStatusLine().getStatusCode());
        InputStream var = response.getEntity().getContent();

        return readHttpResponse(var);
    }

    public static String doPostFile(HttpEntity entity, String url,
                                    String bearer, String contentType, long contentLength)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient();
        HttpPost post = new HttpPost(url);
        if (bearer != null) {
            post.addHeader("Authorization", "Bearer " + bearer);
        }

        post.setHeader("Content-Type", contentType);
        // post.setHeader("Content-Length", "" + contentLength);

        if (entity != null) {
            post.setEntity(entity);
        }

        HttpResponse response = httpclient.execute(post);
        // Log.d(TAG, "Response code: " +
        // response.getStatusLine().getStatusCode());
        InputStream var = response.getEntity().getContent();

        return readHttpResponse(var);
    }

    public static HttpResponse doPostForResponse(HttpEntity entity, String url,
                                                 String authorization, String contentType,
                                                 String uploadContentLength, boolean setUploadHeader)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient();
        httpclient.getParams().setParameter("http.protocol.content-charset",
                "UTF-8");
        HttpPost post = new HttpPost(url);
        if (authorization != null) {
            post.addHeader("Authorization", /* "Bearer " + */authorization);
        }
        if (contentType != null) {
            post.setHeader("Content-Type", contentType);
        }

        if (setUploadHeader) {
            post.addHeader("X-Upload-Content-Length", uploadContentLength);
            post.addHeader("X-Upload-Content-Type", "video/*");
        }

        post.setEntity(entity);

        HttpResponse response = httpclient.execute(post);
        // Log.d(TAG, "Response code: " +
        // response.getStatusLine().getStatusCode());
        // InputStream var = response.getEntity().getContent();

        return response;
    }

    public static String doDelete(String url) throws IOException {
        return doDelete(url, null);
    }

    public static String doDelete(String url, String authorization)
            throws IOException {

        HttpClient httpclient = getDefaultHttpClient();
        HttpDelete delete = new HttpDelete(url);

        if (authorization != null) {
            delete.addHeader("Authorization", authorization);
        }

        HttpResponse response = httpclient.execute(delete);
        InputStream var = response.getEntity().getContent();

        return readHttpResponse(var);
        // int statusCode = response.getStatusLine().getStatusCode();
        // return statusCode >= 200 && statusCode < 300;
    }

    public static DefaultHttpClient getDefaultHttpClient() {
        return getDefaultHttpClient(240000);
    }

    public static DefaultHttpClient getDefaultHttpClient(int timeout) {

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", TrustAllSSLSocketFactory
                .getSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, timeout);

        SingleClientConnManager mgr = new SingleClientConnManager(params,
                registry);
        return new DefaultHttpClient(mgr, params);
    }

    public static String readHttpResponse(InputStream inputStream)
            throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream, "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static String getPathFromRedirectRequest(String url,
                                                    Profile profile, final String header) throws IllegalStateException,
            IOException {
        final StringBuilder builder = new StringBuilder();

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        httpclient.setRedirectHandler(new RedirectHandler() {

            @Override
            public boolean isRedirectRequested(HttpResponse response,
                                               HttpContext context) {
                Header h = response.getFirstHeader(header);
                if (h != null)
                    builder.append(h.getValue());
                return false;
            }

            @Override
            public URI getLocationURI(HttpResponse response, HttpContext context)
                    throws org.apache.http.ProtocolException {
                return null;
            }
        });

        httpclient.execute(get);
        return builder.toString();
    }

    public static String doPostString(String strToUpload, String urlToPost,
                                      String method) {

        HttpURLConnection connection = null;
        ByteArrayOutputStream baos = null;
        InputStream dis = null;
        OutputStream dos = null;
        DataOutputStream dout = null;

        try {

            connection = getHttpURLConnection(urlToPost);

            // This will setup the connection as a POST
            connection.setDoOutput(true);

            strToUpload = TextHelper.removeDiacritic(strToUpload);
            int byteSize = (int) strToUpload.length();

			/*
             * Important to avoid out of memeory exceptions or crashes to set an
			 * actual Streaming fixed size. In this case we know the actual size
			 * of the Streaming request.
			 */
            connection.setFixedLengthStreamingMode(byteSize);

            connection.setRequestMethod(method);
            // microsoft set form-data for sending data to a server
            // boundary simple defines the limits of the body content
            // Content-Length very important to set it up with the correct size
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length",
                    String.valueOf(byteSize));

            if (strToUpload != null) {
                dos = connection.getOutputStream();
                dout = new DataOutputStream(dos);
                dout.write(strToUpload.getBytes());
            }

			/*
             * start reading the response from the server that is written to the
			 * InputStream
			 */
            baos = new ByteArrayOutputStream();
            dis = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int readed = 0;
            while ((readed = dis.read(buffer)) != -1) {
                baos.write(buffer, 0, readed);
            }

            return new String(baos.toByteArray(), "utf-8");

        } catch (IOException e) {
            try {
                if (dos == null) {
                    if (e != null)
                        Log.e(TAG,
                                "OutputStream not created: return null, possibly no access token");
                    return null;
                }

                if (connection.getResponseMessage() != null) {
                    if (e != null)
                        Log.e(TAG,
                                connection != null ? connection
                                        .getResponseMessage()
                                        : "IOException in doPostMultipart");

                } else {
                    if (e != null) {
                        Log.e(TAG,
                                "IOException getResponseMessage() is null at the moment");
                        if (e.getCause() != null)
                            Log.e(TAG,
                                    "Checking IOExcpetion e = " + e.getCause());
                    }
                }

                return null;
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            if (e != null)
                Log.e(TAG, e.getMessage());
            return null;
        } finally {
            try {
                if (dis != null)
                    dis.close();
                if (baos != null)
                    baos.close();
                if (connection != null)
                    connection.disconnect();
                if (dos != null)
                    dos.close();
                // dout is closed when dos is closed, hence simply set null to
                // dout and let the
                // garbage collector to get rid of it. dout.close() will create
                // an exception
                if (dout != null)
                    dout = null;

            } catch (IOException e) {
                if (e != null)
                    Log.e(TAG, e.getMessage());
                return null;
            }
        }

    }

    public static HttpURLConnection getHttpURLConnection(String urlStr)
            throws NoSuchAlgorithmException, KeyManagementException,
            IOException {
        HttpURLConnection connection;
        URL url = new URL(urlStr);

        if ("https".equalsIgnoreCase(url.getProtocol())) {

            // Trust all certificates due to issues in SSL when trying to do an
            // OutputStream later on the code.

            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {

                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {

                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            connection = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) connection)
                    .setSSLSocketFactory(sslSocketFactory);
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }

        return connection;
    }

}

