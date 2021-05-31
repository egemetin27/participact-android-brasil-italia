package br.udesc.esag.participactbrasil;

public class ParticipActConfiguration {


    //	server key AIzaSyAGHmaTa_RXnIYFpHaR0kI1JYht_VZXaJ4
    public static final String GCM_SENDER_ID = "728491275474";

//    public static final String GCM_SENDER_ID = "182762764792"; // bergmannsoft


    //Default lifespan (7 days) of a reservation until it is considered expired.
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

    public static final int PHOTO_REQUEST_CODE = 12213;
    public static final int QUESTIONNAIRE_REQUEST_CODE = 13333;

    //  public static final String BASE_URL = "http://200.19.102.84:8080/participact-server/rest/";
    //public static final String BASE_URL = "https://pabrain.ing.unibo.it:8443/participact-server/rest/";
    //public static final String BASE_URL = "https://200.98.147.60:8443/participact-server/rest/";
//    public static final String BASE_URL = "https://pa.labges.esag.udesc.br:8443/participact-server/rest/"; // PRODUCTION

    // DEV ENVIRONMENT AZURE
//    public static final String BASE_URL = "http://191.232.234.245:8080/participact-server/rest/"; // AZURE
//    public static final String BASE_URL_V2 = "http://191.232.234.245:8080/participact-server/api/v2/public/"; // AZURE
    public static final String BASE_URL = "http://api.participact.com.br:8080/participact-server/rest/"; // AZURE
    public static final String BASE_URL_V2 = "http://api.participact.com.br:8080/participact-server/api/v2/public/"; // AZURE

    // LOCAL ENVIRONMENT TEST
//    public static final String BASE_URL = "http://192.168.0.15:8080/participact-server/rest/"; // LOCAL
//    public static final String BASE_URL_V2 = "http://192.168.0.15:8080/participact-server/api/v2/public/"; // LOCAL
//    public static final String BASE_URL = "http://192.168.1.101:8080/participact-server/rest/"; // LOCAL
//    public static final String BASE_URL_V2 = "http://192.168.1.101:8080/participact-server/api/v2/public/"; // LOCAL

    // public static final String BASE_URL = "http://137.204.45.201:8080/participact-server/rest/";

    public static final int VERSION = 25;

    public static final String BASE_PARAMETER = "version=" + VERSION;

    public static final String LOGIN_URL = BASE_URL + "login?" + BASE_PARAMETER;

    public static final String ME_URL = BASE_URL + "user/me";

    public static final String NOTIFICATIONS_URL = BASE_URL + "me/messages";

    public static final String PROFILE_EDIT_PHOTO = BASE_URL + "profile/photo";

    public static final String STATISTICS2_URL = BASE_URL + "statistics/general/";

    public static final String PROFILE_UPDATE_URL = BASE_URL + "profile/update";

    public static final String PAGES_URL = BASE_URL_V2 + "pages";

    public static final String SIGNUP_URL = BASE_URL_V2 + "signup?" + BASE_PARAMETER;

    public static final String FORGOT_URL = BASE_URL_V2 + "forgot?" + BASE_PARAMETER;

    public static final String GCM_REGISTER_URL = BASE_URL + "registergcm?" + BASE_PARAMETER + "&gcmid={gcmid}";

    public static final String GCM_UNREGISTER_URL = BASE_URL + "unregistergcm?" + BASE_PARAMETER;

    public static final String TASK_URL = BASE_URL + "taskflat?" + BASE_PARAMETER + "&type={type}&state={state}";
    // public static final String TASK_URL = BASE_URL + "taskflat?" + BASE_PARAMETER ;

    public static final String CREATE_TASK_URL = BASE_URL + "taskflat";

    public static final String CREATED_TASK_STATE_URL = BASE_URL + "createdTaskflat/{state}";

    public static final String CREATED_TASK_URL = BASE_URL + "createdTaskflat?valutation={valutation}";

    public static final String ACCEPT_TASK_URL = BASE_URL + "acceptTask?" + BASE_PARAMETER + "&taskId={taskId}";

    public static final String REJECT_TASK_URL = BASE_URL + "rejectTask?" + BASE_PARAMETER + "&taskId={taskId}";

    public static final String COMPLETE_WITH_SUCCESS_TASK_URL = BASE_URL + "completeTask?" + BASE_PARAMETER + "&taskId={taskId}";

    public static final String COMPLETE_WITH_FAILURE_TASK_URL = BASE_URL + "completeTaskWithFailure?" + BASE_PARAMETER + "&taskId={taskId}&sensingProgress={sensingProgress}&photoProgress={photoProgress}&questionnaireProgress={questionnaireProgress}";

    public static final String RESULT_DATA_URL = BASE_URL + "upload/";

    public static final String STATISTICS_URL = BASE_URL + "statistics?" + BASE_PARAMETER;

    public static final String CHECK_CLIENT_APP_VERSION = BASE_URL + "clientversion?" + BASE_PARAMETER;

    public static final String TWITTER_URL = BASE_URL + "twitter?" + BASE_PARAMETER;

    public static final String LEADERBOARD_URL = BASE_URL + "leaderboard?type={type}";

    public static final String USER_URL = BASE_URL + "user/{id}";

    public static final String BADGES_FOR_USER_URL = BASE_URL + "badges/user/{id}";

    public static final String FRIENDS_GET_URL = BASE_URL + "user/friends?status={status}";

    public static final String FRIENDS_POST_URL = BASE_URL + "user/friends/{id}";

    public static final String FRIEND_STATUS_URL = BASE_URL + "user/friends/{id}";

    public static final String SOCIAL_PRESENCE_ADD_URL = BASE_URL + "user/social/{socialnetwork}";

    public static final String SOCIAL_PRESENCE_GET_FIENDS_URL = BASE_URL + "user/social/{socialnetwork}/friends";

}
