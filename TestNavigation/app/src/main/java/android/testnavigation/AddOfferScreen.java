package android.testnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.testnavigation.Requests.SockHandle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.Ack;

public class AddOfferScreen extends AppCompatActivity {

    private ImageButton backBtn;
    private ProgressDialog pDialog;
    private AlertDialog.Builder myAlert;
    private SockHandle socket;
    private String mainCategory;
    private String category;
    private String ownerId;
    private String locality;
    private String price;
    private String imageUrl;
    private String name;
    private String details;
    private String maxPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backBtn = (ImageButton) findViewById(R.id.imgBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ownerId = getIntent().getStringExtra("ownerId");
        socket = null;

        myAlert = new AlertDialog.Builder(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Prosím čakajte...");
        pDialog.setCancelable(false);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataOnServer();
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

    private void addDataOnServer(){
        showpDialog();
        socket = new SockHandle();

        JSONObject obj = new JSONObject();
        JSONObject jsObj;

        TextView txtName= (TextView) findViewById(R.id.txtTitle);
        final StringBuilder sb = new StringBuilder(txtName.getText().length());
        sb.append(txtName.getText());
        name = sb.toString();

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        final StringBuilder sb1 = new StringBuilder(txtDetails.getText().length());
        sb1.append(txtDetails.getText());
        details = sb1.toString();

        TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
        final StringBuilder sb2 = new StringBuilder(txtPrice.getText().length());
        sb2.append(txtPrice.getText());
        price = sb2.toString();

        TextView txtLocality = (TextView) findViewById(R.id.txtLocality);
        final StringBuilder sb3 = new StringBuilder(txtLocality.getText().length());
        sb3.append(txtLocality.getText());
        locality = sb3.toString();

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

        TextView txtMaxPeople = (TextView) findViewById(R.id.txtPeople);
        final StringBuilder sb5 = new StringBuilder(txtMaxPeople.getText().length());
        sb5.append(txtMaxPeople.getText());
        maxPeople = sb5.toString();

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


        TextView txtCategory = (TextView) findViewById(R.id.txtSubcategory);
        final StringBuilder sb8 = new StringBuilder(txtCategory.getText().length());
        sb8.append(txtCategory.getText());
        category = sb8.toString();

        TextView txtMainCategory = (TextView) findViewById(R.id.txtCategory);
        final StringBuilder sb9 = new StringBuilder(txtMainCategory.getText().length());
        sb9.append(txtMainCategory.getText());
        mainCategory = sb9.toString();

        TextView txtImageUrl = (TextView) findViewById(R.id.txtImageurl);
        final StringBuilder sb10 = new StringBuilder(txtImageUrl.getText().length());
        sb10.append(txtImageUrl.getText());
        imageUrl = sb10.toString();

        JSONObject editOfferJson = new JSONObject();

        try {
            obj.put("url", "/data/TonoKasperke14/"); //username a idobjektu
            editOfferJson.put("startDate", Long.toString(strDate.getTime()));//
            editOfferJson.put("endDate", Long.toString(enDate.getTime()));//
            editOfferJson.put("locality", locality);//
            editOfferJson.put("type", Integer.toString(type));//
            editOfferJson.put("ownerId", ownerId);//
            editOfferJson.put("price", price);//
            editOfferJson.put("imageUrl", imageUrl);
            editOfferJson.put("name", name);//
            editOfferJson.put("details", details);//
            editOfferJson.put("maxPeople", maxPeople);//
            editOfferJson.put("mainCategory", mainCategory);
            editOfferJson.put("category", category);
            jsObj = new JSONObject();
            jsObj.put("data", editOfferJson);
            obj.put("data",jsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.getSocket().emit("post", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                Log.i("putInfo", obj.toString());
                try {
                    if(obj.getString("statusCode").equals("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Ponuka úspešne pridaná", Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                                hidepDialog();
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAlert.setMessage("Nepodarilo sa nadviazať spojenie so serverom!").create();
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
