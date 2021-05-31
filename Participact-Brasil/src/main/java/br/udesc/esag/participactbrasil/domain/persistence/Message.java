package br.udesc.esag.participactbrasil.domain.persistence;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by fabiobergmann on 23/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

    private Long id;
    private String text;

    public Message() {
    }

    public Message(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
