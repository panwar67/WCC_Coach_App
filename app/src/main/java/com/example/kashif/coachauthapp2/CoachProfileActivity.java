package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CoachProfileActivity extends AppCompatActivity {


    private TextView coach_name_age_tv;
    private TextView coach_mail_tv;
    private TextView coach_city_mobile_tv;
    private TextView coach_about_tv;
    private TextView coach_upVotes_count_no_tv;
    private TextView coach_downVotes_count_no_tv;
    private TextView coach_experience_tv;

    private Button upVote_coach_button;
    private Button downVote_coach_button;

    private String coach_name, coach_age, coach_dob, coach_mobile, coach_experience, coach_brief, coach_mail_id, coach_city, coach_unique_id;
    private long coach_upVotes_count_no, coach_downVotes_count_no;
    private String coach_image_uri;
    private CircularImageView coach_userimage_iv;

    private String received_coach_unique_id;
    private boolean coachIsLoggedUser = false;

    private FloatingActionButton coach_profile_edit_fab;
    private ProgressDialog progressDialog;

    private DatabaseReference coachdatabaseReference;
    private FirebaseUser user;

    private Map<String, Object> currentUserDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Profile..");
        showProgressDialog();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // getting unique id of current logged user
        user = FirebaseAuth.getInstance().getCurrentUser();
        coach_unique_id = user.getUid();


        coach_profile_edit_fab = (FloatingActionButton) findViewById(R.id.fab_edit_coach_profile);

        coach_name_age_tv = (TextView) findViewById(R.id.coach_name_age_tv);
        coach_mail_tv = (TextView) findViewById(R.id.coach_mail_tv);
        coach_city_mobile_tv = (TextView) findViewById(R.id.coach_city_mobile_tv);
        coach_about_tv = (TextView) findViewById(R.id.coach_about_tv);
        coach_upVotes_count_no_tv = (TextView) findViewById(R.id.coach_up_voted_no_tv);
        coach_downVotes_count_no_tv = (TextView) findViewById(R.id.coach_down_voted_no_tv);
        coach_experience_tv = (TextView) findViewById(R.id.coach_experience_tv);

        //   coach_image_iv  = (ImageView)findViewById(R.id.coach_profile_imageview);

        upVote_coach_button = (Button) findViewById(R.id.coach_up_voted_btn);
        downVote_coach_button = (Button) findViewById(R.id.coach_down_voted_btn);


        coach_userimage_iv = (CircularImageView) findViewById(R.id.coach_profile_imageview);

        coach_userimage_iv.setBorderColor(getResources().getColor(R.color.colorAccent));
        coach_userimage_iv.setBorderWidth(8);
        coach_userimage_iv.setShadowRadius(1);

        Bundle Bundle = getIntent().getExtras();
        if (Bundle != null) {
            received_coach_unique_id = Bundle.getString("selected_coach_unique_id");
        }

        if (!coach_unique_id.equals(received_coach_unique_id) && received_coach_unique_id!= null){
            coach_unique_id = received_coach_unique_id;
        }

        // changing layout if logged user is opening his profile
        if (coach_unique_id == user.getUid()){
            coach_profile_edit_fab.setVisibility(View.VISIBLE);
            upVote_coach_button.setVisibility(View.GONE);
            downVote_coach_button.setVisibility(View.GONE);
            coachIsLoggedUser = true;

        }


        coachdatabaseReference = FirebaseDatabase.getInstance().getReference().child("all_Coach_Details/" + coach_unique_id);
        coachdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                coach_upVotes_count_no = dataSnapshot.child("upVotes_to_me").getChildrenCount();
                coach_downVotes_count_no = dataSnapshot.child("downVotes_to_me").getChildrenCount();
                coach_name = dataSnapshot.child("Name").getValue(String.class);
                coach_mail_id = dataSnapshot.child("Email").getValue(String.class);
                coach_image_uri = dataSnapshot.child("ProfileImageUrl").getValue(String.class);
                coach_age = String.valueOf(dataSnapshot.child("coach_age").getValue(long.class));
                coach_experience = dataSnapshot.child("coach_experience").getValue(String.class);
                coach_city = dataSnapshot.child("coach_city").getValue(String.class);
                coach_brief = dataSnapshot.child("coach_brief").getValue(String.class);
                coach_mobile = dataSnapshot.child("coach_mobile").getValue(String.class);
                coach_dob = dataSnapshot.child("coach_dob").getValue(String.class);

                coach_upVotes_count_no_tv.setText("" + coach_upVotes_count_no);
                coach_downVotes_count_no_tv.setText("" + coach_downVotes_count_no);

                coach_name_age_tv.setText(coach_name);
                coach_mail_tv.setText(coach_mail_id);

                if (coach_unique_id == user.getUid() && coach_dob == null){
                    goToCoachProfileEditActivity();
                }

                else if (coach_dob != null){
                    coach_name_age_tv.setText(coach_name + " | " + coach_age);
                    coach_city_mobile_tv.setVisibility(View.VISIBLE);
                    coach_city_mobile_tv.setText(coach_city + " | " + coach_mobile);
                }
                else {
                    coach_name_age_tv.setText(coach_name);
                    coach_city_mobile_tv.setVisibility(View.GONE);
                }

                coach_experience_tv.setText(coach_experience);
                coach_about_tv.setText(coach_brief);

                Picasso.with(CoachProfileActivity.this)
                        .load(coach_image_uri)
                        .into(coach_userimage_iv);

                if (coachIsLoggedUser == true) {
                    getSupportActionBar().setTitle("Welcome " + coach_name);
                }
                else {
                    getSupportActionBar().setTitle(coach_name + "'s Profile");
                }
                hideProgressDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // method on floating action button
        coach_profile_edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCoachProfileEditActivity();
            }
        });

        // setting current logged in user details into hashmap
        currentUserDetails = new HashMap<>();
        currentUserDetails.put("Name", user.getDisplayName());
        currentUserDetails.put("UniqueUserId", user.getUid());
        currentUserDetails.put("ProfileImageUrl", user.getPhotoUrl().toString());
        currentUserDetails.put("user_category", MainActivity.logged_in_user_category);


        // method on upVote button click
        upVote_coach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUpVoteToCoach();
            }
        });

        // method on downVote button click
        downVote_coach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveDownVoteToCoach();
            }
        });

    }

    // method to up vote coach on button click
    public void giveUpVoteToCoach(){
        coachdatabaseReference.child("downVotes_to_me").child(user.getUid()).removeValue();
        coachdatabaseReference.child("upVotes_to_me").child(user.getUid()).setValue(currentUserDetails);
    }

    // method to down vote coach on button click
    public void giveDownVoteToCoach(){
        coachdatabaseReference.child("upVotes_to_me").child(user.getUid()).removeValue();
        coachdatabaseReference.child("downVotes_to_me").child(user.getUid()).setValue(currentUserDetails);
    }

    // method to show DownVoteGiversList
    public void showDownVoteGiversList(View view) {
        if (coach_unique_id == user.getUid() && coach_downVotes_count_no != 0) {
            Intent intent = new Intent(this, ShowUpAndDownVoteGiversListActivity.class);
            intent.putExtra("user_unique_id", coach_unique_id)
                    .putExtra("up_down_choice", "DOWN");
            startActivity(intent);
        }
    }

    // method to show UpVoteGiversList
    public void showUpVoteGiversList(View view){
        if (coach_unique_id == user.getUid() && coach_upVotes_count_no != 0) {
            Intent intent = new Intent(this, ShowUpAndDownVoteGiversListActivity.class);
            intent.putExtra("user_unique_id", coach_unique_id)
                    .putExtra("up_down_choice", "UP");
            startActivity(intent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected_id = item.getItemId();
        switch (selected_id){
            case android.R.id.home : {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // method to open coach profile edit activity on fab click
    public void goToCoachProfileEditActivity(){
        Intent intent = new Intent(CoachProfileActivity.this, CoachProfileEditActivity.class)
                .putExtra("coach_name", coach_name)
                .putExtra("coach_mail_id", coach_mail_id)
                .putExtra("coach_image_url", coach_image_uri)
                .putExtra("coach_unique_user_id", coach_unique_id)
                .putExtra("coach_dob", coach_dob)
                .putExtra("coach_city", coach_city)
                .putExtra("coach_mobile", coach_mobile)
                .putExtra("coach_experience", coach_experience)
                .putExtra("coach_brief", coach_brief);
        startActivity(intent);
    }

    // method to show progress dialog while signing in
    public void showProgressDialog() {
        progressDialog.show();
    }

    // method to show progress dialog
    public void hideProgressDialog() {
        progressDialog.cancel();
    }
}
