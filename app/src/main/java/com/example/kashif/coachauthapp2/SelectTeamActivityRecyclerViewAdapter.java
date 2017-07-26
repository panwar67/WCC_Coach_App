package com.example.kashif.coachauthapp2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kashif on 24/7/17.
 */

public class SelectTeamActivityRecyclerViewAdapter extends RecyclerView.Adapter<SelectTeamActivityRecyclerViewAdapter.AllPlayersListViewHolder> {


    private ArrayList<AllPlayersListModel> allPlayersListModelArrayList = new ArrayList<AllPlayersListModel>();
    private Context context;
    ArrayList<AllPlayersListModel> checkedPlayers = new ArrayList<>();

    public SelectTeamActivityRecyclerViewAdapter(ArrayList<AllPlayersListModel> allPlayersListModelArrayList, Context context){
        this.allPlayersListModelArrayList = allPlayersListModelArrayList;
        this.context = context;
    }

    @Override
    public AllPlayersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.all_coach_list_model, parent, false);
        return new SelectTeamActivityRecyclerViewAdapter.AllPlayersListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AllPlayersListViewHolder holder, final int position) {

        holder.setPlayerName(allPlayersListModelArrayList.get(position).getPlayerName());
        holder.setPlayerProfileImage(allPlayersListModelArrayList.get(position).getPlayerImageUrl());
        holder.viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to player profile screen
                Intent intent = new Intent(context, PlayerProfileActivity.class)
                        .putExtra("selected_player_unique_id", allPlayersListModelArrayList.get(position).getPlayerUniqueId());
                context.startActivity(intent);
            }
        });

        holder.playercheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.playercheckBox.isChecked()) {
                    checkedPlayers.add(allPlayersListModelArrayList.get(position));
                    Log.d("kashif", checkedPlayers.toString());

                } else if (!holder.playercheckBox.isChecked()) {
                    checkedPlayers.remove(allPlayersListModelArrayList.get(position));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allPlayersListModelArrayList.size();
    }

    // ViewHolder Class for all players list recyclerview
    public class AllPlayersListViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView playerProfileImageView;
        private TextView playerNameTextView;
        public Button viewProfileButton;
        public CheckBox playercheckBox;

        public AllPlayersListViewHolder(View itemView) {
            super(itemView);

            playerProfileImageView = (CircularImageView) itemView.findViewById(R.id.all_coach_image_iv);
            playerNameTextView = (TextView) itemView.findViewById(R.id.all_coach_name_tv);
            viewProfileButton = (Button) itemView.findViewById(R.id.coach_view_profile_btn);
            playercheckBox = (CheckBox) itemView.findViewById(R.id.coach_check_box);

            playercheckBox.setVisibility(View.VISIBLE);
        }

        public void setPlayerName(String name) {
            playerNameTextView.setText(name);
        }

        public void setPlayerProfileImage(String profileImageUrl) {
            Picasso.with(itemView.getContext()).load(profileImageUrl).into(playerProfileImageView);

        }
    }
}
