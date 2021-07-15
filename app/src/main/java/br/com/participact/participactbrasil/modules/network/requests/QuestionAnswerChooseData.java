package br.com.participact.participactbrasil.modules.network.requests;

public class QuestionAnswerChooseData {

    private final Long answerGroupId;
    Long actionId;
    Long taskId;
    Integer type = 1;
    Long questionId;
    String answerId;
    Long timestamp = System.currentTimeMillis() / 1000;
    String ipAddress;

    public QuestionAnswerChooseData(Long actionId, Long taskId, Long questionId, String answerId, String ipAddress, Long answerGroupId) {
        this.actionId = actionId;
        this.taskId = taskId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.ipAddress = ipAddress;
        this.answerGroupId = answerGroupId;
    }
}
