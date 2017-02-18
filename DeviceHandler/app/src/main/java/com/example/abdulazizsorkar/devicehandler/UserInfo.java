package com.example.abdulazizsorkar.devicehandler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserInfo extends AppCompatActivity implements View.OnClickListener {

    Button btLogout;
    EditText etName, etAge, etUserName;
    UserLocalStore userLocalStore;
    public int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUserName = (EditText) findViewById(R.id.etUserName);
        btLogout = (Button) findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticated()==true)
            displayUserDetails();
        else
        {
            count++;
            if(count<2)
            startActivity(new Intent(this,Login.class));
            else
            {
                finish();
            }

        }

    }

    public void displayUserDetails()
    {
        User user = userLocalStore.getLoggedInUser();
        etName.setText(user.name);
        etAge.setText(user.age+"");
        etUserName.setText(user.userName);
    }

    public boolean authenticated()
    {
        return userLocalStore.getUserLoggedIn();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(this, Login.class));
                break;

            default:
                break;
        }
    }

}
