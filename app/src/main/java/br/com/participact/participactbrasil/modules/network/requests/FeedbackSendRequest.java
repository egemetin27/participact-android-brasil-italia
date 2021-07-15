package br.com.participact.participactbrasil.modules.network.requests;

public class FeedbackSendRequest {

    Long feedbackTypeId;
    String comment;

    public FeedbackSendRequest(Long feedbackTypeId, String comment) {
        this.feedbackTypeId = feedbackTypeId;
        this.comment = comment;
    }
}
