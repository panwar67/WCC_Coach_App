package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
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

public class PlayerProfileActivity extends AppCompatActivity {


    private TextView username_tv;
    private TextView usermail_tv;
    private TextView user_skills_tv;
    private TextView user_achievement_tv;
    private TextView user_role_tv;
    private TextView user_batting_style_tv;
    private TextView user_bowling_style_tv;
    private TextView user_current_city_tv;
    private TextView player_upVotes_count_no_tv;
    private TextView player_downVotes_count_no_tv;


    private Button upVote_player_button;
    private Button downVote_player_button;



    String user_name;
    String user_mail;
    private String user_image_url;
    String user_dob;
    String user_role;
    String user_current_city;
    String user_batting_style;
    String user_bowling_style;
    String skills;
    String achievements;
    long player_upVotes_count;
    long player_downVotes_count;

    private String player_unique_id;
    private String user_mobile_no;
    private String user_age;

    private boolean playerIsLoggedUser = false;

    private String received_player_unique_id;

    private FloatingActionButton player_profile_edit_fab;

    private ProgressDialog progressDialog;
    private DatabaseReference player_databaseReference;

    FirebaseUser user;

    private Map<String, Object> currentUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Profile..");
        showProgressDialog();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        player_profile_edit_fab = (FloatingActionButton) findViewById(R.id.player_profile_edit_fab);


        username_tv = (TextView) findViewById(R.id.user_profile_name_tv);
        usermail_tv = (TextView) findViewById(R.id.user_profile_email_tv);
        user_skills_tv = (TextView) findViewById(R.id.user_profile_skill_tv);
        user_achievement_tv = (TextView) findViewById(R.id.user_profile_achievements_tv);
        user_current_city_tv = (TextView) findViewById(R.id.user_profile_address_city_tv);
        user_role_tv = (TextView) findViewById(R.id.user_profile_role__tv);
        user_batting_style_tv = (TextView) findViewById(R.id.user_batting_style__tv);
        user_bowling_style_tv = (TextView) findViewById(R.id.user_bowling_style__tv);
        player_upVotes_count_no_tv = (TextView) findViewById(R.id.player_upvotes_count_no_tv);
        player_downVotes_count_no_tv = (TextView) findViewById(R.id.player_downvotes_count_no_tv);

        upVote_player_button = (Button) findViewById(R.id.upvote_player_button);
        downVote_player_button = (Button) findViewById(R.id.downvote_player_button);

        final CircularImageView userimage_iv = (CircularImageView) findViewById(R.id.user_profile_imageview);

        userimage_iv.setBorderColor(getResources().getColor(R.color.com_facebook_button_background_color));
        userimage_iv.setBorderWidth(8);
        userimage_iv.setShadowRadius(1);


        // getting current logged user unique id
        user = FirebaseAuth.getInstance().getCurrentUser();
        player_unique_id = user.getUid();

        Bundle Bundle = getIntent().getExtras();
        if (Bundle != null) {
            received_player_unique_id = Bundle.getString("selected_player_unique_id");
        }

        // checking if logged user is opening his own profile or someone else is opening his profile
        if (!player_unique_id.equals(received_player_unique_id) && received_player_unique_id!= null){
            player_unique_id = received_player_unique_id;
        }

        // changing layout if logged user is opening his profile
        if (player_unique_id == user.getUid()){
            player_profile_edit_fab.setVisibility(View.VISIBLE);
            upVote_player_button.setVisibility(View.GONE);
            downVote_player_button.setVisibility(View.GONE);
            playerIsLoggedUser = true;

        }


        player_databaseReference = FirebaseDatabase.getInstance().getReference().child("all_Player_Details/"+ player_unique_id);
        player_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                player_upVotes_count = dataSnapshot.child("upVotes_to_me").getChildrenCount();
                player_downVotes_count = dataSnapshot.child("downVotes_to_me").getChildrenCount();
                user_name = dataSnapshot.child("Name").getValue(String.class);
                user_image_url = dataSnapshot.child("ProfileImageUrl").getValue(String.class);
                user_mail = dataSnapshot.child("Email").getValue(String.class);
                user_dob = dataSnapshot.child("user_dob").getValue(String.class);
                user_role = dataSnapshot.child("user_role").getValue(String.class);
                user_age = String.valueOf(dataSnapshot.child("user_age").getValue(long.class));
                user_mobile_no = dataSnapshot.child("user_mobile_no").getValue(String.class);
                user_current_city = dataSnapshot.child("user_address_city").getValue(String.class);
                user_batting_style = dataSnapshot.child("user_batting_hand").getValue(String.class);
                user_bowling_style = dataSnapshot.child("user_bowling_hand").getValue(String.class);
                skills = dataSnapshot.child("user_skills").getValue(String.class);
                achievements = dataSnapshot.child("user_achievement").getValue(String.class);


