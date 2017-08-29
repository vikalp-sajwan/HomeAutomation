package com.example.vikalpsajwan.homeautomation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vikalp on 20/05/2017.
 */

public class MainActivity extends AppCompatActivity {

    AppCompatEditText userNameET;
    AppCompatEditText passwordET;
    CheckBox autoLogInCB;
    AppCompatButton button;
    GetStatusTask getStatusTask;
    AppCompatEditText ipET;

    String userName;
    String password;
    String ip;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences ipPref;
    SharedPreferences.Editor ipEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userNameET = (AppCompatEditText) findViewById(R.id.userName_ET);
        passwordET = (AppCompatEditText) findViewById(R.id.password_ET);
        button = (AppCompatButton) findViewById(R.id.login_button);
        autoLogInCB = (CheckBox)findViewById(R.id.autoLogInCB);
        ipET = (AppCompatEditText) findViewById(R.id.ip_ET);

        ipPref = getApplicationContext().getSharedPreferences("IPPref", MODE_PRIVATE);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        ipEditor = ipPref.edit();

        ipET.setText(ipPref.getString("ip", ""));
        // check if userName and password is already stored
        if (pref.getBoolean("autoLogIn", false)) {
            userNameET.setText(pref.getString("userName", ""));
            passwordET.setText(pref.getString("password", ""));

            autoLogInCB.setChecked(true);
            button.performClick();
        } else {
            userNameET.requestFocus();
        }
    }

    // called on pressing the LogIn button
    public void logIn(View view) {
        userName = userNameET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        ip = ipET.getText().toString().trim();

        if (userName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Valid Username String", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Valid Password String", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(ip.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Enter Valid Device IP", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            List<NameValuePair> extras = new ArrayList<>();

            extras.add(new BasicNameValuePair("userName", userName)); //Add the parameters to an array
            extras.add(new BasicNameValuePair("password", password));


            getStatusTask = new GetStatusTask(this, getApplicationContext(),userName, password, ip);
            getStatusTask.execute(extras);

        }

    }


}






