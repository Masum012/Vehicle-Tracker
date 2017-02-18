package com.example.abdulazizsorkar.devicehandler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity implements View.OnClickListener{

    Button btRegister;
    EditText etName,etAge,etUserName,etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPass = (EditText) findViewById(R.id.etPassword);
        btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btRegister:
                String name = etName.getText().toString();
                int age = Integer.parseInt(etAge.getText().toString());
                String username = etUserName.getText().toString();
                String password = etPass.getText().toString();

                User user = new User(name,username,password,age);
                registerUser(user);
                break;
        }
    }

    private void registerUser(User user) {
        ServerRequestsLogReg serverRequests = new ServerRequestsLogReg(this);
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnUser) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
    }
}
