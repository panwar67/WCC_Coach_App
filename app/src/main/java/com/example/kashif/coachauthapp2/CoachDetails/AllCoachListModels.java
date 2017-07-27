package com.example.kashif.coachauthapp2.CoachDetails;

/**
 * Created by kashif on 23/7/17.
 */

public class AllCoachListModels {

    public AllCoachListModels() {

    }


    public AllCoachListModels(String UniqueUserId, String Name, String ProfileImageUrl, String user_category) {
        this.UniqueUserId = UniqueUserId;
        this.Name = Name;

        this.ProfileImageUrl = ProfileImageUrl;
        this.user_category = user_category;
    }


    private String UniqueUserId;
    private String ProfileImageUrl;
    private String Name;
    private String user_category;

    public String getUniqueUserId() {
        return UniqueUserId;
    }

    public String getProfileImageUrl() {
        return ProfileImageUrl;
    }

    public String getName() {
        return Name;
    }
    public String getUser_category(){
        return user_category;
    }

}
