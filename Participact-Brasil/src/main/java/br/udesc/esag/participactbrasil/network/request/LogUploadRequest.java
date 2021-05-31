package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;

public class LogUploadRequest extends ApacheHttpSpiceRequest<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(LogUploadRequest.class);

    private final static String RELATIVE_URL = "log";
    private final static String KEY = "logRequestKey";

    private String logFilename;

    public LogUploadRequest(Context context, String filename) {
        super(context, ResponseMessage.class);
        this.logFilename = filename;
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {

        File file = new File(context.getExternalFilesDir(null) + "/" + logFilename + ".log");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALIAN);
        String fileName = formatter.format(new Date(file.lastModified()));

        HttpClient httpClient = getHttpClient();
        HttpPost httppost = getHttpPost(ParticipActConfiguration.RESULT_DATA_URL + RELATIVE_URL + "/" + fileName + "_" + logFilename);

        byte[] logBytes = compress(context.getExternalFilesDir(null) + "/" + logFilename + ".log");
        ByteArrayEntity entity = new ByteArrayEntity(logBytes);
        httppost.setEntity(entity);

        logger.info("Executing request {}.", httppost.getRequestLine());
        HttpResponse response = httpClient.execute(httppost);
        logger.info("RestResult: {}", response.getStatusLine());

        ResponseMessage responseMes = new ObjectMapper().readValue(response.getEntity().getContent(), ResponseMessage.class);
        logger.info("Result: {} key {}", responseMes.getResultCode(), responseMes.getKey());

        switch (responseMes.getResultCode()) {
            case ResponseMessage.RESULT_OK:
                logger.info("Log {} successfully uploaded", logFilename);
                break;
            default:
                break;
        }

        return responseMes;
    }

    @Override
    public String getKey() {
        return KEY + logFilename;
    }

    public static byte[] compress(String path) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(os);
        FileInputStream fi = new FileInputStream(path);
        int buffer = 2048;
        byte data[] = new byte[buffer];
        BufferedInputStream origin = new BufferedInputStream(fi, buffer);
        int count;
        while ((count = origin.read(data, 0, buffer)) != -1) {
            gos.write(data, 0, count);
        }
        origin.close();
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

}
