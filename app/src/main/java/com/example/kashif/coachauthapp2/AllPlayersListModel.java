package com.example.kashif.coachauthapp2;

/**
 * Created by kashif on 23/7/17.
 */

public class AllPlayersListModel {

    private String PlayerName;
    private String PlayerImageUrl;
    private String PlayerUniqueId;

    public AllPlayersListModel(String user_name, String user_image_Url, String user_uniqueId){
        this.PlayerName = user_name;
        this.PlayerImageUrl = user_image_Url;
        this.PlayerUniqueId = user_uniqueId;
    }


    public String getPlayerName(){
        return PlayerName;
    }
    public String getPlayerImageUrl(){
        return PlayerImageUrl;
    }
    public String getPlayerUniqueId(){
        return PlayerUniqueId;
    }
}
