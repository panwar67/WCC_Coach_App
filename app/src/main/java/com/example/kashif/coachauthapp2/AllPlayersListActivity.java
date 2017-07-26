package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllPlayersListActivity extends AppCompatActivity {

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    private RecyclerView all_players_list_recyclerView;

    private ProgressDialog progressDialog;
    private Snackbar noPlayersFoundMessageSnackbar;

    private ArrayList<AllPlayersListModel> allPlayersListModelArrayList;
    private ArrayList<AllPlayersListModel> filtered_allPlayersListModelArrayList;

    private AllPlayersListRecyclerViewAdapter allPlayersListRecyclerViewAdapter;

    private static DatabaseReference playerList_databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_players_list);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Players..");

        getSupportActionBar().setTitle("All Players");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noPlayersFoundMessageSnackbar = Snackbar.make(findViewById(R.id.all_players_list_layout),
                "No Players Found in this Category. Check others from menu option", Snackbar.LENGTH_INDEFINITE);

        all_players_list_recyclerView = (RecyclerView) findViewById(R.id.all_players_list_recycler_view);
        all_players_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));


        getAllPlayerDetailsIntoArrayList("all_Player_Details");

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
        allPlayersListRecyclerViewAdapter = new AllPlayersListRecyclerViewAdapter(allPlayersListModelArrayList, this);
        all_players_list_recyclerView.setAdapter(allPlayersListRecyclerViewAdapter);
        hideProgressDialog();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_action_bar_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        if (searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    String userQuery = newText.toLowerCase();

                    filtered_allPlayersListModelArrayList = new ArrayList<AllPlayersListModel>();
                    int i = 0;
                    int j;

                    for (j = 0; j < allPlayersListModelArrayList.size(); j++){
                        String UserName = allPlayersListModelArrayList.get(j).getPlayerName().toLowerCase();

                        if (UserName.contains(userQuery)){

                            String user_name = allPlayersListModelArrayList.get(j).getPlayerName();
                            String user_image_url = allPlayersListModelArrayList.get(j).getPlayerImageUrl();
                            String user_uniqueId = allPlayersListModelArrayList.get(j).getPlayerUniqueId();
                            filtered_allPlayersListModelArrayList.add(i, new AllPlayersListModel(user_name, user_image_url, user_uniqueId));
                            i++;
                        }
                    }
                    setAdapter(filtered_allPlayersListModelArrayList);
                    allPlayersListRecyclerViewAdapter.notifyDataSetChanged();
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_search : {
                return false;
            }
            case android.R.id.home : {
                finish();
                return true;
            }

            case R.id.action_all_player_list : {
                if (getSupportActionBar().getTitle().equals("All Players")){
                    return false;
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
                getSupportActionBar().setTitle("All Players");
                getAllPlayerDetailsIntoArrayList("all_Player_Details");
                return true;
            }

            case R.id.action_u19_player_list : {
                if (getSupportActionBar().getTitle().equals("U-19 Players")){
                    return false;
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
                getSupportActionBar().setTitle("U-19 Players");
                getAllPlayerDetailsIntoArrayList("all_under19_Players");
                return true;
            }

            case R.id.action_u15_player_list : {
                if (getSupportActionBar().getTitle().equals("U-15 Players")){
                    return false;
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
                getSupportActionBar().setTitle("U-15 Players");
                getAllPlayerDetailsIntoArrayList("all_under15_Players");
                return true;
            }

            default: {
                break;
            }
        }
        searchView.setOnQueryTextListener(queryTextListener);
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
