package android.testnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.testnavigation.Requests.SockHandle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;

public class LoginWindow extends Activity {
    private ProgressDialog pDialog;
    private AlertDialog.Builder myAlert;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Overovanie...");
        pDialog.setCancelable(false);
        myAlert = new AlertDialog.Builder(this);




        IO.Options opts = new IO.Options();
        opts.secure = false;
        opts.port = 1341;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.timeout = 5000;

        SockHandle socketHandler = new SockHandle();
        try {
            socketHandler.setSocket(IO.socket("http://sandbox.touch4it.com:1341/?__sails_io_sdk_version=0.12.1", opts));
            socketHandler.getSocket().connect();
        }
        catch (URISyntaxException e) {}


        //postDataOnServer();
        //deleteDataFromServer();
        //updateDataOnServer();
        getDataFromServer();
        //getOneDataFromServer();






        Backendless.initApp(this, BackendlessSettings.AP_ID, BackendlessSettings.SECRET_KEY, BackendlessSettings.appVersion);
        Button logButton = (Button) findViewById(R.id.loginBtn);

        final EditText userNameTxt = (EditText) findViewById(R.id.userNameTxt);
        final EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = userNameTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                if (userName.isEmpty()) {
                    userNameTxt.requestFocus();
                    userNameTxt.setError("FIELD CANNOT BE EMPTY");
                } else if (!userName.matches("[a-zA-Z0-9 ]+")) {
                    userNameTxt.requestFocus();
                    userNameTxt.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                } else if (password.isEmpty()) {
                    passwordTxt.requestFocus();
                    passwordTxt.setError("FIELD CANNOT BE EMPTY");
                }
                //login
                else {
                    if (isConnectedToInternet()) {
                       /* InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
*/
                        pDialog.show();
                        Backendless.UserService.login(userName, password, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                String userId = backendlessUser.getObjectId();
                                String userMail = backendlessUser.getEmail();
                                Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginWindow.this, MenuActivity.class);
                                intent.putExtra("userMail", userMail);
                                intent.putExtra("userName", userName);
                                intent.putExtra("userId", userId); //passing data to another activity - vyuzitie pri MOJE PONUKY
                                pDialog.dismiss();
                                startActivity(intent);
                                userNameTxt.setText(null);
                                passwordTxt.setText(null);
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "WRONG USERNAME OR PASSWORD!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        myAlert.setMessage("Chyba pripojenia na internet!").create();
                        myAlert.setTitle("Error");
                        myAlert.setIcon(R.drawable.error_icon);
                        myAlert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        myAlert.show();
                    }
                }
            }
        });
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private void postDataOnServer(){
        IO.Options opts = new IO.Options();
        opts.secure = false;
        opts.port = 1341;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.timeout = 5000;

        SockHandle socketHandler = new SockHandle();

        JSONObject js = new JSONObject();
        JSONObject newJs = new JSONObject();

        try {
            JSONObject jsObj;
            js.put("url", "/data/TonoKasperke14");
            newJs.put("test4", "Test4");
            newJs.put("ttt", "fff");
            jsObj = new JSONObject().put("data", newJs);
            js.put("data",jsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketHandler.getSocket().emit("post", js, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                JSONObject body = null;
                Log.i("postInfo", obj.toString());
                try {
                    body = obj.getJSONObject("body");
                    String id = body.getString("id");
                    Log.i("postInfo", "id : " + id);
                    Log.i("postInfo", body.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteDataFromServer(){
        JSONObject obj = new JSONObject();
        SockHandle socketHandler = new SockHandle();

        try {
            obj.put("url", "/data/TonoKasperke14/15f175b0-e8ad-4c26-aa04-c4bb26ce860e"); //id objektu
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketHandler.getSocket().emit("delete", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                Log.d("deletedInfo", obj.toString());
            }
        });
    }

    private void updateDataOnServer() {

        SockHandle socketHandler = new SockHandle();

        JSONObject obj = new JSONObject();
        JSONObject jsObj;

        try {
            obj.put("url", "/data/TonoKasperke14/eb78f3f4-8749-4985-acbc-7cacac4bb893"); //username a idobjektu
            jsObj = new JSONObject().put("data", new JSONObject().put("Test1", "updateeeeZeby"));
            obj.put("data",jsObj);
           // obj.put("user","TonoKasperke14");
           // obj.put("id","eb78f3f4-8749-4985-acbc-7cacac4bb893"); //id objektu
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketHandler.getSocket().emit("put", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                Log.i("putInfo", obj.toString());
            }
        });
    }

    private void getDataFromServer(){
        SockHandle socketHandler = new SockHandle();

        JSONObject obj = new JSONObject();

        try {
            obj.put("url", "/data/TonoKasperke14"); //username

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketHandler.getSocket().emit("get", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                JSONObject body = null;
                JSONArray data = null;
                Log.d("getInfo", obj.toString());

                try {
                    body = obj.getJSONObject("body");
                    Log.d("getInfo", body.toString());
                    data = body.getJSONArray("data");
                    Log.d("getInfo", data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void getOneDataFromServer(){
        SockHandle socketHandler = new SockHandle();

        JSONObject obj = new JSONObject();

        try {
            obj.put("url", "/data/TonoKasperke14/eb78f3f4-8749-4985-acbc-7cacac4bb893"); //username a id objektu


        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketHandler.getSocket().emit("get", obj, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                JSONObject body = null;
                JSONArray data = null;
                Log.d("getOneInfo", obj.toString());

                try {
                    body = obj.getJSONObject("body");
                    Log.d("getOneInfo", body.toString());
                    data = body.getJSONArray("data");
                    Log.d("getOneInfo", data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
