package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderStatePreferences;

public class StateUploadRequest extends ApacheHttpSpiceRequest<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(StateUploadRequest.class);

    private final static String RELATIVE_URL = "log";
    private final static String KEY = "stateRequestKey";

    public StateUploadRequest(Context context) {
        super(context, ResponseMessage.class);
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALIAN);
        String fileName = formatter.format(new Date());

        HttpClient httpClient = getHttpClient();
        HttpPost httppost = getHttpPost(ParticipActConfiguration.RESULT_DATA_URL + RELATIVE_URL + "/" + fileName + "_state");

        State state = StateUtility.loadState(context);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        PrintWriter print = new PrintWriter(gzipOut);
        if (state != null) {

            logger.info("STATUS");
            for (Entry<Long, TaskStatus> task : state.getTasks().entrySet()) {

                logger.info(String.format("Task id: %d", task.getKey()));
                logger.info(String.format("Task state: %s", task.getValue().getState().toString()));
                if (task.getValue().getAcceptedTime() == null) {
                    logger.info(String.format("Accepted Time: not accepted"));
                } else {
                    logger.info(String.format("Accepted Time: %s", task.getValue().getAcceptedTime().toString()));
                }
                logger.info(String.format("Last checked timestamp: %s", new DateTime(task.getValue().getLastCheckedTimestamp())));
                logger.info(String.format("Sensing progress: %d", task.getValue().getSensingProgress()));
                logger.info(String.format("Photo progress: %d", task.getValue().getPhotoProgress()));
                logger.info(String.format("Questionnaire progress: %d", task.getValue().getQuestionnaireProgress()));
                logger.info("--------------------------");

            }
            logger.info("END STATUS");

        } else {
            logger.info("NO STATUS");
        }
        print.close();

        byte[] logBytes = baos.toByteArray();
        ByteArrayEntity entity = new ByteArrayEntity(logBytes);
        httppost.setEntity(entity);

        logger.info("Executing request {}.", httppost.getRequestLine());
        HttpResponse response = httpClient.execute(httppost);
        logger.info("RestResult: {}", response.getStatusLine());

        ResponseMessage responseMes = new ObjectMapper().readValue(response.getEntity().getContent(), ResponseMessage.class);
        logger.info("Result: {} key {}", responseMes.getResultCode(), responseMes.getKey());

        switch (responseMes.getResultCode()) {
            case ResponseMessage.RESULT_OK:
                logger.info("State successfully uploaded");
                DataUploaderStatePreferences.getInstance(context).setStateUpload(false);
                break;
            default:
                break;
        }

        return responseMes;
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
