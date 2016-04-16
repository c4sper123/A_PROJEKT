package android.testnavigation.Requests;


import android.testnavigation.BackendlessSettings;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class CustomJsonArrayRequest extends JsonArrayRequest
{
    public CustomJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("application-id", BackendlessSettings.AP_ID);
        headers.put("secret-key", BackendlessSettings.SECRET_KEY);
        headers.put("application-type","REST");
        return headers;
    }

}
