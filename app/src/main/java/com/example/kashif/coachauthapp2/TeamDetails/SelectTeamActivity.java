package com.example.kashif.coachauthapp2.TeamDetails;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kashif.coachauthapp2.PlayerDetails.AllPlayersListModel;
import com.example.kashif.coachauthapp2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SelectTeamActivity extends AppCompatActivity {


    private RecyclerView all_players_list_recyclerView;

    private Snackbar noPlayersFoundMessageSnackbar;
    private ProgressDialog progressDialog;

    private Map<String, Object> posts_detail_hash_map;

    private ArrayList<AllPlayersListModel> allPlayersListModelArrayList;
    private ArrayList<AllPlayersListModel> filtered_allPlayersListModelArrayList;

    private SelectTeamActivityRecyclerViewAdapter allPlayersListRecyclerViewAdapter;

    private static DatabaseReference playerList_databaseReference;
    private DatabaseReference finalTeamdatabaserefernce;
    private DatabaseReference addPostDatabaseReference;
    private Button submit_team_btn;
    private Map<String, Object> allSelectedPlayersHashMap;
    String uniquePlayerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Players..");

        getSupportActionBar().setTitle("U-19 Team");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noPlayersFoundMessageSnackbar = Snackbar.make(findViewById(R.id.activity_select_player),
                "No Players Found in this Category. Check others from menu option", Snackbar.LENGTH_INDEFINITE);

        submit_team_btn = (Button) findViewById(R.id.submit_team_btn);
        all_players_list_recyclerView = (RecyclerView) findViewById(R.id.all_players_list_recycler_view);
        all_players_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        finalTeamdatabaserefernce = FirebaseDatabase.getInstance().getReference().child("under19_Team");
        addPostDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");


        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String[] metadata = formattedDate.split(" ");
        String date = metadata[0];
        String time = metadata[1];
        long timestamp = System.currentTimeMillis();

        long newTime = -1 * timestamp;
        final String string_timestamp = String.valueOf(newTime);


        posts_detail_hash_map = new HashMap<>();
        posts_detail_hash_map.put("Post_Image", "NULL");
        posts_detail_hash_map.put("Post_Time", time);
        posts_detail_hash_map.put("Post_date", date);
        posts_detail_hash_map.put("Post_timestamp", newTime);



        getAllPlayerDetailsIntoArrayList("all_under19_Players");

        submit_team_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String selectedTeamCategory = String.valueOf(getSupportActionBar().getTitle());

                        if (allPlayersListRecyclerViewAdapter.checkedPlayers.size() > 0) {

                            finalTeamdatabaserefernce.removeValue();
                            for (AllPlayersListModel p : allPlayersListRecyclerViewAdapter.checkedPlayers) {

                                uniquePlayerId = p.getPlayerUniqueId();
                                allSelectedPlayersHashMap = new HashMap<>();
                                allSelectedPlayersHashMap.put("Name", p.getPlayerName());
                                allSelectedPlayersHashMap.put("ProfileImageUrl", p.getPlayerImageUrl());
                                allSelectedPlayersHashMap.put("UniqueUserId", uniquePlayerId);

                                finalTeamdatabaserefernce.child(uniquePlayerId).updateChildren(allSelectedPlayersHashMap);

                                Toast.makeText(SelectTeamActivity.this, "Team Formed", Toast.LENGTH_SHORT).show();
                            }

                            posts_detail_hash_map.put("Post_Title", "New "+ selectedTeamCategory + " Formed");
                            posts_detail_hash_map.put("Post_Description", "Coach has updated the "+ selectedTeamCategory + ". Kindly go to Show Teams section to check the players list.");
                            addPostDatabaseReference.child(string_timestamp).setValue(posts_detail_hash_map);
                            finish();

                        }

                        else {
                            Toast.makeText(SelectTeamActivity.this, "Please Select Players First", Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }

    public void getAllPlayerDetailsIntoArrayList(String playerCategory){

        showProgressDialog();
        allPlayersListModelArrayList = new ArrayList<AllPlayersListModel>();

        playerList_databaseReference = FirebaseDatabase.getInstance().getReference().child(playerCategory);
        playerList_databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0){
                    noPlayersFoundMessageSnackbar.show();
                    hideProgressDialog();
                }
                else {
                    noPlayersFoundMessageSnackbar.dismiss();
                }

                int i = 0;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    String user_name = childSnapshot.child("Name").getValue().toString();
                    String user_image_Url = childSnapshot.child("ProfileImageUrl").getValue(String.class);
                    String user_uniqueId = childSnapshot.child("UniqueUserId").getValue().toString();
                    allPlayersListModelArrayList.add(i, new AllPlayersListModel(user_name, user_image_Url, user_uniqueId));
                    i++;
                }
                setAdapter(allPlayersListModelArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void setAdapter(ArrayList<AllPlayersListModel> allPlayersListModelArrayList){
        allPlayersListRecyclerViewAdapter = new SelectTeamActivityRecyclerViewAdapter(allPlayersListModelArrayList, this);
        all_players_list_recyclerView.setAdapter(allPlayersListRecyclerViewAdapter);
        hideProgressDialog();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_team_menu, menu);

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

            case R.id.action_u19_player_list : {
                if (getSupportActionBar().getTitle().equals("U-19 Team")){
                    return false;
                }
                getSupportActionBar().setTitle("U-19 Team");
                getAllPlayerDetailsIntoArrayList("all_under19_Players");
                finalTeamdatabaserefernce = FirebaseDatabase.getInstance().getReference().child("under19_Team");
                return true;
            }

            case R.id.action_u15_player_list : {
                if (getSupportActionBar().getTitle().equals("U-15 Team")){
                    return false;
                }
                getSupportActionBar().setTitle("U-15 Team");
                getAllPlayerDetailsIntoArrayList("all_under15_Players");
                finalTeamdatabaserefernce = FirebaseDatabase.getInstance().getReference().child("under15_Team");
                return true;
            }

            default: {
                break;
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
