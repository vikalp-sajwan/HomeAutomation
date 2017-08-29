package com.example.vikalpsajwan.homeautomation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomePageActivity extends AppCompatActivity {

    String userName;
    String password;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // UI componenets
    TextView tempTV;
    TextView humidityTV;
    AppCompatSeekBar fanSeekBar;
    ToggleButton bulbButton;
    Switch automateSwitch;
    Switch lockSwitch;
    TextView messageTV;

    SharedPreferences ipPref;


    // Toolbar
    Toolbar myToolbar;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private JSONObject json;

    List<NameValuePair> extras;

    SetStatusTask setStatusTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // get UI components references
        tempTV = (TextView) findViewById(R.id.tempTextView);
        humidityTV = (TextView) findViewById(R.id.humidityTextView);
        fanSeekBar = (AppCompatSeekBar) findViewById(R.id.fanSeekBar);
        bulbButton = (ToggleButton) findViewById(R.id.bulbButton);
        automateSwitch = (Switch) findViewById(R.id.automateSwitch);
        lockSwitch = (Switch) findViewById(R.id.lockSwitch);
        messageTV = (TextView) findViewById(R.id.messageTextView);


        ipPref = getApplicationContext().getSharedPreferences("IPPref", MODE_PRIVATE);

        extras = new ArrayList<>();


        //setting up the toolbar
        myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);


        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        userName = pref.getString("userName", "");
        password = pref.getString("password", "");

        try {
            json = new JSONObject(this.getIntent().getStringExtra("jsonDataAsString"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setUI(json);

    }


    void setListeners() {
        extras = new ArrayList<>();
        extras.add(new BasicNameValuePair("userName", userName)); //Add the parameters to an array
        extras.add(new BasicNameValuePair("password", password));
        automateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                extras.add(new BasicNameValuePair("key", "auto"));
                if (isChecked)
                    extras.add(new BasicNameValuePair("value", "true"));

                else
                    extras.add(new BasicNameValuePair("value", "false"));
                performTask(extras);

            }
        });


        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                extras.add(new BasicNameValuePair("key", "lock"));
                if (isChecked)
                    extras.add(new BasicNameValuePair("value", "true"));

                else
                    extras.add(new BasicNameValuePair("value", "false"));
                performTask(extras);

            }
        });

        bulbButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                extras.add(new BasicNameValuePair("key", "light"));
                if (isChecked)
                    extras.add(new BasicNameValuePair("value", "true"));

                else
                    extras.add(new BasicNameValuePair("value", "false"));
                performTask(extras);

            }
        });

        fanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int prevValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                prevValue = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() != prevValue) {
                    extras.add(new BasicNameValuePair("key", "fan"));
                    extras.add(new BasicNameValuePair("value", "" + seekBar.getProgress()));
                    performTask(extras);
                }
            }
        });

    }

    public void removeListeners() {
        fanSeekBar.setOnSeekBarChangeListener(null);
        bulbButton.setOnCheckedChangeListener(null);
        automateSwitch.setOnCheckedChangeListener(null);
        lockSwitch.setOnCheckedChangeListener(null);
    }

    public void performTask(List<NameValuePair> extras) {
        setStatusTask = new SetStatusTask(this, getApplicationContext());
        setStatusTask.execute(extras);
    }


    // method to set UI from json data
    public void setUI(JSONObject json) {
        removeListeners();



        // to remove previous state changes
        messageTV.setText("");
        fanSeekBar.setEnabled(true);
        bulbButton.setEnabled(true);
        lockSwitch.setEnabled(true);
        automateSwitch.setEnabled(true);



        boolean auto = false;
        boolean light = false;
        int fan = 0;
        boolean isAdmin = true;
        String temp = new String();
        String humidity = new String();
        int success = 0;
        boolean lock = false;

        /**TODO
         *
         */
        // $%^$^%$%&$#@%#^
//        success = 1;
//        json = new JSONObject();
//        lock = true;
        // &^%%&*(*&^%^&

        try {
            isAdmin = json.getBoolean("admin");

            temp = json.getString("temp");
            humidity = json.getString("humidity");
            light = json.getBoolean("light");
            fan = json.getInt("fan");
            auto = json.getBoolean("auto");
            success = json.getInt("success");
            lock = json.getBoolean("lock");


        } catch (Exception e) {
            e.printStackTrace();
        }

        tempTV.setText(temp + " \u2103");
        humidityTV.setText(humidity + " %");
        bulbButton.setChecked(light);
        automateSwitch.setChecked(auto);
        lockSwitch.setChecked(lock);
        fanSeekBar.setProgress(fan);

        if (isAdmin) {    //admin
            if (auto) {
                fanSeekBar.setEnabled(false);
                bulbButton.setEnabled(false);
            }
        } else {           // not admin
            lockSwitch.setEnabled(false);

            if (lock) {
                automateSwitch.setEnabled(false);
                messageTV.setText("Controls are LOCKED by ADMIN");
                fanSeekBar.setEnabled(false);
                bulbButton.setEnabled(false);

            }

            if (auto) {
                fanSeekBar.setEnabled(false);
                bulbButton.setEnabled(false);
            }

        }

        setListeners();

    }


    /**
     * overridden method to inflate the Toolbar menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);




        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id;

        if (item == null) { // for case when success == 3 and we call the refresh function manually
            id = R.id.refreshButton;
        } else{
            id = item.getItemId();
        }

        if (id == R.id.refreshButton) {

            List<NameValuePair> extras = new ArrayList<>();

            extras.add(new BasicNameValuePair("userName", userName)); //Add the parameters to an array
            extras.add(new BasicNameValuePair("password", password));

//
//            getStatusTask = new GetStatusTask(this, getApplicationContext(),userName, password);
//            getStatusTask.execute(extras);

            new GetStatusTask(this, getApplicationContext(), userName, password, ipPref.getString("ip", "")).execute(extras);

        }else if(id == R.id.cameraButton){
            Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
            startActivity(intent);
        }
        else {  //logout
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            editor = pref.edit();
            // REMOVE saved userName and password as that user is no longer valid
            if (pref.getBoolean("autoLogIn", false)) {
                editor.clear();
                editor.commit();
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);

    }


}

