package br.com.participact.participactbrasil.modules.network;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.bergmannsoft.rest.Request;
import com.bergmannsoft.rest.Response;
import com.bergmannsoft.retrofit.ProgressRequestBody;
import com.bergmannsoft.util.ConnectionUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.Question;
import br.com.participact.participactbrasil.modules.db.QuestionAnswer;
import br.com.participact.participactbrasil.modules.db.QuestionAnswerWrapper;
import br.com.participact.participactbrasil.modules.db.QuestionWrapper;
import br.com.participact.participactbrasil.modules.db.Sensor;
import br.com.participact.participactbrasil.modules.db.SensorDaoImpl;
import br.com.participact.participactbrasil.modules.db.UPSensor;
import br.com.participact.participactbrasil.modules.db.UPSensorDaoImpl;
import br.com.participact.participactbrasil.modules.enums.Constants;
import br.com.participact.participactbrasil.modules.network.api.AccountAPI;
import br.com.participact.participactbrasil.modules.network.api.CampaignsAPI;
import br.com.participact.participactbrasil.modules.network.api.FeedbackAPI;
import br.com.participact.participactbrasil.modules.network.api.GeneralAPI;
import br.com.participact.participactbrasil.modules.network.api.IPDataAPI;
import br.com.participact.participactbrasil.modules.network.api.IssueReportAPI;
import br.com.participact.participactbrasil.modules.network.api.LoggerAPI;
import br.com.participact.participactbrasil.modules.network.api.LoginAPI;
import br.com.participact.participactbrasil.modules.network.api.NotificationsAPI;
import br.com.participact.participactbrasil.modules.network.api.QRCodeAPI;
import br.com.participact.participactbrasil.modules.network.api.ReceiveDataAPI;
import br.com.participact.participactbrasil.modules.network.requests.AbuseSubmitRequest;
import br.com.participact.participactbrasil.modules.network.requests.AbuseTypeRequest;
import br.com.participact.participactbrasil.modules.network.requests.AbuseTypeResponse;
import br.com.participact.participactbrasil.modules.network.requests.CampaignsRequest;
import br.com.participact.participactbrasil.modules.network.requests.CampaignsResponse;
import br.com.participact.participactbrasil.modules.network.requests.EmptyBodyRequest;
import br.com.participact.participactbrasil.modules.network.requests.EndRequest;
import br.com.participact.participactbrasil.modules.network.requests.FeedbackSendRequest;
import br.com.participact.participactbrasil.modules.network.requests.FeedbackTypeResponse;
import br.com.participact.participactbrasil.modules.network.requests.IPDataResponse;
import br.com.participact.participactbrasil.modules.network.requests.MeResponse;
import br.com.participact.participactbrasil.modules.network.requests.NotificationsResponse;
import br.com.participact.participactbrasil.modules.network.requests.ParticipateRequest;
import br.com.participact.participactbrasil.modules.network.requests.ParticipateResponse;
import br.com.participact.participactbrasil.modules.network.requests.ProfileRequest;
import br.com.participact.participactbrasil.modules.network.requests.QRCodeRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerChooseRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerRequest;
import br.com.participact.participactbrasil.modules.network.requests.QuestionAnswerTextRequest;
import br.com.participact.participactbrasil.modules.network.requests.SendFacebookTokenRequest;
import br.com.participact.participactbrasil.modules.network.requests.SendGoogleTokenRequest;
import br.com.participact.participactbrasil.modules.network.requests.SendRegIdRequest;
import br.com.participact.participactbrasil.modules.network.requests.SignInRequest;
import br.com.participact.participactbrasil.modules.network.requests.SignInResponse;
import br.com.participact.participactbrasil.modules.network.requests.StatisticsRequest;
import br.com.participact.participactbrasil.modules.network.requests.StatisticsResponse;
import br.com.participact.participactbrasil.modules.network.requests.StorageFileRequest;
import br.com.participact.participactbrasil.modules.network.requests.UPCategoriesRequest;
import br.com.participact.participactbrasil.modules.network.requests.UPCategoriesResponse;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemSendRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemSendResponse;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsGPSSettingsRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsGPSSettingsResponse;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsRequest;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsResponse;
import br.com.participact.participactbrasil.modules.network.requests.sensing.DataRequest;
import br.com.participact.participactbrasil.modules.support.Tags;
import br.com.participact.participactbrasil.modules.support.UserSettings;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import org.most.pipeline.Pipeline;

