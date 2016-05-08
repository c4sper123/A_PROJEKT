package android.testnavigation.fragments;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.testnavigation.Requests.AppController;
import android.testnavigation.BackendlessSettings;
import android.testnavigation.Requests.DeleteObjectIdRequest;
import android.testnavigation.EditOfferScreen;
import android.testnavigation.Requests.JsonObjectIdRequest;
import android.testnavigation.Offer;
import android.testnavigation.R;
import android.testnavigation.Requests.SockHandle;
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

import io.socket.client.Ack;

public class MyOffersWindow extends Fragment {

    private ArrayList<Offer> offersData = new ArrayList<>();
    private String TAG = this.getClass().getSimpleName();
    private ProgressDialog pDialog;
    private AlertDialog.Builder myAlert;
    private String userId;
    private View rootView;
    private ImageButton refreshBtn;
    private Toolbar toolbar;
    private ImageButton deleteBtn;
    private TextView titleName;
    public String objeeectId;
    private ImageButton editBtn;
    private SockHandle socket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_my_offers_window, container, false);
        myAlert = new AlertDialog.Builder(getContext());

        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Prosím čakajte...");
        pDialog.setCancelable(false);

        socket = null;

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

        //loadMyOffers(BackendlessSettings.urlJsonObj, userId);
        loadMyDataFromServer(userId);

        refreshBtn = (ImageButton) toolbar.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadMyOffers(BackendlessSettings.urlJsonObj, userId);
                loadMyDataFromServer(userId);
            }
        });
        return rootView;
    }

    private void showAllOffers(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<Offer> adapter = new MyListAdapter();
                ListView list = (ListView) getView().findViewById(R.id.offersListView);
                list.setAdapter(adapter);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Offer> {
        public MyListAdapter() {
            super(getActivity(), R.layout.my_offer_view, offersData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = getActivity().getLayoutInflater().inflate(R.layout.my_offer_view, null);

            final Offer currentOffer = offersData.get(position);
            objeeectId = currentOffer.getObjectId();

            TextView nameText = (TextView) itemView.findViewById(R.id.offerNameTxt);
            nameText.setText(currentOffer.getName());
            ImageView iconView = (ImageView) itemView.findViewById(R.id.imageView);
            iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            UrlImageViewHelper.setUrlDrawable(iconView, currentOffer.getImageUrl());
            TextView localityText = (TextView) itemView.findViewById(R.id.offerPlaceTxt);
            localityText.setIncludeFontPadding(false);
            localityText.setText("  Miesto: " + currentOffer.getLocality());

            editBtn = (ImageButton) itemView.findViewById(R.id.editButton);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditOfferScreen.class);
                    intent.putExtra("objectId", currentOffer.getObjectId());
                    //Log.d("WTF",objeeectId + " " + currentOffer.getName());
                    startActivityForResult(intent, 9999);
                }
            });

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
                            //deleteOffer(currentOffer.getObjectId(), BackendlessSettings.urlJsonObjId);
                            deleteDataFromServer(currentOffer.getObjectId());
                            loadMyDataFromServer(userId);
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
        offersData.removeAll(offersData);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 9999) && (resultCode == Activity.RESULT_OK))
            loadMyDataFromServer(userId);
    }

    private void deleteOffer(String objectId, String URL) {
        objeeectId = objectId;
        showpDialog();
        URL += objectId;
        Log.d("URL delete: ", URL);

        DeleteObjectIdRequest stringRequest = new DeleteObjectIdRequest(Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(),"Ponuka úspešne vymazaná",Toast.LENGTH_SHORT).show();
                //loadMyOffers(BackendlessSettings.urlJsonObj, userId);
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

    private void deleteDataFromServer(final String objectId){
        final JSONObject obj = new JSONObject();
        socket = new SockHandle();
        showpDialog();

        try {
            obj.put("url", "/data/TonoKasperke14/" + objectId); //id objektu
            Toast.makeText(getContext(),"Ponuka úspešne vymazaná",Toast.LENGTH_SHORT).show();
            //loadMyOffers(BackendlessSettings.urlJsonObj, userId);
            hidepDialog();
        } catch (JSONException e) {
            myAlert.setMessage("Nepodarilo sa vymazať ponuku!").create();
            myAlert.setTitle("Error");
            myAlert.setIcon(R.drawable.error_icon);
            myAlert.setNegativeButton("Skúsiť znova", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //deleteOffer(objeeectId, BackendlessSettings.urlJsonObjId);
                    deleteDataFromServer(objectId);
                }
            });
            myAlert.show();
            hidepDialog();
        }

        socket.getSocket().emit("delete", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                Log.d("deletedInfo", obj.toString());
            }
        });
    }

    private void loadMyDataFromServer(final String userId) {
        showpDialog();
        offersData.removeAll(offersData);
        socket = new SockHandle();

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "/data/TonoKasperke14"); //username
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.getSocket().emit("get", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                JSONObject body = null;
                JSONArray data = null;
                //Log.d("getInfo", obj.toString());
                try {
                    body = obj.getJSONObject("body");
                    data = body.getJSONArray("data");
                    JSONObject data1;
                    //Log.i("getInfo",data.toString());

                    for (int i = 0; i < data.length(); i++) {
                        data1 = data.getJSONObject(i);
                        //Log.i("getInfoData1",data1.toString());
                        JSONObject offerObject = data1.getJSONObject("data");
                        //Log.i("getInfoOffer",offerObject.toString());
                        if(offerObject.getString("ownerId").equals(userId)) {
                            offersData.add(new Offer(offerObject.getString("name"), offerObject.getString("locality"), offerObject.getString("details"),
                                    Integer.parseInt(offerObject.getString("price")), Integer.parseInt(offerObject.getString("type")),
                                    offerObject.getString("startDate"), offerObject.getString("endDate"), Integer.parseInt(offerObject.getString("maxPeople")),
                                    offerObject.getString("imageUrl"), data1.getString("id")));
                        }
                    }
                    showAllOffers();
                    hidepDialog();
                } catch (JSONException e) {
                    hidepDialog();
                    Log.d("getError", ":(");
                }
                try {
                    if(obj.getString("statusCode").equals("200"));
                    else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAlert.setMessage("Nepodarilo sa nadviazať spojenie so serverom!").create();
                                myAlert.setTitle("Error");
                                myAlert.setIcon(R.drawable.error_icon);
                                myAlert.setNegativeButton("Skúsiť znova", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        loadMyDataFromServer(userId);
                                    }
                                });
                                myAlert.show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
