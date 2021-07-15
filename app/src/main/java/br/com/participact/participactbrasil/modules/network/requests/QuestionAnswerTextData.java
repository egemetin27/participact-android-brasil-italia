package br.com.participact.participactbrasil.modules.network.requests;

public class QuestionAnswerTextData {
    private final Long answerGroupId;
    Long actionId;
    Long taskId;
    Integer type = 0;
    Long questionId;
    String openAnswerValue;
    Long timestamp = System.currentTimeMillis() / 1000;
    String ipAddress;

    public QuestionAnswerTextData(Long actionId, Long taskId, Long questionId, String openAnswerValue, String ipAddress, Long answerGroupId) {
        this.actionId = actionId;
        this.taskId = taskId;
        this.questionId = questionId;
        this.openAnswerValue = openAnswerValue;
        this.ipAddress = ipAddress;
        this.answerGroupId = answerGroupId;
    }
}