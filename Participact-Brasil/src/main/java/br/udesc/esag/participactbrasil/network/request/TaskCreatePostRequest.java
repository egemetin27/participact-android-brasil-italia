package br.udesc.esag.participactbrasil.network.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.rest.TaskFlatRequest;

/**
 * Created by alessandro on 24/11/14.
 */
public class TaskCreatePostRequest extends SpringAndroidSpiceRequest<TaskFlat> {

    private TaskFlatRequest newTaskFlatRequest;

    public TaskCreatePostRequest(TaskFlatRequest newTaskFlatRequest) {
        super(TaskFlat.class);
        this.newTaskFlatRequest = newTaskFlatRequest;
    }

    @Override
    public TaskFlat loadDataFromNetwork() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));

        //mock taskflatrequest

        //this.newTaskFlatRequest = mockTaskflatRequest();
        // TaskFlatRequest prova = mockProva();
        HttpEntity<TaskFlatRequest> requestEntity = new HttpEntity<TaskFlatRequest>(newTaskFlatRequest, requestHeaders);

        RestTemplate restTemplate = getRestTemplate();

        // Add the Jackson and String message converters
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP POST request, marshaling the request to JSON, and the response to a TaskFlat
        ResponseEntity<TaskFlat> responseEntity = restTemplate.exchange(ParticipActConfiguration.CREATE_TASK_URL, HttpMethod.POST, requestEntity, TaskFlat.class);
        return responseEntity.getBody();
    }


}
