package com.example.kashif.coachauthapp2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kashif on 23/7/17.
 */

public class AllPlayersListRecyclerViewAdapter extends RecyclerView.Adapter<AllPlayersListRecyclerViewAdapter.AllPlayersListViewHolder> {


    private ArrayList<AllPlayersListModel> allPlayersListModelArrayList = new ArrayList<AllPlayersListModel>();
    private Context context;

    public AllPlayersListRecyclerViewAdapter(ArrayList<AllPlayersListModel> allPlayersListModelArrayList, Context context){
        this.allPlayersListModelArrayList = allPlayersListModelArrayList;
        this.context = context;
    }


    @Override
    public AllPlayersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.all_coach_list_model, parent, false);
        return new AllPlayersListViewHolder(view);
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

        public AllPlayersListViewHolder(View itemView) {
            super(itemView);

            playerProfileImageView = (CircularImageView) itemView.findViewById(R.id.all_coach_image_iv);
            playerNameTextView = (TextView) itemView.findViewById(R.id.all_coach_name_tv);
            viewProfileButton = (Button) itemView.findViewById(R.id.coach_view_profile_btn);
        }

        public void setPlayerName(String name) {
            playerNameTextView.setText(name);
        }

        public void setPlayerProfileImage(String profileImageUrl) {
            Picasso.with(itemView.getContext()).load(profileImageUrl).into(playerProfileImageView);

        }
    }
}
