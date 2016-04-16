package android.testnavigation.fragments;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.testnavigation.AppController;
import android.testnavigation.BackendlessSettings;
import android.testnavigation.DeleteObjectIdRequest;
import android.testnavigation.JsonObjectIdRequest;
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
import com.android.volley.toolbox.StringRequest;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyOffersWindow extends Fragment {

    private ArrayList<Offer> offersData = new ArrayList<>();
    private String TAG = this.getClass().getSimpleName();
    private ProgressDialog pDialog;
    private String userId;
    private AlertDialog.Builder myAlert;
    private View rootView;
    private ImageButton refreshBtn;
    private Toolbar toolbar;
    private ImageButton deleteBtn;
    private TextView titleName;
    public String objeeectId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_my_offers_window, container, false);
        myAlert = new AlertDialog.Builder(getContext());

        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Prosím čakajte...");
        pDialog.setCancelable(false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        titleName = (TextView) toolbar.findViewById(R.id.textView5);
        titleName.setText("MOJE PONUKY");


        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("userId");
            Log.d(TAG, "user ID: " + userId);
        } else {
            //chyba
        }

        loadMyOffers(BackendlessSettings.urlJsonObj, userId);

        refreshBtn = (ImageButton) toolbar.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offersData.removeAll(offersData);
                //offersData.clear();
                loadMyOffers(BackendlessSettings.urlJsonObj, userId);
            }
        });


        return rootView;
    }

    private void showAllOffers() {
        ArrayAdapter<Offer> adapter = new MyListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.offersListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Offer> {
        public MyListAdapter() {
            super(getActivity(), R.layout.my_offer_view, offersData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = getActivity().getLayoutInflater().inflate(R.layout.my_offer_view, null);

            final Offer currentOffer = offersData.get(position);


            TextView nameText = (TextView) itemView.findViewById(R.id.offerNameTxt);
            nameText.setText(currentOffer.getName());
//            TextView priceText = (TextView) itemView.findViewById(R.id.offerPriceTxt);
//            priceText.setText("" + currentOffer.getPrice().toString() + " €");
            ImageView iconView = (ImageView) itemView.findViewById(R.id.imageView);
            iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            UrlImageViewHelper.setUrlDrawable(iconView, currentOffer.getImageUrl());
            TextView localityText = (TextView) itemView.findViewById(R.id.offerPlaceTxt);
            localityText.setIncludeFontPadding(false);
            localityText.setText("  Miesto: " + currentOffer.getLocality());

            deleteBtn = (ImageButton) itemView.findViewById(R.id.deleteButton);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAlert.setMessage("Naozaj chcete vymazať túto ponuku?").create();
                    myAlert.setTitle("Potvrdenie");
                    myAlert.setIcon(R.drawable.error_icon);
                    myAlert.setNegativeButton("Vymazať", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            objeeectId = currentOffer.getObjectId();
                            deleteOffer(objeeectId, BackendlessSettings.urlJsonObjId);
                            dialog.dismiss();
                        }
                    });
                    myAlert.setPositiveButton("Zrušiť", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    myAlert.show();
                }
            });

            TextView detailsText = (TextView) itemView.findViewById(R.id.offerDetailsTxt);
            detailsText.setVisibility(View.INVISIBLE);
            detailsText.setClickable(false);

            return itemView;
        }
    }

    private void loadMyOffers(String URL, final String userId) {

        showpDialog();

        JsonObjectIdRequest jsonObjReq = new JsonObjectIdRequest(Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    JSONArray data = response.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject offerObject = (JSONObject) data.get(i);
                        if (offerObject.getString("ownerId").equals(userId)) {
                            Log.d(TAG, offerObject.getString("name"));
                            offersData.add(new Offer(offerObject.getString("name"), offerObject.getString("locality"), offerObject.getString("details"),
                                    Integer.parseInt(offerObject.getString("price")), Integer.parseInt(offerObject.getString("type")),
                                    offerObject.getString("startDate"), offerObject.getString("endDate"), Integer.parseInt(offerObject.getString("maxPeople")),
                                    offerObject.getString("imageUrl"), offerObject.getString("objectId")));

                        }
                    }
                    if (!response.getString("nextPage").equals("null")) {
                        loadMyOffers(response.getString("nextPage"), userId);
                    } else {
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
                        offersData.removeAll(offersData);
                        loadMyOffers(BackendlessSettings.urlJsonObj, userId);
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

//    DataPassListener mCallback;
//    public interface DataPassListener{
//        public void passData(String data);
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // Make sure that container activity implement the callback interface
//        try {
//            mCallback = (DataPassListener)activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(" must implement DataPassListener");
//        }
//    }

    private void deleteOffer(String objectId, String URL) {

        showpDialog();
        URL += objectId;
        Log.d("URL delete: ", URL);

        DeleteObjectIdRequest stringRequest = new DeleteObjectIdRequest(Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(),"Ponuka úspešne vymazaná",Toast.LENGTH_SHORT).show();
                loadMyOffers(BackendlessSettings.urlJsonObj, userId);
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                myAlert.setMessage("Nepodarilo sa vymazať ponuku!").create();
                myAlert.setTitle("Error");
                myAlert.setIcon(R.drawable.error_icon);
                myAlert.setNegativeButton("Skúsiť znova", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteOffer(objeeectId, BackendlessSettings.urlJsonObjId);
                    }
                });
                myAlert.show();
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
