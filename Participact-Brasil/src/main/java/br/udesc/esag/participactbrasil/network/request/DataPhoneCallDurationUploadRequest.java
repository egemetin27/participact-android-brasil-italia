package br.udesc.esag.participactbrasil.network.request;

import android.content.ContentValues;
import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.most.persistence.DBAdapter;
import org.most.pipeline.PipelinePhoneCallDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.ContentValuesToProto;

public class DataPhoneCallDurationUploadRequest extends ApacheHttpSpiceRequest<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(DataPhoneCallDurationUploadRequest.class);

    private final static String RELATIVE_URL = "phonecallduration";
    private final static String TABLE = PipelinePhoneCallDuration.TBL_PHONE_CALL_DURATION;

    String key;
    List<ContentValues> data;

    public DataPhoneCallDurationUploadRequest(Context context, List<ContentValues> data) {
        super(context, ResponseMessage.class);
        this.data = data;
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {

        HttpClient httpClient = getHttpClient();
        HttpPost httppost = getHttpPost(ParticipActConfiguration.RESULT_DATA_URL + RELATIVE_URL);
        ByteArrayEntity entity = new ByteArrayEntity(ContentValuesToProto.convertToDataPhoneCallDurationProto(data).toByteArray());

        // Log.i("Sending:", new String(dataList.toByteArray()) +" with key: " +
        // getKey());
        httppost.setEntity(entity);

        logger.info("Executing request {}.", httppost.getRequestLine());
        HttpResponse response = httpClient.execute(httppost);
        logger.info("RestResult: {}", response.getStatusLine());

        ResponseMessage responseMes = new ObjectMapper().readValue(response.getEntity().getContent(), ResponseMessage.class);
        logger.info("Result: {} key {}", responseMes.getResultCode(), responseMes.getKey());

        DBAdapter adapter = null;
        int deleted = 0;
        switch (responseMes.getResultCode()) {
            case ResponseMessage.RESULT_OK:
                // delete
                adapter = DBAdapter.getInstance(getContext());
                logger.info("Deleting tuples from {} table", TABLE);
                deleted = adapter.deleteTuples(TABLE, ContentValuesToProto.getIds(data));
                break;
            case ResponseMessage.DATA_ALREADY_ON_SERVER:
                adapter = DBAdapter.getInstance(getContext());
                logger.info("Deleting tuples from {} table", TABLE);
                deleted = adapter.deleteTuples(TABLE, ContentValuesToProto.getIds(data));
                break;
            case ResponseMessage.DATA_NOT_REQUIRED:
                adapter = DBAdapter.getInstance(getContext());
                logger.info("Deleting tuples from {} table", TABLE);
                deleted = adapter.deleteTuples(TABLE, ContentValuesToProto.getIds(data));
                break;
            default:
                break;
        }
        logger.info("Upload request completed, Successfully deleted {} tuples.", deleted);
        return responseMes;
    }

}
