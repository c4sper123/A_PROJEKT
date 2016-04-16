package android.testnavigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeleteObjectIdRequest extends StringRequest{


    public DeleteObjectIdRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
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
