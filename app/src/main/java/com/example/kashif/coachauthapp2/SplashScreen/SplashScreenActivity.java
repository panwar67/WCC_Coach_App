package com.example.kashif.coachauthapp2.SplashScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.kashif.coachauthapp2.HomeScreenActivity.MainActivity;
import com.example.kashif.coachauthapp2.LoginActivities.LoginChoiceSelectionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by kashif on 16/7/17.
 */

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseUser user;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting the stored login choice preference
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String selectedLoginChoice=(mSharedPreference.getString("SelectedLoginChoice", "Default_Value"));

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            intent = new Intent(this, MainActivity.class);
            intent.putExtra("SelectedLoginChoice",selectedLoginChoice);
            intent.putExtra("uniqueregid", user.getUid());
            intent.putExtra("username",user.getDisplayName());
            if (user.getEmail() == null){
                intent.putExtra("usermail","-");
            }
            else {
                intent.putExtra("usermail",user.getEmail());
            }
            intent.putExtra("userimageurl",user.getPhotoUrl().toString());
        }
        else {

            intent = new Intent(this, LoginChoiceSelectionActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
