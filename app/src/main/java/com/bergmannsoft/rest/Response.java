package com.bergmannsoft.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.participact.participactbrasil.R;
import okhttp3.ResponseBody;

/**
 * Created by fabiobergmann on 4/26/16.
 */
public class Response {

    private static final String TAG = Response.class.getSimpleName();
    @SerializedName("status")
    private Boolean status;
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private String error;
    @SerializedName("code")
    private Integer code;

    public Response() {

    }

    public Boolean isSuccess() {
        return status != null && status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static <T extends Response> T getDefaultErrorResponse(Context context, Class<T> responseClassType) {
        JSONObject json = new JSONObject();
        try {
            json.put("status", false);
            json.put("message", context.getString(R.string.network_response_error_default_message));
            json.put("code", 555);
        } catch (JSONException e) {
            Log.e(TAG, null, e);
        }
        T response = new Gson().fromJson(json.toString(), responseClassType);
        return response;
    }

    public static Response getError(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                String str = responseBody.string();
                Log.v(TAG, "Response code: " + str);

                JSONObject json = new JSONObject(str);
                Response resp = new Response();
                resp.setStatus(json.getBoolean("status"));
                resp.setMessage(json.getString("message"));
                resp.setError(json.getString("error"));
                resp.setCode(json.getInt("code"));
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
