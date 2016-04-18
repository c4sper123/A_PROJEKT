package android.testnavigation.Requests;

import android.testnavigation.BackendlessSettings;
import android.testnavigation.Offer;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectIdPutRequest extends StringRequest
{
    private Offer offer;

    public JsonObjectIdPutRequest(Offer offer,int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.offer = offer;
        Log.d("aaaa",  offer.getObjectId() + " " + offer.getName() + " " + offer.getDetails() + " " + Integer.toString(offer.getPrice()));
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap<String, String>();
        headers.put("application-id", BackendlessSettings.AP_ID);
        headers.put("secret-key", BackendlessSettings.REST);
        headers.put("application-type", "REST");
      //
      //
      headers.put("Content-Type", "application/json");
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        Map params = new HashMap<String, String>();

     //  params.put("endDate",offer.getEndDateStamp());//convert ešte
     //   params.put("locality", offer.getLocality());
     //   params.put("type", offer.getType());
        params.put("price",Integer.toString(offer.getPrice()));
      //  params.put("imageUrl",offer.getImageUrl());
        params.put("name",offer.getName());
        params.put("details",offer.getDetails());
     //   params.put("maxPeople",offer.getMaxPeople());
      //  params.put("startDate", offer.getStartDateStamp());//convert ešte

        return params;
    }

}
