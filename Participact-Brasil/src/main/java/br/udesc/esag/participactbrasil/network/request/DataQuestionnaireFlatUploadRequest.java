package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.persistence.DataQuestionnaireFlat;
import br.udesc.esag.participactbrasil.domain.persistence.support.DomainDBHelper;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.ContentValuesToProto;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderQuestionnairePreferences;

public class DataQuestionnaireFlatUploadRequest extends ApacheHttpSpiceRequest<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(DataQuestionnaireFlatUploadRequest.class);

    private final static String RELATIVE_URL = "question";

    String key;
    List<DataQuestionnaireFlat> data;
    Context context;

    public DataQuestionnaireFlatUploadRequest(Context context, List<DataQuestionnaireFlat> data) {
        super(context, ResponseMessage.class);
        this.data = data;
        this.context = context;
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {

        HttpClient httpClient = getHttpClient();
        HttpPost httppost = getHttpPost(ParticipActConfiguration.RESULT_DATA_URL + RELATIVE_URL);

        ByteArrayEntity entity = new ByteArrayEntity(ContentValuesToProto.convertToDataQuestionnaireFlatProto(data).toByteArray());

        // Log.i("Sending:", new String(dataList.toByteArray()) +" with key: " +
        // getKey());
        httppost.setEntity(entity);

        logger.info("Executing request {}.", httppost.getRequestLine());
        HttpResponse response = httpClient.execute(httppost);
        logger.info("RestResult: {}", response.getStatusLine());

        ResponseMessage responseMes = new ObjectMapper().readValue(response.getEntity().getContent(), ResponseMessage.class);
        logger.info("Result: {} key {}", responseMes.getResultCode(), responseMes.getKey());
        Log.d("DataQuestionary", "Result: "+responseMes.getResultCode()+" key "+responseMes.getKey());


        DataUploaderQuestionnairePreferences.getInstance(context).setQuestionnaireUpload(false);
        return responseMes;
    }

}
