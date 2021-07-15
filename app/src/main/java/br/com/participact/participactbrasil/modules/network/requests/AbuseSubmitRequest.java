package br.com.participact.participactbrasil.modules.network.requests;

public class AbuseSubmitRequest {

    Long reportId;
    Long typeId;
    String comment = "";

    public AbuseSubmitRequest(Long reportId, Long typeId) {
        this.reportId = reportId;
        this.typeId = typeId;
    }
}
