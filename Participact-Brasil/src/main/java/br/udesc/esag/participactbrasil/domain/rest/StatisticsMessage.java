package br.udesc.esag.participactbrasil.domain.rest;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class StatisticsMessage implements Serializable {

    private static final long serialVersionUID = -7678989653187505735L;

    List<Properties> list;

    public StatisticsMessage() {
        list = new LinkedList<Properties>();
    }

    public List<Properties> getList() {
        return list;
    }

    public void setList(List<Properties> list) {
        this.list = list;
    }

}
