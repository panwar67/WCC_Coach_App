package com.example.kashif.coachauthapp2;

/**
 * Created by kashif on 20/7/17.
 */

public class AllPostsModel {

    public AllPostsModel() {

    }


    public AllPostsModel(String post_description, String post_image, String post_title, long post_timestamp,String post_date,String post_time) {

        this.Post_Description = post_description;
        this.Post_Image = post_image;

        this.Post_Title = post_title;
        this.Post_timestamp = post_timestamp;
        this.Post_Time = post_time;
        this.Post_date = post_date;
    }

    private String Post_Description;
    private String Post_Image;
    private String Post_Title;
    private long Post_timestamp;
    private String Post_Time;
    private String Post_date;

    public String getPost_Title() {
        return Post_Title;
    }


    public String getPost_Description() {
        return Post_Description;
    }


    public String getPost_Image() {
        return Post_Image;
    }


    public long getPost_timestamp() {
        return Post_timestamp;
    }

    public String getPost_time() {
        return Post_Time;
    }

    public String getPost_date() {
        return Post_date;
    }
}
