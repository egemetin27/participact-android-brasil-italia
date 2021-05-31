package br.udesc.esag.participactbrasil.domain.data;


import org.joda.time.DateTime;

import java.io.Serializable;

public abstract class Data implements Serializable {

    private static final long serialVersionUID = -7022941042935851846L;

    private Long id;

    private long sampleTimestamp;

    private DateTime dataReceivedTimestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSampleTimestamp() {
        return sampleTimestamp;
    }

    public void setSampleTimestamp(long sampleTimestamp) {
        this.sampleTimestamp = sampleTimestamp;
    }

    public DateTime getDataReceivedTimestamp() {
        return dataReceivedTimestamp;
    }

    public void setDataReceivedTimestamp(DateTime dataReceivedTimestamp) {
        this.dataReceivedTimestamp = dataReceivedTimestamp;
    }

}
