package com.example.quietcorners;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by erik3_000 on 10/24/13.
 */
public class Corner {
    public int Id;
    public double Lat;
    public double Lng;
    public int QuietRating;
    public int LightRating;
    public boolean HasOpenNetwork;
    public int overallRating;
    public List<Comment> Comments;

    public int saveCornerToDatabase(Corner corner) {
        //This return is a return code, most likely will be either errors found by us or HTTP errors
        return 0;
    }

    public Corner LoadCornerById(int id) {
        return new Corner();
    }

    public List<Corner> LoadAllCornersWithinRange(LatLng location, double range) {
        List<Corner> corners = null;
        return corners;
    }

    public int SaveCommentToDatabase(int CornerId, Comment comment) {
        //This return is a return code, most likely will be either errors found by us or HTTP errors
        return 0;
    }
}

class Comment {
    public int CornerId;
    public String Comment;
}

enum Intent {
    SaveComment,
    SaveCorner,
    LoadCorner,
    LoadCornersWithinRange
}