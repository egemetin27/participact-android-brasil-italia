package com.bergmannsoft.greendao;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class Main {

    public static void main(String[] a) throws Exception {

        Schema schema = new Schema(39, "br.com.participact.participactbrasil.modules.db");

//        Entity ticket = schema.addEntity("UrbanProblem");
//        ticket.addIdProperty();
//        ticket.addStringProperty("comment");
//        ticket.addDoubleProperty("latitude");
//        ticket.addDoubleProperty("longitude");
//        ticket.addStringProperty("creationDate");
//
//        Entity category = schema.addEntity("UPCategory");
//
//        Entity subcategory = schema.addEntity("UPSubcategory");
//        subcategory.addIdProperty();
//        subcategory.addLongProperty("categoryId");

        Entity upFile = schema.addEntity("UPFileDB");
        upFile.addIdProperty().autoincrement();
        upFile.addStringProperty("filename");
        upFile.addStringProperty("filePath");
        upFile.addLongProperty("reportId");
        upFile.addIntProperty("duration");
        upFile.addStringProperty("type");
        upFile.addBooleanProperty("uploaded");
        upFile.addDateProperty("dateUploaded");

        Entity campaign = schema.addEntity("Campaign");
        campaign.setCodeBeforeClass("import com.google.gson.annotations.SerializedName;");
        campaign.addIdProperty();
        campaign.addStringProperty("name");
        campaign.addStringProperty("text").codeBeforeField("@SerializedName(\"description\")");
        campaign.addStringProperty("startDateString").codeBeforeField("@SerializedName(\"start\")");
        campaign.addStringProperty("deadlineDateString").codeBeforeField("@SerializedName(\"deadline\")");
        campaign.addLongProperty("duration");
        campaign.addLongProperty("sensingDuration");
        campaign.addBooleanProperty("refusable").codeBeforeField("@SerializedName(\"canBeRefused\")");
        campaign.addStringProperty("notificationArea");
        campaign.addStringProperty("activationArea");
        campaign.addStringProperty("agreement");
        campaign.addBooleanProperty("sensingWeekSun");
        campaign.addBooleanProperty("sensingWeekMon");
        campaign.addBooleanProperty("sensingWeekTue");
        campaign.addBooleanProperty("sensingWeekWed");
        campaign.addBooleanProperty("sensingWeekThu");
        campaign.addBooleanProperty("sensingWeekFri");
        campaign.addBooleanProperty("sensingWeekSat");
        campaign.addBooleanProperty("cardOpen");
        campaign.addStringProperty("rawState");
        campaign.addStringProperty("cardColor").codeBeforeField("@SerializedName(\"color\")");
        campaign.addStringProperty("cardIconUrl").codeBeforeField("@SerializedName(\"iconUrl\")");
        campaign.addBooleanProperty("agreementAccepted");
        campaign.addBooleanProperty("archived");
        campaign.addIntProperty("archivedCount");

        Entity campaignStatus = schema.addEntity("CampaignStatus");
        campaignStatus.addLongProperty("campaignId").primaryKey();
        campaignStatus.addBooleanProperty("ended");
        campaignStatus.addBooleanProperty("completed");
        campaignStatus.addIntProperty("campaignProgress");
        campaignStatus.addIntProperty("campaignDateProgress");
        /*
        boolean isEnded = campaignWrapper.isEnded();
        boolean isCompleted = campaignWrapper.isCompleted();
        int progress = campaignWrapper.getProgress();
        int dateProgress = campaignWrapper.percentDateProgress();
         */

        Entity action = schema.addEntity("Action");
        action.setCodeBeforeClass("import com.google.gson.annotations.SerializedName;");
        action.addIdProperty();
        action.addStringProperty("name");
        action.addIntProperty("minimum").codeBeforeField("@SerializedName(\"numeric_threshold\")");
        action.addIntProperty("inputType").codeBeforeField("@SerializedName(\"input_type\")");
        action.addStringProperty("type");
        action.addStringProperty("title");
        action.addLongProperty("sensorDuration");
        action.addStringProperty("actionDescription").codeBeforeField("@SerializedName(\"description\")");
        Property campaignId = action.addLongProperty("campaignId").getProperty();
        action.addBooleanProperty("sensorWeekSun");
        action.addBooleanProperty("sensorWeekMon");
        action.addBooleanProperty("sensorWeekTue");
        action.addBooleanProperty("sensorWeekWed");
        action.addBooleanProperty("sensorWeekThu");
        action.addBooleanProperty("sensorWeekFri");
        action.addBooleanProperty("sensorWeekSat");
        action.addBooleanProperty("repeat"); // Indicates if the questionnaire can be replied more than once.

        campaign.addToMany(action, campaignId, "actions");

        Entity question = schema.addEntity("Question");
        question.setCodeBeforeClass("import com.google.gson.annotations.SerializedName;");
        question.addIdProperty();
        question.addStringProperty("question");
        question.addBooleanProperty("closedAnswers").codeBeforeField("@SerializedName(\"isClosedAnswers\")").javaDocField("This is the MultiChoice (múltipla escolha). User chooses only one option.");
        question.addBooleanProperty("multipleSelect").codeBeforeField("@SerializedName(\"isMultipleAnswers\")").javaDocField("This is the Checkboxes (caixas de seleção). User can choose one or more options.");
        question.addIntProperty("order").codeBeforeField("@SerializedName(\"questionOrder\")");
        question.addStringProperty("answer");
        question.addStringProperty("answerIds");
        question.addLongProperty("campaignId");
        Property actionId = question.addLongProperty("actionId").getProperty();
        question.addBooleanProperty("readyToUpload");
        question.addBooleanProperty("uploaded");
        question.addBooleanProperty("required"); // Indicates if the question is mandatory or not.
        question.addBooleanProperty("skipped"); // Indicates if the question was skipped.
        question.addIntProperty("numberPhotos"); // Indicates the number of photos to take.
        question.addBooleanProperty("photo"); // Indicates if the question is to take photos.
        question.addLongProperty("targetId"); // If bigger than zero indicates the next question.
        question.addDateProperty("answerDate");
        question.addStringProperty("ipAddress");
        question.addBooleanProperty("isDate");
        question.addLongProperty("answerGroupId");

        action.addToMany(question, actionId, "questions");

        Entity questionOption = schema.addEntity("QuestionOption");
        questionOption.setCodeBeforeClass("import com.google.gson.annotations.SerializedName;");
        questionOption.addIdProperty();
        questionOption.addStringProperty("option").codeBeforeField("@SerializedName(\"answerDescription\")");
        questionOption.addIntProperty("order").codeBeforeField("@SerializedName(\"answerOrder\")");
        questionOption.addLongProperty("targetId"); // If bigger than zero indicates the next question.
        Property questionId = questionOption.addLongProperty("questionId").getProperty();

        question.addToMany(questionOption, questionId, "closed_answers");

        Entity questionAnswer = schema.addEntity("QuestionAnswer");
        questionAnswer.addIdProperty().autoincrement();
        questionAnswer.addStringProperty("answer");
        questionAnswer.addStringProperty("answerIds");
        questionAnswer.addLongProperty("campaignId");
        questionAnswer.addLongProperty("questionId");
        questionAnswer.addLongProperty("actionId");
        questionAnswer.addBooleanProperty("readyToUpload");
        questionAnswer.addStringProperty("ipAddress");
        questionAnswer.addLongProperty("answerGroupId");
        questionAnswer.addStringProperty("filename");
        questionAnswer.addDoubleProperty("latitude");
        questionAnswer.addDoubleProperty("longitude");
        questionAnswer.addStringProperty("provider"); // GPS ou IP
        questionAnswer.addBooleanProperty("photo");

        Entity sensor = schema.addEntity("Sensor");
        sensor.addIdProperty().autoincrement();
        sensor.addIntProperty("pipelineTypeValue");
        sensor.addStringProperty("data");
        sensor.addDateProperty("dateWhen");
        sensor.addBooleanProperty("uploaded");
        sensor.addDateProperty("dateUploaded");

        Entity upsensor = schema.addEntity("UPSensor");
        upsensor.addIdProperty().autoincrement();
        upsensor.addIntProperty("pipelineTypeValue");
        upsensor.addStringProperty("data");
        upsensor.addDateProperty("dateWhen");
        upsensor.addBooleanProperty("uploaded");
        upsensor.addDateProperty("dateUploaded");

        Entity photo = schema.addEntity("Photo");
        photo.addIdProperty().autoincrement();
        photo.addStringProperty("filename");
        photo.addDoubleProperty("latitude");
        photo.addDoubleProperty("longitude");
        photo.addStringProperty("provider"); // GPS ou IP
        photo.addBooleanProperty("uploaded");
        photo.addBooleanProperty("readyToUpload");
        photo.addLongProperty("questionId"); // Se for de uma questão de foto
        photo.addLongProperty("actionId");
        photo.addLongProperty("campaignId");
        photo.addDateProperty("dateUploaded");
        photo.addStringProperty("ipAddress");
        photo.addLongProperty("answerGroupId");

        Entity log = schema.addEntity("Log");
        log.addIdProperty().autoincrement();
        log.addStringProperty("message");
        log.addStringProperty("user");

        Entity notification = schema.addEntity("PANotification");
        notification.addIdProperty();
        notification.addStringProperty("message");
        notification.addBooleanProperty("read");

        Entity abuseType = schema.addEntity("AbuseType");
        abuseType.setCodeBeforeClass("import com.google.gson.annotations.SerializedName;");
        abuseType.addIdProperty();
        abuseType.addStringProperty("text").codeBeforeField("@SerializedName(\"name\")");
        abuseType.addBooleanProperty("selected");

        Entity pendingRequest = schema.addEntity("PendingRequest");
        pendingRequest.addIdProperty().autoincrement();
        pendingRequest.addLongProperty("campaignId");
        pendingRequest.addStringProperty("requestType");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }

}
