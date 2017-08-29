package com.example.vikalpsajwan.homeautomation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Vikalp on 21/05/2017.
 */

public class GetStatusTask extends AsyncTask<List<NameValuePair>, Void, Void> {

    // CODES FOR SUCCESS STATUS IN RESPONSE
    final int SUCCESS = 1;
    final int FAIL = 0;
    final int NOT_AUTHORIZED = 2;

    Context context;
    JSONParser jsonParser;
    ProgressDialog pDialog;
    int success;

    String userName;
    String password;
    String ip;

    Activity activity;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    SharedPreferences ipPref;
    SharedPreferences.Editor ipEditor;

    JSONObject json;


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Void doInBackground(List<NameValuePair>... params) {
        success = FAIL; // in case of connection problem

        List<NameValuePair> extras = params[0];
        extras.add(new BasicNameValuePair("query", "logIn"));

        // Do the HTTP POST Request with the JSON parameters

        
        json = jsonParser.makeHttpRequest("http://"+ ip +":8080/Index", "GET", extras);

        try {


            if (json != null)
                success = json.getInt("success");


        } catch (JSONException e) {

            e.printStackTrace();

        }

        return null;
    }

    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     * <p>
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        this.cancel(true);
    }

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    public GetStatusTask(Activity activity, Context context, String userName, String password, String ip) {
        super();
        this.activity = activity;
        this.context = context;
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        ipPref = context.getSharedPreferences("IPPref", MODE_PRIVATE);
        ipEditor = ipPref.edit();

    }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        jsonParser = new JSONParser();
        pDialog = new ProgressDialog(activity);

        if(activity instanceof MainActivity)
            pDialog.setMessage("Logging In..."); //Set the message for the loading window
        else
            pDialog.setMessage("Performing Refresh..."); //Set the message for the loading window

        pDialog.setCancelable(true);
        pDialog.setIndeterminate(false);
        pDialog.show(); //Place the loading message on the screen
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param aVoid The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        /**TODO
         *
         */
        // $%^$^%$%&$#@%#^
//        success = SUCCESS;
//        json = new JSONObject();
        // &^%%&*(*&^%^&

        if(activity instanceof MainActivity) {


            MainActivity mainActivity = (MainActivity) activity;

            if (success == SUCCESS) {
                // save userName and password in persistent sharedPreferences
                editor.putString("userName", userName);
                editor.putString("password", password);

                if(!pref.getString("userName", "").equals(ip))
                    ipEditor.putString("ip", ip);

                if (mainActivity.autoLogInCB.isChecked()) {
                    editor.putBoolean("autoLogIn", true);
                }
                editor.commit();
                ipEditor.commit();

                Intent intent = new Intent(context, HomePageActivity.class);
                intent.putExtra("jsonDataAsString", json.toString());
                mainActivity.startActivity(intent);
                mainActivity.finish();

            } else {
                if (success == NOT_AUTHORIZED) {
                    Toast.makeText(context, "                   LOGIN FAILED!!! \n Wrong userName and/or Password", Toast.LENGTH_LONG).show();
                    // REMOVE saved userName and password as that user is no longer valid
                    if (pref.getBoolean("autoLogIn", false)) {
                        editor.clear();
                        editor.commit();
                    }
                } else {  //network error
                    Toast.makeText(context, "CONNECTION ERROR !!!", Toast.LENGTH_LONG).show();
                }
                mainActivity.userNameET.setText("");
                mainActivity.passwordET.setText("");
                mainActivity.userNameET.requestFocus();
            }
        }else{
            HomePageActivity homePageActivity = (HomePageActivity)activity;

            pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
            editor = pref.edit();

            if (success == SUCCESS) {
                homePageActivity.setUI(json);

            } else {
                if (success == NOT_AUTHORIZED) {
                    Toast.makeText(context, "NOT AUTHORIZED!!!", Toast.LENGTH_LONG).show();
                    // REMOVE saved userName and password as that user is no longer valid
                    if (pref.getBoolean("autoLogIn", false)) {
                        editor.clear();
                        editor.commit();
                    }
                    Intent intent = new Intent(context, MainActivity.class);
                    homePageActivity.startActivity(intent);
                    homePageActivity.finish();
                } else {  //network error
                    Toast.makeText(context, "CONNECTION ERROR !!!", Toast.LENGTH_LONG).show();
                }

            }
        }

        pDialog.dismiss(); // Close the loading window when ready
    }

}