                username_tv.setText(user_name);
                usermail_tv.setText(user_mail);

                if (player_unique_id == user.getUid() && user_dob == null){
                    goToPlayerProfileEditActivity();
                }
                else if (user_dob != null){
                    username_tv.setText(user_name + " (" + user_age+")");
                    user_current_city_tv.setVisibility(View.VISIBLE);
                    user_current_city_tv.setText(user_current_city + " | " + user_mobile_no);
                }
                else {
                    username_tv.setText(user_name);
                    user_current_city_tv.setVisibility(View.GONE);
                }

                Picasso.with(PlayerProfileActivity.this)
                        .load(user_image_url)
                        .into(userimage_iv);

                user_role_tv.setText(user_role);
                user_batting_style_tv.setText(user_batting_style);
                user_bowling_style_tv.setText(user_bowling_style);
                user_skills_tv.setText(skills);
                user_achievement_tv.setText(achievements);

                player_upVotes_count_no_tv.setText("" + player_upVotes_count);
                player_downVotes_count_no_tv.setText("" + player_downVotes_count);


                if (playerIsLoggedUser == true) {
                    getSupportActionBar().setTitle("Welcome " + user_name);
                }
                else {
                    getSupportActionBar().setTitle(user_name + "'s Profile");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // method on floating action button
        player_profile_edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayerProfileEditActivity();
            }
        });

        // setting current logged in user details into hashmap
        currentUserDetails = new HashMap<>();
        currentUserDetails.put("Name", user.getDisplayName());
        currentUserDetails.put("UniqueUserId", user.getUid());
        currentUserDetails.put("ProfileImageUrl", user.getPhotoUrl().toString());
        currentUserDetails.put("user_category", MainActivity.logged_in_user_category);


        // method on upVote button click
        upVote_player_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUpVoteToPlayer();
            }
        });

        // method on downVote button click
        downVote_player_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveDownVoteToPlayer();
            }
        });
    }


    // method to up vote coach on button click
    public void giveUpVoteToPlayer(){
        player_databaseReference.child("downVotes_to_me").child(user.getUid()).removeValue();
        player_databaseReference.child("upVotes_to_me").child(user.getUid()).setValue(currentUserDetails);
    }

    // method to up vote coach on button click
    public void giveDownVoteToPlayer(){
        player_databaseReference.child("upVotes_to_me").child(user.getUid()).removeValue();
        player_databaseReference.child("downVotes_to_me").child(user.getUid()).setValue(currentUserDetails);
    }

    // method to show DownVoteGiversList
    public void showDownVoteGiversList(View view) {
        if (player_unique_id == user.getUid() && player_downVotes_count !=0) {
            Intent intent = new Intent(this, ShowUpAndDownVoteGiversListActivity.class);
            intent.putExtra("user_unique_id", player_unique_id)
                    .putExtra("up_down_choice", "DOWN");
            startActivity(intent);
        }
    }

    // method to show UpVoteGiversList
    public void showUpVoteGiversList(View view){
        if (player_unique_id == user.getUid() && player_upVotes_count != 0) {
            Intent intent = new Intent(this, ShowUpAndDownVoteGiversListActivity.class);
            intent.putExtra("user_unique_id", player_unique_id)
                    .putExtra("up_down_choice", "UP");
            startActivity(intent);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    // method to show progress dialog while signing in
    public void showProgressDialog() {
        progressDialog.show();
    }

    // method to show progress dialog
    public void hideProgressDialog() {
        progressDialog.cancel();
    }

    // method to go to player profile edit activity
    public void goToPlayerProfileEditActivity(){
        Intent edit_your_profile_intent = new Intent(PlayerProfileActivity.this, PlayerProfileEditActivity.class)
                            .putExtra("username", user_name)
                            .putExtra("usermail", user_mail)
                            .putExtra("userimageurl", user_image_url)
                            .putExtra("uniqueUserId", player_unique_id)
                            .putExtra("user_dob", user_dob)
                            .putExtra("user_role", user_role)
                            .putExtra("user_skills", skills)
                            .putExtra("user_achievements", achievements)
                            .putExtra("user_current_city", user_current_city)
                            .putExtra("user_mobile_no", user_mobile_no);

        startActivity(edit_your_profile_intent);
    }
}

