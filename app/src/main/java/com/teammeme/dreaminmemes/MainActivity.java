package com.teammeme.dreaminmemes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


// MainActivity connects to Parse Server and initializes Login screen. Provides functionality
// for logging in and registering user.
public class MainActivity extends AppCompatActivity {
    private EditText et_username, et_password;
    private Button login, register;
    private TextView username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("qBpMYLk5wOBosYWXVpAuedILE5JE3OsIWPUri4C5")
                .clientKey("9bySWn09g6GH6q4wBhGLHwjIC5CTcdrn7a2S9QrI")
                .server("https://parseapi.back4app.com/").build()
        );

        // Get actual widgets
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        login = (Button) findViewById(R.id.b_login);
        register = (Button) findViewById(R.id.b_register);
        username = (TextView) findViewById(R.id.tv_username);
        email = (TextView) findViewById(R.id.tv_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parseLogin();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).run();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parseRegister();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).run();
            }
        });

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("TempSenderID", "1234567890");
        installation.saveInBackground();
    }

    void parseLogin(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOut();
        ParseUser.logInInBackground(et_username.getText().toString(), et_password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    Toast.makeText(getApplicationContext(), "Successfully logged in.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), Tabs.class);
                    startActivity(i);
                    //progressDialog.dismiss();
                    //getUserDetailFromParse();
                } else {
                    Toast.makeText(getApplicationContext(), "Unrecognized credentials. Try again.", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                    //alertDisplayer("Login Fail", e.getMessage()+" Please re-try");
                }
            }
        });
    }

    void parseRegister() {
        ParseUser user = new ParseUser();
        user.setUsername(et_username.getText().toString());
        user.setPassword(et_password.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    saveNewUser();
                } else {
                    Toast.makeText(getApplicationContext(), "Register failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void saveNewUser() {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(et_username.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Welcome! You have been registered.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
