package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ShowTeamActivity extends AppCompatActivity {

    private RecyclerView all_players_list_recyclerView;
    private static DatabaseReference playerList_databaseReference;
    Query query;
    private ProgressDialog progressDialog;
    private Snackbar noPlayersFoundMessageSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_players_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Team..");

        getSupportActionBar().setTitle("U-19 Team");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noPlayersFoundMessageSnackbar = Snackbar.make(findViewById(R.id.all_players_list_layout),
                "Team is not Formed Yet. Check other Team from menu option", Snackbar.LENGTH_INDEFINITE);
        all_players_list_recyclerView = (RecyclerView) findViewById(R.id.all_players_list_recycler_view);

        all_players_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        showAllPlayers("under19_Team");
    }

    public void showAllPlayers(String selectedTeamChoice) {

        showProgressDialog();
        playerList_databaseReference = FirebaseDatabase.getInstance().getReference().child(selectedTeamChoice);
        query = playerList_databaseReference.orderByChild("Name");


        playerList_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0){
                    noPlayersFoundMessageSnackbar.show();
                }
                else {
                    noPlayersFoundMessageSnackbar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<AllCoachListModels, ShowTeamActivity.AllCoachListViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<AllCoachListModels, ShowTeamActivity.AllCoachListViewHolder>(AllCoachListModels.class,
                        R.layout.all_coach_list_model,
                        ShowTeamActivity.AllCoachListViewHolder.class,
                        query) {


                    @Override
                    protected void populateViewHolder(ShowTeamActivity.AllCoachListViewHolder viewHolder, final AllCoachListModels model, int position) {

                        viewHolder.setCoach_name(model.getName());
                        viewHolder.setCoach_image(getApplicationContext(), model.getProfileImageUrl());

                        viewHolder.see_coach_profile_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent show_coach_profile_intent = new Intent(ShowTeamActivity.this, PlayerProfileActivity.class);
                                show_coach_profile_intent.putExtra("selected_player_unique_id",model.getUniqueUserId());
                                startActivity(show_coach_profile_intent);

                            }
                        });
                        hideProgressDialog();
                    }
                };

        all_players_list_recyclerView.setAdapter(firebaseRecyclerAdapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_team_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected_id = item.getItemId();
        switch (selected_id){
            case android.R.id.home : {
                finish();
                return true;
            }
            case R.id.action_u19_team_list : {
                if (getSupportActionBar().getTitle().equals("U-19 Team")){
                    return false;
                }
                showAllPlayers("under19_Team");
                getSupportActionBar().setTitle("U-19 Team");
                return true;
            }
            case R.id.action_u15_team_list : {
                if (getSupportActionBar().getTitle().equals("U-15 Team")){
                    return false;
                }
                showAllPlayers("under15_Team");
                getSupportActionBar().setTitle("U-15 Team");
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
