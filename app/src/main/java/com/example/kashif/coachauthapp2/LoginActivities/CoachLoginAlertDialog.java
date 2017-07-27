package com.example.kashif.coachauthapp2.LoginActivities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kashif.coachauthapp2.LoginActivities.CoachLoginAlertDialogInterface;
import com.example.kashif.coachauthapp2.LoginActivities.LoginChoiceSelectionActivity;

/**
 * Created by kashif on 18/7/17.
 */

public class CoachLoginAlertDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText getKeyEditText;
    LoginChoiceSelectionActivity callBackActivity;
    CoachLoginAlertDialogInterface coachLoginAlertDialogInterface;


    public CoachLoginAlertDialog(){

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        callBackActivity = new LoginChoiceSelectionActivity();
        getKeyEditText = new EditText(getActivity());
        getKeyEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Coach Login :");
        builder.setMessage("Please Enter secret Key :");
        builder.setPositiveButton("Ok", this);
        builder.setNegativeButton("Cancel", null);
        builder.setView(getKeyEditText);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        String enteredKey = getKeyEditText.getText().toString();
        if (enteredKey.equals("1111")) {
            coachLoginAlertDialogInterface.okButtonClicked(enteredKey);
            dialog.dismiss();
        }
        else {
            Toast.makeText(getActivity(),"Invalid Key, Try again ...", Toast.LENGTH_LONG).show();
        }
    }
}
