package android.testnavigation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.testnavigation.Requests.AppController;
import android.testnavigation.Requests.DeleteObjectIdRequest;
import android.testnavigation.Requests.JsonObjectIdPutRequest;
import android.testnavigation.Requests.JsonObjectIdRequest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditOfferScreen extends AppCompatActivity {
    private String objeeectId;
    private ImageButton backBtn;
    private String TAG = this.getClass().getSimpleName();
    private ProgressDialog pDialog;
    private AlertDialog.Builder myAlert;
    private Offer editedOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        loadEditedItems();
        Button update = (Button) findViewById(R.id.updateButton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOffer(objeeectId, BackendlessSettings.urlJsonObjId);
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
}

