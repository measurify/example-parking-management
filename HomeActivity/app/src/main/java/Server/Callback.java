package Server;


import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface Callback {
    void onSuccess(JSONObject result) throws JSONException, IOException, InterruptedException;
    void onError(ANError error) throws Exception;
}