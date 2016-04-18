package android.testnavigation.fragments;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.testnavigation.Requests.AppController;
import android.testnavigation.BackendlessSettings;
import android.testnavigation.Requests.JsonObjectIdRequest;
import android.testnavigation.Offer;
import android.testnavigation.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SummerOffersWindow extends Fragment{

    private ArrayList<Offer> offersData = new ArrayList<>();
    private String TAG = this.getClass().getSimpleName();
    private ProgressDialog pDialog;
    private String userId;
    private AlertDialog.Builder myAlert;
    private View rootView;
    private ImageButton refreshBtn;
    private Toolbar toolbar;
    private DataPassListener mCallback;
    private TextView titleName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_summer_offers_window, container, false);
        myAlert = new AlertDialog.Builder(getContext());

        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Prosím čakajte...");
        pDialog.setCancelable(false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        titleName = (TextView) toolbar.findViewById(R.id.textView5);
        titleName.setText("LETO");

        loadOffers(BackendlessSettings.urlJsonObj);

        refreshBtn= (ImageButton) toolbar.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // offersData.removeAll(offersData);
                //offersData.clear();
                loadOffers(BackendlessSettings.urlJsonObj);
            }
        });


        return rootView;
    }

    private void showAllOffers(){
        ArrayAdapter<Offer> adapter = new MyListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.offersListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Offer>{
        public MyListAdapter(){
            super(getActivity(),R.layout.offer_view,offersData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = getActivity().getLayoutInflater().inflate(R.layout.offer_view,null);

            final Offer currentOffer = offersData.get(position);


            TextView nameText = (TextView) itemView.findViewById(R.id.offerNameTxt);
            nameText.setText(currentOffer.getName());
            TextView priceText = (TextView) itemView.findViewById(R.id.offerPriceTxt);
            priceText.setText("" + currentOffer.getPrice().toString() + " €");
            ImageView iconView = (ImageView) itemView.findViewById(R.id.imageView);
            iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            UrlImageViewHelper.setUrlDrawable(iconView, currentOffer.getImageUrl());
            TextView localityText = (TextView) itemView.findViewById(R.id.offerPlaceTxt);
            localityText.setIncludeFontPadding(false);
            localityText.setText("  Miesto: " + currentOffer.getLocality());



            TextView detailsText = (TextView) itemView.findViewById(R.id.offerDetailsTxt);
            detailsText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.passData(currentOffer.getObjectId());
                }
            });

            return itemView;
        }
    }

    private void loadOffers(String URL) {

        showpDialog();
        Log.d(TAG, "url ka  " + URL);

        offersData.removeAll(offersData);
        JsonObjectIdRequest jsonObjReq = new JsonObjectIdRequest(Method.GET,URL,null ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try{
                    JSONArray data = response.getJSONArray("data");

                    for(int i=0;i<data.length();i++){
                        JSONObject offerObject = (JSONObject) data.get(i);
                        if(offerObject.getJSONArray("categories").getJSONObject(0).getString("mainCategory").equals("Leto")){
                        Log.d(TAG, offerObject.getString("name"));
                        offersData.add(new Offer(offerObject.getString("name"), offerObject.getString("locality"), offerObject.getString("details"),
                                Integer.parseInt(offerObject.getString("price")), Integer.parseInt(offerObject.getString("type")),
                                offerObject.getString("startDate"), offerObject.getString("endDate"), Integer.parseInt(offerObject.getString("maxPeople")),
                                offerObject.getString("imageUrl"), offerObject.getString("objectId")));
                        }
                    }
                    if(!response.getString("nextPage").equals("null")){
                        loadOffers(response.getString("nextPage"));
                    }
                    else{
                        showAllOffers();
                        hidepDialog();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();

                myAlert.setMessage("Nepodarilo sa nadviazať spojenie so serverom!").create();
                myAlert.setTitle("Error");
                myAlert.setIcon(R.drawable.error_icon);
                myAlert.setNegativeButton("Skúsiť znova", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadOffers(BackendlessSettings.urlJsonObj);
                    }
                });
                myAlert.show();

            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Make sure that container activity implement the callback interface
        try {
            mCallback = (DataPassListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(" must implement DataPassListener");
        }
    }
}