public class SessionManager {

    public static final String API_URL = Constants.HOST;
    private static final String TAG = SessionManager.class.getSimpleName();
    private final Retrofit retrofit;

    private static SessionManager instance;

    private SessionManager() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public interface RequestCallback<T extends Response> {
        void onResponse(T response);

        void onFailure(Throwable t);
    }

    // region Auth

    public void signIn(final RequestCallback<SignInResponse> callback) {
        boolean guest = false;
        if (UserSettings.getInstance().getAccessToken() == null) {
            guest = true;
            String uuid = App.getInstance().getDeviceUuid();
            UserSettings.getInstance().setUsername(uuid + "@participact.com.br");
            UserSettings.getInstance().setPassword(uuid);
        }
        Crashlytics.setUserIdentifier(UserSettings.getInstance().getUsername());
        Crashlytics.setUserEmail(UserSettings.getInstance().getUsername());
        Crashlytics.setUserName(UserSettings.getInstance().getName());
        SignInRequest request = new SignInRequest(UserSettings.getInstance().getUsername(), UserSettings.getInstance().getPassword());
        LoginAPI api = retrofit.create(LoginAPI.class);
        Call<SignInResponse> call;
        if (guest) {
            call = api.guest(defaultHeaders(), request);
        } else {
            call = api.signIn(defaultHeaders(), request);
        }
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, retrofit2.Response<SignInResponse> response) {
                SignInResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, SignInResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void sendFacebookToken(String accessToken, final RequestCallback<Response> callback) {
        SendFacebookTokenRequest request = new SendFacebookTokenRequest(accessToken);
        AccountAPI api = retrofit.create(AccountAPI.class);
        Call<Response> call = api.facebookToken(defaultHeaders(), request);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void sendGoogleToken(String accessToken, final RequestCallback<Response> callback) {
        SendGoogleTokenRequest request = new SendGoogleTokenRequest(accessToken);
        AccountAPI api = retrofit.create(AccountAPI.class);
        Call<Response> call = api.googleToken(defaultHeaders(), request);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void saveProfile(ProfileRequest request, final RequestCallback<Response> callback) {
        AccountAPI api = retrofit.create(AccountAPI.class);
        Call<Response> call = api.updateProfile(defaultHeaders(), request);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void me(final RequestCallback<MeResponse> callback) {
        AccountAPI api = retrofit.create(AccountAPI.class);
        Call<MeResponse> call = api.me(defaultHeaders());
        call.enqueue(new Callback<MeResponse>() {
            @Override
            public void onResponse(Call<MeResponse> call, retrofit2.Response<MeResponse> response) {
                MeResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, MeResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<MeResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // endregion

    // region Campaigns

    public void campaigns(final RequestCallback<CampaignsResponse> callback) {
        CampaignsRequest request = new CampaignsRequest(UserSettings.getInstance().getCampaignsVersion());
        CampaignsAPI api = retrofit.create(CampaignsAPI.class);
        Call<CampaignsResponse> call = api.campaigns(defaultHeaders(), request);
        call.enqueue(new Callback<CampaignsResponse>() {
            @Override
            public void onResponse(Call<CampaignsResponse> call, retrofit2.Response<CampaignsResponse> response) {
                CampaignsResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, CampaignsResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<CampaignsResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void campaignsDeleted(final RequestCallback<CampaignsResponse> callback) {
        CampaignsRequest request = new CampaignsRequest(UserSettings.getInstance().getCampaignsVersionDeleted());
        CampaignsAPI api = retrofit.create(CampaignsAPI.class);
        Call<CampaignsResponse> call = api.campaignsDeleted(defaultHeaders(), request);
        call.enqueue(new Callback<CampaignsResponse>() {
            @Override
            public void onResponse(Call<CampaignsResponse> call, retrofit2.Response<CampaignsResponse> response) {
                CampaignsResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, CampaignsResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<CampaignsResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void confirmToken(String token, final RequestCallback<Response> callback) {
        QRCodeRequest request = new QRCodeRequest(token);
        QRCodeAPI api = retrofit.create(QRCodeAPI.class);
        Call<Response> call = api.confirmToken(defaultHeaders(), request);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void participate(Long campaignId, final RequestCallback<ParticipateResponse> callback) {
        ParticipateRequest request = new ParticipateRequest(campaignId);
        CampaignsAPI api = retrofit.create(CampaignsAPI.class);
        Call<ParticipateResponse> call = api.accept(defaultHeaders(), request);
        call.enqueue(new Callback<ParticipateResponse>() {
            @Override
            public void onResponse(Call<ParticipateResponse> call, retrofit2.Response<ParticipateResponse> response) {
                ParticipateResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, ParticipateResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<ParticipateResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void complete(Campaign campaign, final RequestCallback<Response> callback) {
        EndRequest request = EndRequest.create(campaign);
        CampaignsAPI api = retrofit.create(CampaignsAPI.class);
        Call<Response> call = api.complete(defaultHeaders(), request);
        if (!CampaignWrapper.wrap(campaign).isStatusCompleted()) {
            call = api.incomplete(defaultHeaders(), request);
        }
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void sendSensingData(final Pipeline.Type type, final RequestCallback<Response> callback) {

        if (UserSettings.getInstance().getSendDataOnlyOverWifi()) {
            if (!ConnectionUtils.isWiFiConnected(App.getInstance().getApplicationContext())) {
                Log.d(TAG, "Won't send data. User chose to send data only over wifi.");
                callback.onResponse(null);
                return;
            }
        }

        final SensorDaoImpl dao = new SensorDaoImpl();
        final List<Sensor> entities = dao.findByPipelineType(type, 500);
        Log.d(TAG, "Sensor type " + type);
        if (entities != null && entities.size() > 0) {
            DataRequest request = DataRequest.create(entities);
            if (request.hasData()) {
                ReceiveDataAPI api = retrofit.create(ReceiveDataAPI.class);
                Call<Response> call = request.call(type, api, defaultHeaders());
                if (call != null) {
                    call.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, final retrofit2.Response<Response> response) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Response body = response.body();
                                    if (body == null) {
                                        body = getErrorResponse(response, Response.class);
                                    }
                                    if (body!=null&&body.isSuccess()) {
                                        for (Sensor entity : entities) {
                                            dao.remove(entity);
                                        }
                                    }
                                    Log.d(TAG, "Sensor type " + type + " saved");
                                    entities.clear();
                                    callback.onResponse(body);
                                }
                            });
                            thread.setName("Set Uploaded Sensing");
                            thread.start();
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            entities.clear();
                            callback.onFailure(t);
                        }
                    });
                } else {
                    entities.clear();
                    callback.onResponse(null);
                }
            } else {
                entities.clear();
                callback.onResponse(null);
            }
        } else {
            callback.onResponse(null);
        }
    }

    public void sendUPSensingData(final RequestCallback<Response> callback) {

        if (UserSettings.getInstance().getSendDataOnlyOverWifi()) {
            if (!ConnectionUtils.isWiFiConnected(App.getInstance().getApplicationContext())) {
                Log.d(TAG, "Won't send data. User chose to send data only over wifi.");
                callback.onResponse(null);
                return;
            }
        }

        final UPSensorDaoImpl dao = new UPSensorDaoImpl();
        final List<UPSensor> entities = dao.findByPipelineType(500);
        if (entities != null && entities.size() > 0) {
            DataRequest request = DataRequest.createUp(entities);
            if (request.hasData()) {
                ReceiveDataAPI api = retrofit.create(ReceiveDataAPI.class);
                Call<Response> call = request.call(Pipeline.Type.LOCATION, api, defaultHeaders());
                if (call != null) {
                    call.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            Response body = response.body();
                            if (body == null) {
                                body = getErrorResponse(response, Response.class);
                            }
                            if (body!=null&&body.isSuccess()) {
                                for (UPSensor entity : entities) {
                                    dao.remove(entity);
                                }
                            }
                            entities.clear();
                            callback.onResponse(body);
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            entities.clear();
                            callback.onFailure(t);
                        }
                    });
                } else {
                    entities.clear();
                    callback.onResponse(null);
                }
            } else {
                entities.clear();
                callback.onResponse(null);
            }
        } else {
            callback.onResponse(null);
        }
    }

    public void sendQuestion(Question question, final RequestCallback<Response> callback) {

        if (UserSettings.getInstance().getSendDataOnlyOverWifi()) {
            if (!ConnectionUtils.isWiFiConnected(App.getInstance().getApplicationContext())) {
                Log.d(TAG, "Won't send data. User chose to send data only over wifi.");
                callback.onResponse(null);
                return;
            }
        }

        QuestionAnswerRequest request = QuestionWrapper.wrap(question).getRequest();
        ReceiveDataAPI api = retrofit.create(ReceiveDataAPI.class);
        Call<Response> call;
        if (QuestionWrapper.wrap(question).isText()) {
            call = api.questionText(defaultHeaders(), (QuestionAnswerTextRequest) request);
        } else {
            call = api.questionChoose(defaultHeaders(), (QuestionAnswerChooseRequest) request);
        }
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void sendQuestionAnswer(QuestionAnswer question, final RequestCallback<Response> callback) {

        if (UserSettings.getInstance().getSendDataOnlyOverWifi()) {
            if (!ConnectionUtils.isWiFiConnected(App.getInstance().getApplicationContext())) {
                Log.d(TAG, "Won't send data. User chose to send data only over wifi.");
                callback.onResponse(null);
                return;
            }
        }

        QuestionAnswerRequest request = QuestionAnswerWrapper.wrap(question).getRequest();
        ReceiveDataAPI api = retrofit.create(ReceiveDataAPI.class);
        Call<Response> call;
        if (QuestionAnswerWrapper.wrap(question).isText()) {
            call = api.questionText(defaultHeaders(), (QuestionAnswerTextRequest) request);
        } else {
            call = api.questionChoose(defaultHeaders(), (QuestionAnswerChooseRequest) request);
        }
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // endregion

    // region Urban Problems

    public void statistics(Double latitude, Double longitude, final RequestCallback<StatisticsResponse> callback) {
        StatisticsRequest request = new StatisticsRequest(latitude, longitude);
        IssueReportAPI api = retrofit.create(IssueReportAPI.class);
        Call<StatisticsResponse> call = api.statistics(defaultHeaders(), request);
        call.enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, retrofit2.Response<StatisticsResponse> response) {
                StatisticsResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, StatisticsResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void categories(final RequestCallback<UPCategoriesResponse> callback) {
        UPCategoriesRequest request = new UPCategoriesRequest();
        IssueReportAPI api = retrofit.create(IssueReportAPI.class);
        Call<UPCategoriesResponse> call = api.categories(defaultHeaders(), request);
        call.enqueue(new Callback<UPCategoriesResponse>() {
            @Override
            public void onResponse(Call<UPCategoriesResponse> call, retrofit2.Response<UPCategoriesResponse> response) {
                UPCategoriesResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, UPCategoriesResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<UPCategoriesResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void upSendReport(UrbanProblemSendRequest request, final RequestCallback<UrbanProblemSendResponse> callback) {
        IssueReportAPI api = retrofit.create(IssueReportAPI.class);
        Call<UrbanProblemSendResponse> call = api.submit(defaultHeaders(), request);
        call.enqueue(new Callback<UrbanProblemSendResponse>() {
            @Override
            public void onResponse(Call<UrbanProblemSendResponse> call, retrofit2.Response<UrbanProblemSendResponse> response) {
                UrbanProblemSendResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, UrbanProblemSendResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<UrbanProblemSendResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void urbanProblems(double latitude, double longitude, String startDate, String endDate, boolean mine, final RequestCallback<UrbanProblemsResponse> callback) {
        UrbanProblemsRequest request = new UrbanProblemsRequest(latitude, longitude, startDate, endDate, mine);
        IssueReportAPI api = retrofit.create(IssueReportAPI.class);
        Call<UrbanProblemsResponse> call = api.urbanProblems(defaultHeaders(), request);
        call.enqueue(new Callback<UrbanProblemsResponse>() {
            @Override
            public void onResponse(Call<UrbanProblemsResponse> call, retrofit2.Response<UrbanProblemsResponse> response) {
                UrbanProblemsResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, UrbanProblemsResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<UrbanProblemsResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void updateUrbanProblemsGPSSettings() {
        GeneralAPI api = retrofit.create(GeneralAPI.class);
        Call<UrbanProblemsGPSSettingsResponse> call = api.settings(defaultHeaders(), new UrbanProblemsGPSSettingsRequest());
        call.enqueue(new Callback<UrbanProblemsGPSSettingsResponse>() {
            @Override
            public void onResponse(Call<UrbanProblemsGPSSettingsResponse> call, retrofit2.Response<UrbanProblemsGPSSettingsResponse> response) {
                UrbanProblemsGPSSettingsResponse body = response.body();
                if (body != null && body.isSuccess()) {
                    UserSettings.getInstance().setUrbanProblemsGPSInterval(body.getIntervalTime());
                    UserSettings.getInstance().setUrbanProblemsGPSEnabled(body.isEnabled());
                } else {
                    Log.e(TAG, "updateUrbanProblemsGPSSettings error");
                }
            }

            @Override
            public void onFailure(Call<UrbanProblemsGPSSettingsResponse> call, Throwable t) {
                Log.e(TAG, null, t);
            }
        });
    }

    public void abuseTypes(final RequestCallback<AbuseTypeResponse> callback) {
        IssueReportAPI api = retrofit.create(IssueReportAPI.class);
        Call<AbuseTypeResponse> call = api.abuseTypes(defaultHeaders(), new AbuseTypeRequest());
        call.enqueue(new Callback<AbuseTypeResponse>() {
            @Override
            public void onResponse(Call<AbuseTypeResponse> call, retrofit2.Response<AbuseTypeResponse> response) {
                AbuseTypeResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, AbuseTypeResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<AbuseTypeResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void abuseSubmit(Long reportId, Long abuseId, final RequestCallback<Response> callback) {
        IssueReportAPI api = retrofit.create(IssueReportAPI.class);
        Call<Response> call = api.abuseSubmit(defaultHeaders(), new AbuseSubmitRequest(reportId, abuseId));
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // endregion

    // region Notifications

    public void notifications(final RequestCallback<NotificationsResponse> callback) {
        NotificationsAPI api = retrofit.create(NotificationsAPI.class);
        Call<NotificationsResponse> call = api.notifications(defaultHeaders());
        call.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, retrofit2.Response<NotificationsResponse> response) {
                NotificationsResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, NotificationsResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void sendRegId(String regid, final RequestCallback<Response> callback) {
        AccountAPI api = retrofit.create(AccountAPI.class);
        Call<Response> call = api.sendRegid(defaultHeaders(), new SendRegIdRequest(regid));
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // endregion

    public void feedbackTypes(final RequestCallback<FeedbackTypeResponse> callback) {
        FeedbackAPI api = retrofit.create(FeedbackAPI.class);
        Call<FeedbackTypeResponse> call = api.feedbackTypes(defaultHeaders(), new EmptyBodyRequest());
        call.enqueue(new Callback<FeedbackTypeResponse>() {
            @Override
            public void onResponse(Call<FeedbackTypeResponse> call, retrofit2.Response<FeedbackTypeResponse> response) {
                FeedbackTypeResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, FeedbackTypeResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<FeedbackTypeResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void feedback(String comment, Long feedbackTypeId, final RequestCallback<Response> callback) {
        FeedbackAPI api = retrofit.create(FeedbackAPI.class);
        Call<Response> call = api.feedback(defaultHeaders(), new FeedbackSendRequest(feedbackTypeId, comment));
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void sendLog(br.com.participact.participactbrasil.modules.db.Log log, final RequestCallback<Response> callback) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.22.218.112:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        LoggerAPI api = retrofit.create(LoggerAPI.class);
        Call<Response> call = api.logger(log);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, Response.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });

    }

    public void getIPData(final RequestCallback<IPDataResponse> callback) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ipinfo.participact.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        IPDataAPI api = retrofit.create(IPDataAPI.class);
        Call<IPDataResponse> call = api.ipData();
        call.enqueue(new Callback<IPDataResponse>() {
            @Override
            public void onResponse(Call<IPDataResponse> call, retrofit2.Response<IPDataResponse> response) {
                IPDataResponse body = response.body();
                if (body == null) {
                    body = getErrorResponse(response, IPDataResponse.class);
                }
                callback.onResponse(body);
            }

            @Override
            public void onFailure(Call<IPDataResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // region Upload files

    public void uploadFile(StorageFileRequest request, final RequestCallback<Response> callback, ProgressRequestBody.ProgressCallback progress) {

        if (UserSettings.getInstance().getSendDataOnlyOverWifi()) {
            if (!ConnectionUtils.isWiFiConnected(App.getInstance().getApplicationContext())) {
                Log.d(TAG, "Won't send data. User chose to send data only over wifi.");
                callback.onResponse(null);
                return;
            }
        }

        Call<Response> call = request.call(defaultHeaders(), retrofit, progress);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                try {
                    Log.d(TAG, response.toString());
                    Response body = response.body();
                    if (body == null) {
                        Log.e(TAG, response.errorBody().string());
                        body = getErrorResponse(response, Response.class);
                    } else {
                        Log.d(TAG, body.toString());
                    }
                    callback.onResponse(body);
                } catch (Exception e) {
                    callback.onResponse(null);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // endregion

    // region Network methods

    private <T> T getErrorResponse(retrofit2.Response<T> resp, Class<T> responseClassType) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Integer.class, new TypeAdapter<Integer>() {
                @Override
                public void write(JsonWriter out, Integer value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.value(value);
                }

                @Override
                public Integer read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    String stringValue = in.nextString();
                    try {
                        Integer value = Integer.valueOf(stringValue);
                        return value;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            });
            builder.registerTypeAdapter(Double.class, new TypeAdapter<Double>() {
                @Override
                public void write(JsonWriter out, Double value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.value(value);
                }

                @Override
                public Double read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    String stringValue = in.nextString();
                    try {
                        Double value = Double.valueOf(stringValue);
                        return value;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            });

            T response = builder.create().fromJson(resp.errorBody().string(), responseClassType);
            return response;

        } catch (Exception e) {
            Log.e(Tags.NETWORK, null, e);
        }
        return null;
    }

    public Map<String, String> defaultHeaders() {

        int versionCode = -1;
        String versionName = "";

        try {
            PackageInfo packageInfo = App.getInstance().getPackageManager().getPackageInfo(App.getInstance().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("X-PLATAFORM", "Android");
        map.put("X-DEVICE-MODEL", Build.MODEL);
        map.put("X-OS-VERSION", String.valueOf(Build.VERSION.SDK_INT));
        map.put("X-MANUFACTURER", Build.MANUFACTURER);
        map.put("X-APP-VERSION-NAME", versionName);
        map.put("X-APP-VERSION-CODE", String.valueOf(versionCode));
        map.put("Request_key", UUID.randomUUID().toString());
        map.put("Accept", "application/json");
        if (UserSettings.getInstance().getAccessToken() != null) {
            Log.i(TAG, "X-AUTHORIZATION: " + UserSettings.getInstance().getAccessToken());
            map.put("X-AUTHORIZATION", UserSettings.getInstance().getAccessToken());
        }
        return map;
    }

    // endregion

}
