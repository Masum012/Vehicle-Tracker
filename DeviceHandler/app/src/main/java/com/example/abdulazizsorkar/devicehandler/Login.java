package com.example.abdulazizsorkar.devicehandler;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextView tvRegisterLink;
    EditText userName;
    EditText passWord;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText) findViewById(R.id.etUserName);
        passWord = (EditText) findViewById(R.id.etPassword);

        Button loginButton = (Button) findViewById(R.id.btLogin);
        loginButton.setOnClickListener(this);

        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        tvRegisterLink.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btLogin:
                String username = userName.getText().toString();
                String password = passWord.getText().toString();

                User user = new User(username,password);

                authenticate(user);
                break;

            case R.id.tvRegisterLink:
                startActivity(new Intent(this,Register.class));
                break;
        }
        //startActivity(new Intent(this,UserInfo.class));
    }

    private void authenticate(User user) {
        ServerRequestsLogReg serverRequests = new ServerRequestsLogReg(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if(returnedUser==null)
                    showErrorMessage();
                else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        startActivity(new Intent(this,MapsActivity.class));
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Incorrect User details");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
