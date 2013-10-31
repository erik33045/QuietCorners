package com.example.quietcorners;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class Comment {
    public int Id;
    public int CornerId;
    public String Comment;
}

enum Intent {
    SaveComment,
    SaveCorner,
    LoadComment,
    LoadCommentsByCornerId,
    LoadCorner,
    LoadCornersWithinRange,
    LoadAllCorners
}

public class Corner {
    public int Id;
    public double Lat;
    public double Lng;
    public int QuietRating;
    public int LightRating;
    public boolean HasOpenNetwork;
    public int OverallRating;
    // public List<Comment> Comments;

    public static int SaveComment(Comment comment) {
        try {
            String query = TurnCommentIntoQueryStringForSave(comment);
            return AccessURLNoReturnData(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int SaveCorner(Corner corner) {
        try {
            String query = TurnCornerIntoQueryStringForSave(corner);
            return AccessURLNoReturnData(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Comment LoadComment(int id) {
        try {
            String query = CreateGetCommentByIdQueryString(id);
            JSONObject json = AccessURLReturnJSON(query);
            return new Comment();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Comment();
        }
    }

    public static ArrayList<Comment> LoadCommentsByCornerId(int cornerId) {
        try {
            String query = CreateGetCommentsByCornerIdQueryString(cornerId);
            JSONObject json = AccessURLReturnJSON(query);
            return new ArrayList<Comment>();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ArrayList<Comment>();
        }
    }

    public static Corner LoadCorner(int id) {
        try {
            String query = CreateGetCornerByIdQueryString(id);
            JSONObject json = AccessURLReturnJSON(query);
            return new Corner();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Corner();
        }
    }

    public static ArrayList<Corner> LoadCornersWithinRange(LatLng location, double range) {
        try {
            String query = CreateGetCornersWithinRangeQueryString(location, range);
            JSONObject json = AccessURLReturnJSON(query);
            return new ArrayList<Corner>();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ArrayList<Corner>();
        }
    }

    public static ArrayList<Corner> LoadAllCorners() {
        try {
            String query = CreateGetAllCornersQueryString();
            JSONObject json = AccessURLReturnJSON(query);
            return new ArrayList<Corner>();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ArrayList<Corner>();
        }
    }

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

    private static String CreateGetCommentByIdQueryString(int id) {
        return "?Intent=" + Intent.LoadComment.toString() + "&Id=" + id;
    }

    private static String CreateGetCommentsByCornerIdQueryString(int cornerId) {
        return "?Intent=" + Intent.LoadCommentsByCornerId.toString() + "&CornerId=" + cornerId;
    }

    private static String CreateGetCornerByIdQueryString(int id) {
        return "?Intent=" + Intent.LoadCorner.toString() + "&Id=" + id;
    }

    private static String CreateGetCornersWithinRangeQueryString(LatLng location, double range) {
        return "?Intent=" + Intent.LoadCornersWithinRange.toString() + "&Lat=" + location.latitude + "&Lng=" + location.longitude + "&Range=" + range;
    }

    private static String CreateGetAllCornersQueryString() {
        return "?Intent=" + Intent.LoadAllCorners.toString();
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

    private static JSONObject AccessURLReturnJSON(final String queryString) throws UnsupportedEncodingException {
        final JSONObject[] json = new JSONObject[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        InputStream inputStream = PerformHTTPGetAndReturnInputStream(queryString);
                        json[0] = new JSONObject(ParseReturnString(inputStream));

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
        return json[0];
    }

    private static InputStream PerformHTTPGetAndReturnInputStream(String queryString) throws IOException {
        DefaultHttpClient httpClient;
        httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("http://www.erik33045.webuda.com/default.php" + queryString);
        HttpResponse response;
        response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    private static String ParseReturnString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}

