package com.example.kashif.coachauthapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginChoiceSelectionActivity extends AppCompatActivity implements CoachLoginAlertDialogInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choice_selection);

    }


    // on coach login click show Alert Dialog
    public void showCoachLoginAlertDialog(View view) {
        CoachLoginAlertDialog coachLoginAlertDialog = new CoachLoginAlertDialog(this);
        coachLoginAlertDialog.show(getSupportFragmentManager(), "Coach Login Alert");
    }


    @Override
    public void okButtonClicked(String value) {
        saveLoginChoicePreference("Coach");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("SelectedLoginChoice", "Coach");
        startActivity(intent);
        finish();
    }

    // players login here
    public void playerLoginScreen(View view){
        saveLoginChoicePreference("Player");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("SelectedLoginChoice", "Player");
        startActivity(intent);
        finish();
    }


    // parent login here
    public void parentLoginScreen(View view){
        saveLoginChoicePreference("Parent");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("SelectedLoginChoice", "Parent");
        startActivity(intent);
        finish();
    }

    // method to save login choice preference
    public void saveLoginChoicePreference(String selectedLoginchoice) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SelectedLoginChoice", selectedLoginchoice);
        editor.commit();
    }
}
