package android.testnavigation.Requests;

import android.testnavigation.BackendlessSettings;
import android.testnavigation.Offer;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectIdPutRequest extends JsonObjectRequest
{
    private Offer offer;

    public JsonObjectIdPutRequest(Offer offer,int method, String url, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.offer = offer;
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("application-id", BackendlessSettings.AP_ID);
        headers.put("secret-key", BackendlessSettings.SECRET_KEY);
        headers.put("application-type", "REST");
        return headers;
    }

    @Override
    public Map getParams() throws AuthFailureError {
        Map params = new HashMap();

        params.put("endDate",offer.getEndDateStamp());//convert ešte
        params.put("locality", offer.getLocality());
        params.put("type", offer.getType());
        params.put("price",offer.getPrice());
        params.put("imageUrl",offer.getImageUrl());
        params.put("name",offer.getName());
        params.put("details",offer.getDetails());
        params.put("maxPeople",offer.getMaxPeople());
        params.put("startDate", offer.getStartDateStamp());//convert ešte

        return params;
    }

}
