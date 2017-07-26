package com.example.kashif.coachauthapp2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CoachProfileEditActivity extends AppCompatActivity {

    private TextView coach_name_tv, coach_mail_tv;
    private EditText coach_city_et, coach_mobile_et, coach_experience_et, coach_brief_et;
    private ImageView coach_image_iv;
    Button coach_submit_btn;

    String coach_name, coach_mail_id, coach_dob, coach_city, coach_mobile, coach_experience, coach_brief, coach_image_url, coach_unique_user_id;

    private TextView tvDisplayDate;
    private Button btnChangeDate;

    private int year;
    private int month;
    private int day;
    private int iDay;
    private int iMonth;
    private int iYear;
    private int age;

    private Calendar calendar;

    static final int DATE_DIALOG_ID = 999;

    DatePickerDialog dialog;

    private Map<String, Object> coach_details_hashmap;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_profile_edit);


        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("all_Coach_Details");

        coach_name_tv = (TextView) findViewById(R.id.coach_full_name_tv);
        coach_mail_tv = (TextView) findViewById(R.id.coach_email_tv);
        coach_city_et = (EditText) findViewById(R.id.coach_city_address_et);
        coach_mobile_et = (EditText) findViewById(R.id.coach_mobile_et);
        coach_experience_et = (EditText) findViewById(R.id.coach_previous_organisation_et);
        coach_brief_et = (EditText) findViewById(R.id.coach_brief_et);
        coach_submit_btn = (Button) findViewById(R.id.coach_submit_btn);
        tvDisplayDate = (TextView) findViewById(R.id.coach_dob_tv);
        coach_image_iv = (ImageView) findViewById(R.id.coach_image_iv);

        setCurrentDateOnView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            coach_name = bundle.getString("coach_name");
            coach_mail_id = bundle.getString("coach_mail_id");
            coach_image_url = bundle.getString("coach_image_url");
            coach_unique_user_id = bundle.getString("coach_unique_user_id");
            coach_brief = bundle.getString("coach_brief");
            coach_experience = bundle.getString("coach_experience");
            coach_mobile = bundle.getString("coach_mobile");
            coach_city = bundle.getString("coach_city");
            coach_dob = bundle.getString("coach_dob");
        }

        if (coach_name != null) {
            coach_name_tv.setText(coach_name);
            coach_mail_tv.setText(coach_mail_id);

            Picasso.with(CoachProfileEditActivity.this)
                    .load(coach_image_url)
                    .into(coach_image_iv);
        }

        if (coach_dob != null) {
            tvDisplayDate.setText(coach_dob);
        }
        if (coach_city != null) {
            coach_city_et.setText(coach_city);
        }
        if (coach_mobile != null) {
            coach_mobile_et.setText(coach_mobile);
        }
        if (coach_experience != null) {
            coach_experience_et.setText(coach_experience);
        }
        if (coach_brief != null) {
            coach_brief_et.setText(coach_brief);
        }


        coach_submit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tvDisplayDate.getText().toString().length() == 0) {
                    tvDisplayDate.setError("Please enter Date of Birth ");
                } else if (coach_city_et.getText().toString().length() == 0) {
                    coach_city_et.setError("Please enter City ");

                } else if (coach_mobile_et.getText().toString().length() == 0) {
                    coach_mobile_et.setError("Please enter Mobile No.");
                } else if (coach_mobile_et.getText().toString().length() < 10) {
                    coach_mobile_et.setError("Should be of 10 digits");
                } else if (coach_experience_et.getText().toString().length() == 0) {
                    coach_experience_et.setError("Please enter Experience");
                } else if (coach_brief_et.getText().toString().length() == 0) {
                    coach_brief_et.setError("Please brief yourself");
                } else {
                    String dobSplit[] = tvDisplayDate.getText().toString().split("-");
                    calendar = Calendar.getInstance();
                    calendar.set(Integer.parseInt(dobSplit[2]), Integer.parseInt(dobSplit[0]), Integer.parseInt(dobSplit[1]));

                    iDay = calendar.get(Calendar.DATE);
                    iMonth = calendar.get(Calendar.MONTH) - 1;
                    iYear = calendar.get(Calendar.YEAR);

                    Calendar calenderToday = Calendar.getInstance();
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

                    coach_Details_submit();
                    databaseReference.child(coach_unique_user_id).updateChildren(coach_details_hashmap);

                    finish();
                }
            }
        });
        addListenerOnButton();

    }

    public void coach_Details_submit() {

        if (age == 0){
            Toast.makeText(getApplicationContext(),"Age can not be 0 years", Toast.LENGTH_SHORT).show();
            tvDisplayDate.setError("Age can not be 0 years");
        }
        else {

            coach_dob = tvDisplayDate.getText().toString();
            coach_city = coach_city_et.getText().toString();
            coach_experience = coach_experience_et.getText().toString();
            coach_brief = coach_brief_et.getText().toString();
            coach_mobile = coach_mobile_et.getText().toString();
            coach_details_hashmap = new HashMap<>();
            coach_details_hashmap.put("coach_dob", coach_dob);
            coach_details_hashmap.put("coach_city", coach_city);
            coach_details_hashmap.put("coach_experience", coach_experience);
            coach_details_hashmap.put("coach_brief", coach_brief);
            coach_details_hashmap.put("coach_mobile", coach_mobile);
            coach_details_hashmap.put("coach_age", age);
        }
    }

    public void setCurrentDateOnView() {

        //  dpResult = (DatePicker) findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year));

        // set current date into datepicker
        // dpResult.init(year, month, day, null);

    }

    public void addListenerOnButton() {

        btnChangeDate = (Button) findViewById(R.id.coach_dob_btn);

        btnChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //showDialog(DATE_DIALOG_ID);
                lastStoreDate();
            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date

                dialog = new DatePickerDialog(this, datePickerListener,
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
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year));

            // set selected date into datepicker also
            //  dpResult.init(year, month, day, null);

        }
    };

    public void lastStoreDate() {

        if (tvDisplayDate.getText().toString() == null || tvDisplayDate.getText().toString().trim().length() == 0) {
            showDialog(DATE_DIALOG_ID);
        } else {
            String dobSplit[] = tvDisplayDate.getText().toString().split("-");
            calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(dobSplit[2]), Integer.parseInt(dobSplit[0]), Integer.parseInt(dobSplit[1]));

            iDay = calendar.get(Calendar.DATE);
            iMonth = calendar.get(Calendar.MONTH) - 1;
            iYear = calendar.get(Calendar.YEAR);
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


                    Calendar calenderToday = Calendar.getInstance();
                    int currentYear = calenderToday.get(Calendar.YEAR);
                    int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
                    int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);
                    age = currentYear - year;

                    if (monthOfYear > currentMonth) {
                        --age;
                    } else if (monthOfYear == currentMonth) {
                        if (dayOfMonth > todayDay) {
                            --age;
                        }
                    }
                }
            }, iYear, iMonth, iDay);
            // dbDialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            dbDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


            dbDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected_id = item.getItemId();
        switch (selected_id) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
