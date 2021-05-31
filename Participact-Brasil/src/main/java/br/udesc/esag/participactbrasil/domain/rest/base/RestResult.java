package br.udesc.esag.participactbrasil.domain.rest.base;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by fabiobergmann on 10/6/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RestResult {

    private Boolean status;
    private String message;
    private Integer code;

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public Boolean isSuccess() {
        return status != null && status;
    }

}
