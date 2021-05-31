package br.udesc.esag.participactbrasil.domain.persistence;

import java.io.Serializable;


public abstract class Action implements Serializable {

    private static final long serialVersionUID = 7569398216122665190L;

    public Action(){}

    private Long id;

    private String name;

    private Integer numeric_threshold;

    private Integer duration_threshold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumeric_threshold() {
        return numeric_threshold;
    }

    public void setNumeric_threshold(Integer numeric_threshold) {
        this.numeric_threshold = numeric_threshold;
    }

    public Integer getDuration_threshold() {
        return duration_threshold;
    }

    public void setDuration_threshold(Integer duration_threshold) {
        this.duration_threshold = duration_threshold;
    }

    public ActionFlat convertToActionFlat() {
        return new ActionFlat(this);
    }
}
