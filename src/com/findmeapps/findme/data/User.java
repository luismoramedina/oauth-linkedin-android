package com.findmeapps.findme.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.findmeapps.findme.R;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 17/12/12
 * Time: 1:12
 * To change this template use File | Settings | File Templates.
 */
public class User {
    public String email;
    public String name;
    public String lastName;
    public String base64Avatar;

    public User() {}

    public void save(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(context.getString(R.string.findme_user_name_pref), name);
        editor.putString(context.getString(R.string.findme_user_last_name_pref), lastName);
        editor.putString(context.getString(R.string.findme_user_avatar_pref), base64Avatar);
        editor.putString(context.getString(R.string.findme_user_email_pref), email);
        editor.commit();
    }
}
