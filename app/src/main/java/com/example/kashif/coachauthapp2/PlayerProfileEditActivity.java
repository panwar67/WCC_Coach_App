package com.example.kashif.coachauthapp2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class PlayerProfileEditActivity extends AppCompatActivity {


    private String username;
    private String usermail;
    private String userimageurl;
    private String user_skills;
    private String user_achievement;
    private String user_dob;
    private String user_address_city;
    private String user_batting_hand;
    private String user_bowling_hand;
    private String user_wicketkeeper;
    private String user_bundle_skills;
    private String user_bundle_achievement;
    private String user_bundle_address_city;
    private String user_mobile_no;

    private String user_bundle_dob;

    private Calendar calenderToday;
    private Calendar calendar;


    String selected_primary_skill;
    String selected_secondary_skill;
    String user_role;
    String user_bundle_mobile_no;


    String unique_user_Id;

    private TextView tvDisplayDate;
    private Button btnChangeDate;

    private int year;
    private int month;
    private int day;
    private int iDay;
    private int iMonth;
    private int iYear;

    static final int DATE_DIALOG_ID = 999;
    private int age;


    private TextView user_name_tv;
    private ImageView user_image_iv;
    private TextView user_email_tv;
    private TextView user_batting_style_tv;
    private TextView user_bowling_style_tv;
    private EditText user_mobile_et;
    private EditText user_address_city_et;
    private EditText user_skill_et;
    private EditText user_achievement_et;

    private Button user_credential_save_button;

    private Spinner user_primary_skill_spinner;
    private Spinner user_secondary_skill_spinner;
    private Spinner user_batting_skill_spinner;
    private Spinner user_bowling_skill_spinner;

    private ArrayAdapter<CharSequence> user_primary_skill_adapter;
    private ArrayAdapter<CharSequence> user_secondary_skill_adapter;
    private ArrayAdapter<CharSequence> user_batting_skill_adapter;
    private ArrayAdapter<CharSequence> user_bowling_skill_adapter;

    private HashMap<String, Object> user_edit_profile_details;

    DatabaseReference update_player_details_databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile_edit);


        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        user_name_tv = (TextView) findViewById(R.id.user_name_tv);
        user_email_tv = (TextView) findViewById(R.id.user_email_tv);
        user_batting_style_tv = (TextView) findViewById(R.id.user_batting_style__tv);
        user_bowling_style_tv = (TextView) findViewById(R.id.user_bowling_style__tv);
        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
        user_mobile_et = (EditText) findViewById(R.id.user_mobile_et);


        user_image_iv = (ImageView) findViewById(R.id.user_image_iv);


        user_address_city_et = (EditText) findViewById(R.id.user_address_city_et);
        user_skill_et = (EditText) findViewById(R.id.user_skills_et);
        user_achievement_et = (EditText) findViewById(R.id.user_achievement_et);

        user_credential_save_button = (Button) findViewById(R.id.submit_btn);


        user_primary_skill_spinner = (Spinner) findViewById(R.id.user_skill_set_primary_spinner);
        user_secondary_skill_spinner = (Spinner) findViewById(R.id.user_skill_set_secondary_spinner);
        user_batting_skill_spinner = (Spinner) findViewById(R.id.user_batsman_skill_set_spinner);
        user_bowling_skill_spinner = (Spinner) findViewById(R.id.user_bowler_skill_set_spinner);


        setCurrentDateOnView();

        Bundle Bundle = getIntent().getExtras();
        if (Bundle != null) {
            username = Bundle.getString("username");
            usermail = Bundle.getString("usermail");
            userimageurl = Bundle.getString("userimageurl");
            unique_user_Id = Bundle.getString("uniqueUserId");
            user_bundle_skills = Bundle.getString("user_skills");
            user_bundle_achievement = Bundle.getString("user_achievements");
            user_bundle_address_city = Bundle.getString("user_current_city");
            user_bundle_dob = Bundle.getString("user_dob");
            user_bundle_mobile_no = Bundle.getString("user_mobile_no");

        }

        if (username != null) {
            Picasso.with(PlayerProfileEditActivity.this)
                    .load(userimageurl)
                    .into(user_image_iv);


            user_name_tv.setText(username);
            user_email_tv.setText(usermail);
        }


        if (user_bundle_skills != null) {
            user_skill_et.setText(user_bundle_skills);
        }

        if (user_bundle_achievement != null) {
            user_achievement_et.setText(user_bundle_achievement);
        }

        if (user_bundle_address_city != null) {
            user_address_city_et.setText(user_bundle_address_city);
        }

        if (user_bundle_dob != null) {
            tvDisplayDate.setText(user_bundle_dob);
        }
        if (user_bundle_mobile_no != null) {
            user_mobile_et.setText(user_bundle_mobile_no);
        }


        user_primary_skill_adapter = ArrayAdapter.createFromResource(this, R.array.user_primary_skill_set_array, android.R.layout.simple_spinner_item);
        user_primary_skill_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_primary_skill_spinner.setAdapter(user_primary_skill_adapter);

        user_secondary_skill_adapter = ArrayAdapter.createFromResource(this, R.array.user_secondary_skill_set_array, android.R.layout.simple_spinner_item);
        user_secondary_skill_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_secondary_skill_spinner.setAdapter(user_secondary_skill_adapter);

        user_batting_skill_adapter = ArrayAdapter.createFromResource(this, R.array.user_batting_skill_set_array, android.R.layout.simple_spinner_item);
        user_batting_skill_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_batting_skill_spinner.setAdapter(user_batting_skill_adapter);


        user_bowling_skill_adapter = ArrayAdapter.createFromResource(this, R.array.user_bowling_skill_set_array, android.R.layout.simple_spinner_item);
        user_bowling_skill_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_bowling_skill_spinner.setAdapter(user_bowling_skill_adapter);

        user_batting_skill_spinner.setVisibility(View.INVISIBLE);
        user_batting_style_tv.setVisibility(View.INVISIBLE);
        user_bowling_skill_spinner.setVisibility(View.INVISIBLE);
        user_bowling_style_tv.setVisibility(View.INVISIBLE);

        user_primary_skill_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_primary_skill = parent.getItemAtPosition(position).toString();

                if (selected_primary_skill == user_primary_skill_spinner.getItemAtPosition(0)) {
                    user_bowling_hand = "-";
                    user_bowling_skill_spinner.setVisibility(View.GONE);
                    user_bowling_style_tv.setVisibility(View.GONE);
                    user_batting_skill_spinner.setVisibility(View.VISIBLE);
                    user_batting_style_tv.setVisibility(View.VISIBLE);

                } else if (selected_primary_skill == user_primary_skill_spinner.getItemAtPosition(1)) {
                    user_batting_hand = "-";
                    user_batting_skill_spinner.setVisibility(View.GONE);
                    user_batting_style_tv.setVisibility(View.GONE);
                    user_bowling_skill_spinner.setVisibility(View.VISIBLE);
                    user_bowling_style_tv.setVisibility(View.VISIBLE);

                } else {
                    user_batting_skill_spinner.setVisibility(View.VISIBLE);
                    user_batting_style_tv.setVisibility(View.VISIBLE);
                    user_bowling_skill_spinner.setVisibility(View.VISIBLE);
                    user_bowling_style_tv.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        user_secondary_skill_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_secondary_skill = parent.getItemAtPosition(position).toString();

                if (selected_secondary_skill == user_secondary_skill_spinner.getItemAtPosition(1)) {
                    user_wicketkeeper = selected_secondary_skill;
                } else {
                    return;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        user_batting_skill_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                user_batting_hand = parent.getItemAtPosition(position).toString();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        user_bowling_skill_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user_bowling_hand = parent.getItemAtPosition(position).toString();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        user_credential_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user_mobile_et.getText().toString().length() < 10) {
                    user_mobile_et.setError("Should be 10 digits");
                }

                else if (user_skill_et.getText().toString().length() == 0) {
                    user_skill_et.setError("Skills not entered");
                }
                else if (user_achievement_et.getText().toString().length() == 0) {
                    user_achievement_et.setError("Achievements not entered");
                }
                else if (user_address_city_et.getText().toString().length() == 0) {
                    user_address_city_et.setError("City not entered");

                }
                else if (selected_secondary_skill == user_secondary_skill_spinner.getItemAtPosition(0)) {
                    user_secondary_skill_spinner.requestFocus();
                    Toast.makeText(getApplicationContext(), "Are You wicketKeeper ?", Toast.LENGTH_SHORT).show();
                }
                else {
                    String dobSplit[] = tvDisplayDate.getText().toString().split("-");
                    calendar = Calendar.getInstance();
                    calendar.set(Integer.parseInt(dobSplit[2]), Integer.parseInt(dobSplit[0]), Integer.parseInt(dobSplit[1]));
                    iDay = calendar.get(Calendar.DATE);
                    iMonth = calendar.get(Calendar.MONTH) - 1;
                    iYear = calendar.get(Calendar.YEAR);
                    calenderToday = Calendar.getInstance();
                    int currentYear = calenderToday.get(Calendar.YEAR);
                    int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
                    int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);
                    age = currentYear - iYear;

                    if (iMonth > currentMonth) {
                        --age;
                    } else if (iMonth == currentMonth) {
                        if (iDay > todayDay) {
                            --age;
                        }
                    }
                    dataStorage();
                    update_player_details_databaseReference = FirebaseDatabase.getInstance().getReference();


                    if (age <= 15) {
                        update_player_details_databaseReference.child("all_under15_Players").child(unique_user_Id).updateChildren(user_edit_profile_details);
                        update_player_details_databaseReference.child("all_Player_Details").child(unique_user_Id).updateChildren(user_edit_profile_details);
                        update_player_details_databaseReference.child("all_under19_Players").child(unique_user_Id).removeValue();

                        finish();
                    } else if (age > 15 && age <= 19) {
                        update_player_details_databaseReference.child("all_under19_Players").child(unique_user_Id).updateChildren(user_edit_profile_details);
                        update_player_details_databaseReference.child("all_Player_Details").child(unique_user_Id).updateChildren(user_edit_profile_details);
                        update_player_details_databaseReference.child("all_under15_Players").child(unique_user_Id).removeValue();

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Age should be <= 19 years", Toast.LENGTH_SHORT).show();
                        tvDisplayDate.setError("Age should be <= 19 years");
                    }
                }
            }

        });

        addListenerOnButton();

    }

    //Calender
    public void setCurrentDateOnView() {


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year));


    }

    public void addListenerOnButton() {

        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);

        btnChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // showDialog(DATE_DIALOG_ID);
                lastStoreDate();
            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date

                DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener,
                        year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                return dialog;

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year));

        }
    };

    public void lastStoreDate() {

        if (tvDisplayDate.getText().toString() == null || tvDisplayDate.getText().toString().trim().length() == 0) {
            showDialog(DATE_DIALOG_ID);
        }
        else {
            String dobSplit[] = tvDisplayDate.getText().toString().split("-");
            calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(dobSplit[2]), Integer.parseInt(dobSplit[0]), Integer.parseInt(dobSplit[1]));
            iDay = calendar.get(Calendar.DATE);
            iMonth = calendar.get(Calendar.MONTH) - 1;
            iYear = calendar.get(Calendar.YEAR);
            calenderToday = Calendar.getInstance();
            int currentYear = calenderToday.get(Calendar.YEAR);
            int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
            int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);
            age = currentYear - iYear;

            if (iMonth > currentMonth) {
                --age;
            } else if (iMonth == currentMonth) {
                if (iDay > todayDay) {
                    --age;
                }
            }
            DatePickerDialog dbDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    StringBuilder date = new StringBuilder();

                    monthOfYear = monthOfYear + 1;
                    if (monthOfYear < 10) {
                        date.append("0" + monthOfYear + "-" + dayOfMonth);
                    } else {
                        date.append(monthOfYear).append("-" + dayOfMonth);
                    }
                    date.append("-" + year);
                    tvDisplayDate.setText(date.toString());
                    calenderToday = Calendar.getInstance();
                    int currentYear = calenderToday.get(Calendar.YEAR);
                    int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
                    int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);
                    age = currentYear - iYear;

                    if (iMonth > currentMonth) {
                        --age;
                    } else if (iMonth == currentMonth) {
                        if (iDay > todayDay) {
                            --age;
                        }
                    }

                }
            }, iYear, iMonth, iDay);

            dbDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dbDialog.show();
        }
    }

    public void dataStorage() {
        if (selected_primary_skill.equals("Batsman")) {
            user_bowling_hand = "-";
        } else if (selected_primary_skill.equals("Bowler")) {
            user_batting_hand = "-";
        }
        if (selected_secondary_skill == user_wicketkeeper) {
            user_role = ("WicketKeeper" + "-" + selected_primary_skill);
        } else {
            user_role = selected_primary_skill;
        }
        user_dob = tvDisplayDate.getText().toString();
        user_skills = user_skill_et.getText().toString();
        user_achievement = user_achievement_et.getText().toString();
        user_address_city = user_address_city_et.getText().toString();
        user_mobile_no = user_mobile_et.getText().toString();


        user_edit_profile_details = new HashMap<>();
        user_edit_profile_details.put("Name", username);
        user_edit_profile_details.put("Email", usermail);
        user_edit_profile_details.put("UniqueUserId", unique_user_Id);
        user_edit_profile_details.put("ProfileImageUrl", userimageurl);
        user_edit_profile_details.put("user_dob", user_dob);
        user_edit_profile_details.put("user_address_city", user_address_city);
        user_edit_profile_details.put("user_skills", user_skills);
        user_edit_profile_details.put("user_role", user_role);

        user_edit_profile_details.put("user_age", age);
        user_edit_profile_details.put("user_mobile_no", user_mobile_no);
        user_edit_profile_details.put("user_achievement", user_achievement);
        user_edit_profile_details.put("user_batting_hand", user_batting_hand);
        user_edit_profile_details.put("user_bowling_hand", user_bowling_hand);
        user_edit_profile_details.put("user_wicketkeeper", user_wicketkeeper);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
