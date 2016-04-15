package android.testnavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;



public class LoginWindow extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // getActionBar().setDisplayShowHomeEnabled(false);


        Backendless.initApp(this, BackendlessSettings.AP_ID, BackendlessSettings.SECRET_KEY, BackendlessSettings.appVersion);
        Button logButton = (Button) findViewById(R.id.loginBtn);

        final EditText userNameTxt = (EditText) findViewById(R.id.userNameTxt);
        final EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameTxt.getText().toString();
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
                    Backendless.UserService.login(userName, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            String userId = backendlessUser.getObjectId();
                            Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginWindow.this,MenuActivity.class);
                            intent.putExtra("userId", userId); //passing data to another activity - vyuzitie pri MOJE PONUKY
                            startActivity(intent);
                            userNameTxt.setText(null);
                            passwordTxt.setText(null);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(getApplicationContext(), "WRONG USERNAME OR PASSWORD!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }



}
