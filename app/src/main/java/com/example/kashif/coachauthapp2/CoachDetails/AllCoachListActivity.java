package com.example.kashif.coachauthapp2.CoachDetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kashif.coachauthapp2.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class AllCoachListActivity extends AppCompatActivity {

    private RecyclerView coach_list_recycler_view;
    private Query query;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_coach_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Coach List..");
        showProgressDialog();

        getSupportActionBar().setTitle("All Coach List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coach_list_recycler_view = (RecyclerView) findViewById(R.id.coach_list_recycler_view);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("all_Coach_Details");
        query = databaseReference.orderByChild("Name");

        coach_list_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        showAllCoach();
    }

    public void showAllCoach() {

        FirebaseRecyclerAdapter<AllCoachListModels, AllCoachListViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<AllCoachListModels, AllCoachListViewHolder>(AllCoachListModels.class,
                        R.layout.all_coach_list_model,
                        AllCoachListViewHolder.class,
                        query) {


                    @Override
                    protected void populateViewHolder(AllCoachListViewHolder viewHolder, final AllCoachListModels model, int position) {

                        viewHolder.setCoach_name(model.getName());
                        viewHolder.setCoach_image(getApplicationContext(), model.getProfileImageUrl());

                        viewHolder.see_coach_profile_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent show_coach_profile_intent = new Intent(AllCoachListActivity.this, CoachProfileActivity.class);
                                show_coach_profile_intent.putExtra("selected_coach_unique_id",model.getUniqueUserId());
                                startActivity(show_coach_profile_intent);

                            }
                        });
                        hideProgressDialog();
                    }
                };

            coach_list_recycler_view.setAdapter(firebaseRecyclerAdapter);
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
