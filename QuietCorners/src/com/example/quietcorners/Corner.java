package com.example.quietcorners;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
//import java.util.List;

class Comment {
    //public int Id;
    public int CornerId;
    public String Comment;
}

enum Intent {
    SaveComment,
    SaveCorner,
    LoadCorner,
    LoadCornersWithinRange
}

public class Corner {
    //public int Id;
    public double Lat;
    public double Lng;
    public int QuietRating;
    public int LightRating;
    public boolean HasOpenNetwork;
    public int OverallRating;
    // public List<Comment> Comments;

    public static int saveCornerToDatabase(Corner corner) {
        try {
            String query = TurnCornerIntoQueryStringForSave(corner);
            return AccessURLNoReturnData(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int SaveCommentToDatabase(Comment comment) {
        try {
            String query = TurnCommentIntoQueryStringForSave(comment);
            return AccessURLNoReturnData(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
    public Corner LoadCornerById(int id) {
        return new Corner();
    }


    public List<Corner> LoadAllCornersWithinRange(LatLng location, double range) {
        List<Corner> corners = null;
        return corners;
    }*/

    private static String TurnCornerIntoQueryStringForSave(Corner corner) {
        return "?Intent=" + Intent.SaveCorner.toString()
                + "&Lat=" + corner.Lat
                + "&Lng=" + corner.Lng
                + "&QuietRating=" + corner.QuietRating
                + "&LightRating=" + corner.LightRating
                + "&HasOpenNetwork=" + corner.HasOpenNetwork
                + "&OverallRating=" + corner.OverallRating;
    }

    private static String TurnCommentIntoQueryStringForSave(Comment comment) {
        return "?Intent=" + Intent.SaveComment.toString()
                + "&CornerId=" + comment.CornerId
                + "&Comment=" + comment.Comment.replaceAll(" ", "%20");
    }


    //THAR BE DRAGONS AHEAD! Don't screw with anything below this comment unless you want to spend hours debugging


    private static int AccessURLNoReturnData(final String queryString) throws UnsupportedEncodingException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://www.erik33045.webuda.com/default.php" + queryString);
                    try {
                        httpClient.execute(httpPost);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return 0;
    }
}

