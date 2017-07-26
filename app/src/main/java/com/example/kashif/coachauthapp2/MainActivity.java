package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String username;
    private String usermail;
    private String userimageurl;
    private String uniqueUserId;
    private String selectedLoginChoice;

    public static String logged_in_user_category;

    private CircularImageView nav_user_image_imageView;
    private TextView nav_user_name_textView;
    private TextView nav_user_email_textView;

    private  FloatingActionButton fab_add_new_post;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;

    private RecyclerView allPostsRecyclerView;
    Query query;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // show progress dialog on opening of activity
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading News & Posts");
        showProgressDialog();

        // getting the current user details from login activity
        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        uniqueUserId = intent.getExtras().getString("uniqueregid");
        usermail = intent.getExtras().getString("usermail");
        userimageurl = intent.getExtras().getString("userimageurl");
        selectedLoginChoice = intent.getExtras().getString("SelectedLoginChoice");

        logged_in_user_category = selectedLoginChoice;

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // inserting entries into database if logging for the first time
        insertUserDetailsIntoFirebaseDB(username, usermail, userimageurl, uniqueUserId, selectedLoginChoice);


        allPostsRecyclerView = (RecyclerView) findViewById(R.id.all_posts_recyclerView);
        allPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //add new post only coach can post
        fab_add_new_post = (FloatingActionButton) findViewById(R.id.fab_add_new_post);
        if (selectedLoginChoice.equals("Coach")){
            fab_add_new_post.setVisibility(View.VISIBLE);
        }

        fab_add_new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPostIntent();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (selectedLoginChoice.equals("Player")) {
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }
        else if (selectedLoginChoice.equals("Parent")){
            navigationView.inflateMenu(R.menu.activity_main_parent_drawer);
        }
        else if (selectedLoginChoice.equals("Coach")){
            navigationView.inflateMenu(R.menu.activity_main_coach_drawer);
        }
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        nav_user_name_textView = (TextView) header.findViewById(R.id.nav_user_name_tv);
        nav_user_email_textView = (TextView) header.findViewById(R.id.nav_user_email_tv);
        nav_user_image_imageView = (CircularImageView) header.findViewById(R.id.nav_user_imageView);

        nav_user_image_imageView.setBorderColor(getResources().getColor(R.color.com_facebook_button_background_color));
        nav_user_image_imageView.setBorderWidth(4);
        nav_user_image_imageView.setShadowRadius(1);


        Picasso.with(getApplicationContext()).load(userimageurl).into(nav_user_image_imageView);
        nav_user_name_textView.setText(username);
        nav_user_email_textView.setText(usermail);

        getSupportActionBar().setTitle("News & Posts");


        query = databaseReference.child("Posts").orderByChild("Post_timestamp");


        initialiseFirebaseRecyclerAdapterToLoadData();

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete Alert");
        builder.setMessage("This Post will be Deleted!");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (selectedLoginChoice.equals("Coach")) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.action_add_new_post) {
            addNewPostIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int selectedItemId = item.getItemId();

        switch (selectedItemId){

            case R.id.nav_player_profile : {
                Intent intent = new Intent(MainActivity.this, PlayerProfileActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.nav_coach_profile : {
                Intent intent = new Intent(this, CoachProfileActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.nav_dashboard : {

                break;
            }

            case R.id.nav_see_players : {
                Intent intent = new Intent(this, AllPlayersListActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.nav_view_team_details : {
                Intent intent = new Intent(this, ShowTeamActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.nav_view_coach_details : {
                Intent intent = new Intent(this, AllCoachListActivity.class);
                startActivity(intent);

                break;
            }


            case R.id.nav_add_new_post : {
                addNewPostIntent();

                break;
            }

            case R.id.nav_select_team : {
                Intent intent = new Intent(this, SelectTeamActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.nav_logout : {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                goToLoginScreen();
                finish();
                break;
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void goToLoginScreen() {
        Intent intent = new Intent(this, LoginChoiceSelectionActivity.class);
        startActivity(intent);
    }


    public void insertUserDetailsIntoFirebaseDB(String username, String usermail, String userimageurl, final String uniqueUserId, final String selectedLoginChoice){

        final HashMap<String, Object> userDetails = new HashMap<>();
        userDetails.put("UniqueUserId", uniqueUserId);
        userDetails.put("Name", username);
        userDetails.put("Email", usermail);
        userDetails.put("ProfileImageUrl", userimageurl);


        databaseReference.child("all_"+selectedLoginChoice+"_Details").child(uniqueUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    databaseReference.child("all_"+selectedLoginChoice+"_Details").child(uniqueUserId).setValue(userDetails);
                    Toast.makeText(getApplicationContext(), "Please Update your Profile",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, ""+databaseError,Toast.LENGTH_SHORT).show();

            }
        });
    }

    // method to open add new post/news activity
    public void addNewPostIntent(){
        Intent intent = new Intent(this, AddNewPostActivity.class);
        startActivity(intent);
    }


    // method to load data into recyclerview
    public void initialiseFirebaseRecyclerAdapterToLoadData(){

        final FirebaseRecyclerAdapter<AllPostsModel, AllPostsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<AllPostsModel, AllPostsViewHolder>(AllPostsModel.class,
                        R.layout.all_posts_recyclerview_layout,
                        AllPostsViewHolder.class,
                        query) {


                    @Override
                    protected void populateViewHolder(AllPostsViewHolder viewHolder, final AllPostsModel model, final int position) {

                        viewHolder.setTitle(model.getPost_Title());
                        viewHolder.setDesc(model.getPost_Description());
                        if (model.getPost_Image().equals("NULL")){
                            viewHolder.image_iv.setVisibility(View.GONE);
                        }
                        else {
                            viewHolder.image_iv.setVisibility(View.VISIBLE);
                            viewHolder.setImage(getApplicationContext(), model.getPost_Image());
                        }

                        viewHolder.setDate(model.getPost_date());
                        viewHolder.setTime(model.getPost_time());

                        if (selectedLoginChoice.equals("Coach")) {
                            viewHolder.delete_post_btn.setVisibility(View.VISIBLE);
                        }
                        viewHolder.delete_post_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        databaseReference.child("Posts").child(String.valueOf(model.getPost_timestamp())).removeValue();
                                        notifyDataSetChanged();
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                            }
                        });
                        hideProgressDialog();
                    }
                };

        allPostsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    public static class AllPostsViewHolder extends RecyclerView.ViewHolder {

        View blogview;
        private Button delete_post_btn;
        private ImageView image_iv;

        public AllPostsViewHolder(View itemView) {
            super(itemView);
            blogview = itemView;

            delete_post_btn = (Button) blogview.findViewById(R.id.delete_post_btn);
            image_iv = (ImageView) blogview.findViewById(R.id.blog_image);

        }


        public void setDesc(String desc) {
            TextView desc_tv = (TextView) blogview.findViewById(R.id.blog_desc);
            desc_tv.setText(desc);

        }

        public void setTitle(String title) {
            TextView title_tv = (TextView) blogview.findViewById(R.id.blog_text);
            title_tv.setText(title);
        }

        public void setImage(Context ctx, String image) {
            Picasso.with(ctx)
                    .load(image)
                    .into(image_iv);
        }
        public void setDate(String date) {
            TextView title_tv = (TextView) blogview.findViewById(R.id.blog_date);
            title_tv.setText(date);
        }
        public void setTime(String time) {
            TextView title_tv = (TextView) blogview.findViewById(R.id.blog_time);
            title_tv.setText(time);
        }

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
