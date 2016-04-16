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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;



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


        Backendless.initApp(this, BackendlessSettings.AP_ID, BackendlessSettings.SECRET_KEY, BackendlessSettings.appVersion);
        Button logButton = (Button) findViewById(R.id.loginBtn);

        final EditText userNameTxt = (EditText) findViewById(R.id.userNameTxt);
        final EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = userNameTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                if(userName.isEmpty())
                {
                    userNameTxt.requestFocus();
                    userNameTxt.setError("FIELD CANNOT BE EMPTY");
                }
                else if(!userName.matches("[a-zA-Z0-9 ]+")) {
                    userNameTxt.requestFocus();
                    userNameTxt.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else if(password.isEmpty())
                {
                    passwordTxt.requestFocus();
                    passwordTxt.setError("FIELD CANNOT BE EMPTY");
                }
                //login
                else {
                    if (isConnectedToInternet()) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

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
                    }
                    else{
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

//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }

}
