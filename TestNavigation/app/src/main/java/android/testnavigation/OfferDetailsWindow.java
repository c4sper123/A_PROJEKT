package android.testnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OfferDetailsWindow extends Fragment {
    private String objectId;
    private static String TAG = OffersWindow.class.getSimpleName();
    private ProgressDialog pDialog;
    private ImageView imageDetail;
    private AlertDialog.Builder myAlert;
    private ImageButton refreshBtn;
    final static String DATA_RECEIVE = "data_receive";
    private Toolbar toolbar;
    private Button buyBtn;
    private View rootView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_offer_details_window, container, false);
        myAlert = new AlertDialog.Builder(this.getContext());

        ImageView imgWhite = (ImageView) rootView.findViewById(R.id.imageView4);
        imgWhite.setVisibility(View.VISIBLE);

        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Prosím čakajte...");
        pDialog.setCancelable(false);

        Bundle args = getArguments();
        if (args != null) {
            objectId = args.getString(DATA_RECEIVE);
            Log.d(TAG, "objekt ID: " + objectId);
        }
        else{
            //chyba
        }
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        refreshBtn= (ImageButton) toolbar.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOffer(BackendlessSettings.urlJsonObjId, objectId);
            }
        });

        imageDetail = (ImageView) rootView.findViewById(R.id.imageView);
        imageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageDetail(BackendlessSettings.urlJsonObjId, objectId);
            }
        });

        buyBtn = (Button) rootView.findViewById(R.id.buyBtn);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlert.setMessage("Naozaj chcete zakúpiť túto ponuku?").create();
                myAlert.setTitle("Potvrdenie");
                myAlert.setIcon(R.drawable.checkout);
                myAlert.setNegativeButton("Kúpiť", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

        loadOffer(BackendlessSettings.urlJsonObjId, objectId);
        return rootView;
    }

    private void loadOffer(String URL, String objectId) {


        showpDialog();
        URL += objectId;
        Log.d("Request URL", URL); //len na vypisanie do logu, ako vyzera moja request URL

        final JsonObjectIdRequest jsonObjReq = new JsonObjectIdRequest(Request.Method.GET,URL,null ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try{
                    View itemView = getView();
                    TextView textView = (TextView) itemView.findViewById(R.id.offerNameTxt);
                    textView.setText(response.getString("name"));

                    Button btn = (Button) itemView.findViewById(R.id.buyBtn);
                    btn.setText("KÚPIŤ ZA " + response.getString("price") + "€");


                    JSONArray category =  response.getJSONArray("categories");
                    JSONObject categories = category.getJSONObject(0);

                    String type = null;
                    switch(response.getInt("type")){
                        case 1:{ type = "Chata"; break;}
                        case 2:{ type = "Hotel"; break;}
                        case 3:{ type = "Penzión"; break;}
                        case 4:{ type = "Apartmán"; break;}
                        default: break;
                    };
                    textView = (TextView) itemView.findViewById(R.id.textView3);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                    textView.setText(response.getString("details") +
                                    "\n\nLokalita: " + response.getString("locality")+
                                    "\n\nTyp: " + type +
                                    "\n\nMaximálny počet ľudí: " + response.getString("maxPeople")+
                                    "\n\nKategória: " + categories.getString("mainCategory")+
                                    "\n\nPodkategória: " + categories.getString("category")
                    );

                    ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    UrlImageViewHelper.setUrlDrawable(imageView, response.getString("imageUrl"));

                    textView = (TextView) itemView.findViewById(R.id.offerDateTxt);
                    Date start = new Date();
                    Date end = new Date();

                    start.setTime(Long.parseLong(response.getString("startDate")));  //tuuu
                    end.setTime(Long.parseLong(response.getString("endDate")));        //tuuu2

                    java.text.DateFormat formatStart, formatEnd;
                    formatStart = new SimpleDateFormat("dd.MM.");
                    formatEnd = new SimpleDateFormat("dd.MM.yyyy");

                    String staDate = formatStart.format(start);
                    String endDate = formatEnd.format(end);

                    textView.setText(staDate + " - " + endDate); //vyzera to takto: dd.MM. - dd.MM.yyyy  (napr. 3.4. - 20.4.2016)
                    itemView.findViewById(R.id.imageView4).setVisibility(View.INVISIBLE);
                    hidepDialog();



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
                myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getFragmentManager().popBackStackImmediate(); //návrat do predchadzajucej aktivity - OffersWindow
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

    private void loadImageDetail(String URL, String objectId) {


        showpDialog();
        URL += objectId + "?props=imageUrl";
        Log.d("Request URL", URL); //len na vypisanie do logu, ako vyzera moja request URL

        final JsonObjectIdRequest jsonObjReq = new JsonObjectIdRequest(Request.Method.GET,URL,null ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try{
                    final View itemView = getView();

                    String imageUrl = response.getString("imageUrl");

                    final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageDetail);
                    ImageView imgWhite = (ImageView) itemView.findViewById(R.id.imageView4);
                    imgWhite.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);

                    setScroll(true);


                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    UrlImageViewHelper.setUrlDrawable(imageView, imageUrl);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageView imgWhite = (ImageView) itemView.findViewById(R.id.imageView4);
                            imageView.setVisibility(View.INVISIBLE);
                            imgWhite.setVisibility(View.INVISIBLE);
                            setScroll(false);
                        }
                    });

                    hidepDialog();

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
                myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getFragmentManager().popBackStackImmediate(); //návrat do predchadzajucej aktivity - OffersWindow
                    }
                });
                myAlert.show();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void setScroll(final boolean hodnota){
        ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return hodnota;
            }
        });
    }


}
