package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ShowUpAndDownVoteGiversListActivity extends AppCompatActivity {

    private RecyclerView up_down_votes_list_recycler_view;
    private Query query;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private String user_unique_id;
    private String up_down_choice;
    private String selectedUserCatregory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_up_and_down_vote_givers_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching List..");
        showProgressDialog();


        Bundle Bundle = getIntent().getExtras();
        if (Bundle != null) {
           user_unique_id  = Bundle.getString("user_unique_id");
            up_down_choice = Bundle.getString("up_down_choice");
        }

        if (up_down_choice.equals("UP")){
            getSupportActionBar().setTitle("Up Voters");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("all_"+ MainActivity.logged_in_user_category +"_Details").child(user_unique_id+"/upVotes_to_me");
        }
        else if (up_down_choice.equals("DOWN")){
            getSupportActionBar().setTitle("Down Voters");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("all_"+ MainActivity.logged_in_user_category +"_Details").child(user_unique_id+"/downVotes_to_me");
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        up_down_votes_list_recycler_view = (RecyclerView) findViewById(R.id.up_down_votes_list_recycler_view);
        query = databaseReference.orderByChild("Name");

        up_down_votes_list_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        showAllPeople();
    }

    public void showAllPeople() {

        FirebaseRecyclerAdapter<AllCoachListModels, ShowUpAndDownVoteGiversListActivity.AllCoachListViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<AllCoachListModels, ShowUpAndDownVoteGiversListActivity.AllCoachListViewHolder>(AllCoachListModels.class,
                        R.layout.all_coach_list_model,
                        ShowUpAndDownVoteGiversListActivity.AllCoachListViewHolder.class,
                        query) {


                    @Override
                    protected void populateViewHolder(ShowUpAndDownVoteGiversListActivity.AllCoachListViewHolder viewHolder, final AllCoachListModels model, int position) {

                        viewHolder.setCoach_name(model.getName());
                        viewHolder.setCoach_image(getApplicationContext(), model.getProfileImageUrl());

                        selectedUserCatregory = model.getUser_category();
                        if (selectedUserCatregory.equals("Parent")){
                            viewHolder.see_coach_profile_btn.setVisibility(View.GONE);
                        }

                        viewHolder.see_coach_profile_btn.setOnClickListener(new View.OnClickListener() {
                            Intent show_selected_person_profile_intent;
                            @Override
                            public void onClick(View v) {
                                if (selectedUserCatregory.equals("Coach")) {
                                    show_selected_person_profile_intent = new Intent(ShowUpAndDownVoteGiversListActivity.this, CoachProfileActivity.class);
                                    show_selected_person_profile_intent.putExtra("selected_coach_unique_id",model.getUniqueUserId());
                                }
                                else if (selectedUserCatregory.equals("Player")){
                                    show_selected_person_profile_intent = new Intent(ShowUpAndDownVoteGiversListActivity.this, PlayerProfileActivity.class);
                                    show_selected_person_profile_intent.putExtra("selected_player_unique_id",model.getUniqueUserId());
                                }
                                startActivity(show_selected_person_profile_intent);

                            }
                        });
                        hideProgressDialog();
                    }
                };

        up_down_votes_list_recycler_view.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllCoachListViewHolder extends RecyclerView.ViewHolder {

        View allCoachView;
        private Button see_coach_profile_btn;

        public AllCoachListViewHolder(View itemView) {
            super(itemView);
            allCoachView = itemView;

            see_coach_profile_btn = (Button) itemView.findViewById(R.id.coach_view_profile_btn);

        }

        public void setCoach_name(String coach_name) {
            TextView coach_name_tv = (TextView) allCoachView.findViewById(R.id.all_coach_name_tv);
            coach_name_tv.setText(coach_name);
        }

        public void setCoach_image(Context ctx, String coach_image_url) {
            CircularImageView coach_image_iv = (CircularImageView) allCoachView.findViewById(R.id.all_coach_image_iv);
            Picasso.with(ctx)
                    .load(coach_image_url)
                    .into(coach_image_iv);
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

    // method to show progress dialog while signing in
    public void showProgressDialog() {
        progressDialog.show();
    }

    // method to show progress dialog
    public void hideProgressDialog() {
        progressDialog.cancel();
    }
}
