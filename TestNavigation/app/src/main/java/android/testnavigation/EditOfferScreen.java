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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

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

        myAlert = new AlertDialog.Builder(getApplicationContext());

        pDialog = new ProgressDialog(this.getApplicationContext());
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

       // loadEditedItems(editedOffer);
       // editOffer(objeeectId, BackendlessSettings.urlJsonObjId);
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

        JsonObjectIdPutRequest stringRequest = new JsonObjectIdPutRequest(editedOffer,Request.Method.PUT, URL, new Response.Listener<String>() {
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

    private void loadEditedItems(Offer offer){



    };

}
