package com.findmeapps.findme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import com.findmeapps.findme.service.LinkedInService;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;

import java.util.HashMap;

import static com.findmeapps.findme.service.LinkedInService.KEY_AUTHORIZATION_URL;
import static com.findmeapps.findme.service.LinkedInService.OAUTH_VERIFIER_PARAM;

public class LinkedInActivity extends Activity implements View.OnClickListener {

    public static final String FIRST_NAME = "firstName";
    static final String SUMMARY = "summary";

    private LinkedInService linkedInService;

    private ProgressDialog progDailog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.findmeapps.findme.R.layout.main);
        View nextButton = findViewById(R.id.next_button);
        View nextButtonWithOAuth = findViewById(R.id.next_button_with_oauth);
        nextButton.setOnClickListener(this);
        nextButtonWithOAuth.setOnClickListener(this);
        linkedInService = new LinkedInService(
                getString(R.string.consumer_key),
                getString(R.string.consumer_secret));
//        doNextAction();
    }

    private void showProfile() {
        Log.v("FINDME", "Fetching profileForCurrentUser for current user.");
        new LinkedInAsynkForPerson().execute();
    }

    private void showProfile(Person profile) {
        LIUtils.printPerson(profile);
//        Person profileStandardByUrl = client.getProfileByUrl(profile.getPublicProfileUrl(), ProfileType.STANDARD);
        String firstName = profile.getFirstName();
        String summary = profile.getSummary();
        HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
        stringStringHashMap.put(FIRST_NAME, firstName);
        stringStringHashMap.put(SUMMARY, summary);

        Intent intent = new Intent(this, ShowUserActivity.class);
        intent.putExtras(getBundleFromMap(stringStringHashMap));
        startActivity(intent);
    }

    public void onClick(View v) {
        if((v.getId()) == R.id.next_button_with_oauth) {
            doOAuth();
        } else {
            doNextAction();
        }
    }

    private void doNextAction() {
        //Button next
        try {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            //If not exists a token on prefs we launch the oauth li process
            if ("".equals(defaultSharedPreferences.getString(getString(R.string.li_token_pref), ""))) {

                doOAuth();
                //see postexecute
            } else {
                doShowProfile();
            }

        } catch (Exception e) {
            Log.e("FINDME", "Error", e);
        }
    }

    private void doShowProfile() {
        SharedPreferences defaultSharedPreferences;//LinkedInAccessToken is in the preferences ->
        // new LinkedInAccessToken(
        //           "429a204a-fcb1-4c08-94e9-0fdd9f9bfed6",//token
        //           "9f37a401-c78d-4626-bd26-982ecb51adf8");//tokenSecret

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = defaultSharedPreferences.getString(getString(R.string.li_token_pref), "");
        String tokenSecret = defaultSharedPreferences.getString(getString(R.string.li_token_secret_pref), "");
        linkedInService.setLinkedInAccessToken(token, tokenSecret);
        //TODO if empty error
        showProfile();
    }

    private void doOAuth() {
        new LinkedInAsynkTaskForOAuth().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String pinCode = data.getStringExtra(OAUTH_VERIFIER_PARAM);
            new LinkedInAsynkForAccessToken().execute(pinCode);
        }
    }

    //TODO utils
    public Bundle getBundleFromMap(HashMap<String, String> map) {
        Bundle bundle = new Bundle();
        for (String key : map.keySet()) {
            bundle.putString(key, map.get(key));
        }
        return bundle;
    }

    private void saveLinkedInAccessToken(LinkedInAccessToken linkedInAccessToken) {
        //Save LinkedInAccessToken to preferences
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(getString(R.string.li_token_pref), linkedInAccessToken.getToken());
        editor.putString(getString(R.string.li_token_secret_pref), linkedInAccessToken.getTokenSecret());
        editor.commit();
    }

    private void launchOAuthProccess(String authUrl) {
        Intent intent = new Intent(this, LIWebViewActivity.class);
        intent.putExtra(KEY_AUTHORIZATION_URL, authUrl);
        //Open web browser to authenticate
        startActivityForResult(intent, 1);
    }

    private void startProggressDialog() {
        progDailog = ProgressDialog.show(this,
                getString(R.string.wait_title),
                getString(R.string.wait_message), false);
    }

    abstract class LinkedInAsynkGeneric extends AsyncTask {
        protected Exception exception;
        @Override
        protected void onPreExecute() {
            startProggressDialog();
        }
    }

    class LinkedInAsynkTaskForOAuth extends LinkedInAsynkGeneric {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Object result) {
            launchOAuthProccess((String) result);
       }

        @Override
        protected Object doInBackground(Object... /*no used*/params) {
            try {
                return linkedInService.getOAuthUrl();
            } catch (Exception e) {
                Log.e("FINDME", e.getMessage(), e);
                this.exception = e;
                return null;
            }
        }
    }

    class LinkedInAsynkForAccessToken extends LinkedInAsynkGeneric {

        @Override
        protected LinkedInAccessToken doInBackground(Object... params) {
            try {
                String pinCode = (String) params[0];
                return linkedInService.getAccessTokenWithPinCode(pinCode);
            } catch (Exception e) {
                Log.e("FINDME", e.getMessage(), e);
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            progDailog.dismiss();
            saveLinkedInAccessToken((LinkedInAccessToken) result);
            showProfile();
        }
    }

   class LinkedInAsynkForPerson extends LinkedInAsynkGeneric {

        @Override
        protected Object doInBackground(Object... /*not used*/params) {
            try {
                return linkedInService.getProfile();
            } catch (Exception e) {
                Log.e("FINDME", e.getMessage(), e);
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            progDailog.dismiss();
            showProfile((Person) result);
        }
    }

}