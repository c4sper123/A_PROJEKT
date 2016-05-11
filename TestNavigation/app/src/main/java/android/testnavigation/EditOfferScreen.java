package android.testnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.testnavigation.Requests.AppController;
import android.testnavigation.Requests.JsonObjectIdPutRequest;
import android.testnavigation.Requests.JsonObjectIdRequest;
import android.testnavigation.Requests.MySocket;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.Ack;

public class EditOfferScreen extends AppCompatActivity {
    private String objeeectId;
    private ImageButton backBtn;
    private String TAG = this.getClass().getSimpleName();
    private ProgressDialog pDialog;
    private AlertDialog.Builder myAlert;
    private Offer editedOffer;
    private MySocket socket;
    private String mainCategory;
    private String category;
    private String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        socket = null;

        myAlert = new AlertDialog.Builder(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Prosím čakajte...");
        pDialog.setCancelable(false);

        objeeectId = getIntent().getStringExtra("objectId");
        Log.d("objectId : ", objeeectId);

        backBtn = (ImageButton) findViewById(R.id.imgBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //loadEditedItems();
        loadDataFromServer(objeeectId);
        Button update = (Button) findViewById(R.id.updateButton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager inputManager = (InputMethodManager)
//                        getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                //editOffer(objeeectId, BackendlessSettings.urlJsonObjId);
                editDataOnServer(objeeectId);
            }
        });


    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void editOffer(String objectId, String URL) {
        //URL = "https://api.backendless.com/v1/data/offers";
        showpDialog();
        URL += objectId;
        Log.d("URL put: ", URL);

        TextView txtName= (TextView) findViewById(R.id.txtTitle);
        final StringBuilder sb = new StringBuilder(txtName.getText().length());
        sb.append(txtName.getText());
        editedOffer.setName(sb.toString());

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        final StringBuilder sb1 = new StringBuilder(txtDetails.getText().length());
        sb1.append(txtDetails.getText());
        editedOffer.setDetails(sb1.toString());

        TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
        final StringBuilder sb2 = new StringBuilder(txtPrice.getText().length());
        sb2.append(txtPrice.getText());
        editedOffer.setPrice(Integer.parseInt(sb2.toString()));

        TextView txtLocality = (TextView) findViewById(R.id.txtLocality);
        final StringBuilder sb3 = new StringBuilder(txtLocality.getText().length());
        sb3.append(txtLocality.getText());
        editedOffer.setLocality(sb3.toString());

//        TextView txtType = (TextView) findViewById(R.id.txtType);
//        final StringBuilder sb4 = new StringBuilder(txtType.getText().length());
//        sb4.append(txtType.getText());
//        editedOffer.setType(Integer.parseInt(sb4.toString()));

        TextView txtMaxPeople = (TextView) findViewById(R.id.txtPeople);
        final StringBuilder sb5 = new StringBuilder(txtMaxPeople.getText().length());
        sb5.append(txtMaxPeople.getText());
        editedOffer.setMaxPeople(Integer.parseInt(sb5.toString()));


        final JsonObjectIdPutRequest stringRequest = new JsonObjectIdPutRequest(editedOffer, Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Ponuka úspešne upravená", Toast.LENGTH_SHORT).show();
                finish();
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                myAlert.setMessage("Nepodarilo sa upraviť ponuku!").create();
                myAlert.setTitle("Error");
                myAlert.setIcon(R.drawable.error_icon);
                myAlert.setNegativeButton("Skúsiť znova", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        editOffer(objeeectId, BackendlessSettings.urlJsonObjId);
                    }
                });
                hidepDialog();
                myAlert.show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void loadEditedItems(){
        String URL = BackendlessSettings.urlJsonObjId;

        showpDialog();
        URL += objeeectId;
        Log.d("Request URL: ", URL); //len na vypisanie do logu, ako vyzera moja request URL

        final JsonObjectIdRequest jsonObjReq = new JsonObjectIdRequest(Request.Method.GET,URL,null ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try{
                    editedOffer = new Offer(response.getString("name"), response.getString("locality"), response.getString("details"),
                            Integer.parseInt(response.getString("price")), Integer.parseInt(response.getString("type")),
                            response.getString("startDate"), response.getString("endDate"), Integer.parseInt(response.getString("maxPeople")),
                            response.getString("imageUrl"), response.getString("objectId"));


                    TextView txtName= (TextView) findViewById(R.id.txtTitle);
                    txtName.setText(editedOffer.getName());
                    TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
                    txtDetails.setText(editedOffer.getDetails());
                    TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
                    txtPrice.setText(Integer.toString(editedOffer.getPrice()));
                    TextView txtLocality = (TextView) findViewById(R.id.txtLocality);
                    txtLocality.setText(editedOffer.getLocality());
                    TextView txtMaxPeople = (TextView) findViewById(R.id.txtPeople);
                    txtMaxPeople.setText(Integer.toString(editedOffer.getMaxPeople()));
                    TextView txtType = (TextView) findViewById(R.id.txtType);
                    switch (editedOffer.getType()){
                        case 1: txtType.setText("Chata");break;
                        case 2: txtType.setText("Hotel");break;
                        case 3: txtType.setText("Penzión");break;
                        case 4: txtType.setText("Apartmán");break;
                        default:break;
                    }

                    TextView txtStartDate = (TextView) findViewById(R.id.txtStartDate);
                    TextView txtEndDate = (TextView) findViewById(R.id.txtEndDate);
                    Date start = new Date();
                    Date end = new Date();
                    start.setTime(Long.parseLong(response.getString("startDate")));  //tuuu
                    end.setTime(Long.parseLong(response.getString("endDate")));        //tuuu2
                    java.text.DateFormat formatStart, formatEnd;
                    formatStart = new SimpleDateFormat("dd/MM/yyyy");
                    formatEnd = new SimpleDateFormat("dd/MM/yyyy");
                    String staDate = formatStart.format(start);
                    String endDate = formatEnd.format(end);
                    txtStartDate.setText(staDate);
                    txtEndDate.setText(endDate);

                    hidepDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
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
                myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                myAlert.show();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void loadDataFromServer(String objectId){
        showpDialog();
        socket = new MySocket();

        JSONObject obj = new JSONObject();

        try {
            obj.put("url", "/data/TonoKasperke14/"+objectId); //username a id objektu
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.getSocket().emit("get", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                JSONObject body = null;
                Log.d("getOneInfo1", obj.toString());

                try {
                    body = obj.getJSONObject("body");
                    Log.d("getOneInfo2", body.toString());
                    if(obj.getString("statusCode").equals("200")) {
                        JSONObject response = body.getJSONObject("data");
                        editedOffer = new Offer(response.getString("name"), response.getString("locality"), response.getString("details"),
                                Integer.parseInt(response.getString("price")), Integer.parseInt(response.getString("type")),
                                response.getString("startDate"), response.getString("endDate"), Integer.parseInt(response.getString("maxPeople")),
                                response.getString("imageUrl"), body.getString("id"));
                        mainCategory = response.getString("mainCategory");
                        category = response.getString("category");
                        ownerId = response.getString("ownerId");
                        Log.i("StringInfo", mainCategory + " " + category + " " + ownerId);
                        showDataFromServer(response);
                        hidepDialog();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Nepodarilo sa načítať údaje!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    hidepDialog();
                }
            }
        });
    }

    private void editDataOnServer(String objectId){
        socket = new MySocket();

        JSONObject obj = new JSONObject();
        JSONObject jsObj;

        TextView txtName= (TextView) findViewById(R.id.txtTitle);
        final StringBuilder sb = new StringBuilder(txtName.getText().length());
        sb.append(txtName.getText());
        editedOffer.setName(sb.toString());

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        final StringBuilder sb1 = new StringBuilder(txtDetails.getText().length());
        sb1.append(txtDetails.getText());
        editedOffer.setDetails(sb1.toString());

        TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
        final StringBuilder sb2 = new StringBuilder(txtPrice.getText().length());
        sb2.append(txtPrice.getText());
        editedOffer.setPrice(Integer.parseInt(sb2.toString()));

        TextView txtLocality = (TextView) findViewById(R.id.txtLocality);
        final StringBuilder sb3 = new StringBuilder(txtLocality.getText().length());
        sb3.append(txtLocality.getText());
        editedOffer.setLocality(sb3.toString());

        TextView txtType = (TextView) findViewById(R.id.txtType);
        final StringBuilder sb4 = new StringBuilder(txtType.getText().length());
        sb4.append(txtType.getText());
        int type;
        String typp = sb4.toString();
        Log.d("typ", "'" + typp + "'");
        if(typp.equalsIgnoreCase("Hotel")) type = 2;
        else if(typp.equalsIgnoreCase("Chata")) type = 1;
        else if(typp.equalsIgnoreCase("Penzión")) type = 3;
        else type = 4;
        editedOffer.setType(type);

        TextView txtMaxPeople = (TextView) findViewById(R.id.txtPeople);
        final StringBuilder sb5 = new StringBuilder(txtMaxPeople.getText().length());
        sb5.append(txtMaxPeople.getText());
        editedOffer.setMaxPeople(Integer.parseInt(sb5.toString()));

        TextView txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        final StringBuilder sb6 = new StringBuilder(txtStartDate.getText().length());
        sb6.append(txtStartDate.getText());
        TextView txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        final StringBuilder sb7 = new StringBuilder(txtEndDate.getText().length());
        sb7.append(txtEndDate.getText());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        Date enDate = null;
        try {
            strDate =  df.parse(sb6.toString());
            enDate = df.parse(sb7.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //editedOffer.setStartDate(Long.toString(strDate.getTime()));
        //editedOffer.setEndDate(Long.toString(enDate.getTime()));

        JSONObject editOfferJson = new JSONObject();

        try {
            obj.put("url", "/data/TonoKasperke14/" + objectId); //username a idobjektu
            editOfferJson.put("startDate", Long.toString(strDate.getTime()));
            editOfferJson.put("endDate", Long.toString(enDate.getTime()));
            editOfferJson.put("locality", editedOffer.getLocality());
            editOfferJson.put("type", editedOffer.getType().toString());
            editOfferJson.put("ownerId", ownerId);
            editOfferJson.put("price", editedOffer.getPrice().toString());
            editOfferJson.put("imageUrl", editedOffer.getImageUrl());
            editOfferJson.put("name", editedOffer.getName());
            editOfferJson.put("details", editedOffer.getDetails());
            editOfferJson.put("maxPeople", editedOffer.getMaxPeople().toString());
            editOfferJson.put("mainCategory", mainCategory);
            editOfferJson.put("category", category);
            jsObj = new JSONObject();
            jsObj.put("data", editOfferJson);
            obj.put("data",jsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.getSocket().emit("put", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                Log.i("putInfo", obj.toString());
                try {
                    if(obj.getString("statusCode").equals("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Ponuka úspešne upravená", Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                                hidepDialog();
                            }
                        });
                    }
                    else if(obj.getString("statusCode").equals("500")){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               myAlert.setMessage("Nepodarilo sa nadviazať spojenie so serverom!").create();
                               myAlert.setTitle("ServerError");
                               myAlert.setIcon(R.drawable.error_icon);
                               myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                       finish();
                                       hidepDialog();
                                   }
                               });
                               myAlert.show();
                           }
                       });
                    } else if(obj.getString("statusCode").equals("400")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAlert.setMessage("Bad Request!").create();
                                myAlert.setTitle("Error");
                                myAlert.setIcon(R.drawable.error_icon);
                                myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        hidepDialog();
                                    }
                                });
                                myAlert.show();
                            }
                        });
                    } else if(obj.getString("statusCode").equals("404")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAlert.setMessage("Entry not found - nepodarilo sa nájsť dáta na serveri!").create();
                                myAlert.setTitle("Error - Not found");
                                myAlert.setIcon(R.drawable.error_icon);
                                myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        hidepDialog();
                                    }
                                });
                                myAlert.show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    hidepDialog();
                }
            }
        });
    }

    private void showDataFromServer(final JSONObject response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtName = (TextView) findViewById(R.id.txtTitle);
                txtName.setText(editedOffer.getName());
                TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
                txtDetails.setText(editedOffer.getDetails());
                TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
                txtPrice.setText(Integer.toString(editedOffer.getPrice()));
                TextView txtLocality = (TextView) findViewById(R.id.txtLocality);
                txtLocality.setText(editedOffer.getLocality());
                TextView txtMaxPeople = (TextView) findViewById(R.id.txtPeople);
                txtMaxPeople.setText(Integer.toString(editedOffer.getMaxPeople()));
                TextView txtType = (TextView) findViewById(R.id.txtType);
                switch (editedOffer.getType()) {
                    case 1:
                        txtType.setText("Chata");
                        break;
                    case 2:
                        txtType.setText("Hotel");
                        break;
                    case 3:
                        txtType.setText("Penzión");
                        break;
                    case 4:
                        txtType.setText("Apartmán");
                        break;
                    default:
                        break;
                }

                TextView txtStartDate = (TextView) findViewById(R.id.txtStartDate);
                TextView txtEndDate = (TextView) findViewById(R.id.txtEndDate);
                Date start = new Date();
                Date end = new Date();
                try {
                    start.setTime(Long.parseLong(response.getString("startDate")));
                    end.setTime(Long.parseLong(response.getString("endDate")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                java.text.DateFormat formatStart, formatEnd;
                formatStart = new SimpleDateFormat("dd/MM/yyyy");
                formatEnd = new SimpleDateFormat("dd/MM/yyyy");
                String staDate = formatStart.format(start);
                String endDate = formatEnd.format(end);
                txtStartDate.setText(staDate);
                txtEndDate.setText(endDate);
            }
        });
    }
}

