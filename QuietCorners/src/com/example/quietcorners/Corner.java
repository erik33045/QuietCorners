package com.example.quietcorners;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class Comment {
    public int Id;
    public int CornerId;
    public String Comment;

    Comment() {
    }

    Comment(JSONObject json) {
        try {
            Id = json.getInt("Id");
            CornerId = json.getInt("CornerId");
            Comment = json.getString("Comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
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
    public ArrayList<Comment> Comments;

    public Corner() {
    }

    public Corner(JSONObject json) throws JSONException {
        Id = json.getInt("Id");
        Lat = json.getDouble("Lat");
        Lng = json.getDouble("Lng");
        QuietRating = json.getInt("QuietRating");
        LightRating = json.getInt("LightRating");
        HasOpenNetwork = (json.getString("HasOpenNetwork").equals("1"));
        OverallRating = json.getInt("OverallRating");
    }

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
            JSONArray array = AccessURLReturnJSON(query);
            return new Comment(array.getJSONObject(0));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Comment> LoadCommentsByCornerId(int cornerId) {
        try {
            String query = CreateGetCommentsByCornerIdQueryString(cornerId);
            JSONArray array = AccessURLReturnJSON(query);
            return Corner.GetCommentsFromJSONArray(array);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ArrayList<Comment>();
        }
    }

    public static Corner LoadCorner(int id) {
        try {
            String query = CreateGetCornerByIdQueryString(id);
            JSONArray array = AccessURLReturnJSON(query);
            return new Corner(array.getJSONObject(0));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Corner> LoadCornersWithinRange(LatLng location, double range) {
        try {
            String query = CreateGetCornersWithinRangeQueryString(location, range);
            JSONArray array = AccessURLReturnJSON(query);
            return Corner.GetCornersFromJSONArray(array);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ArrayList<Corner>();
        }
    }

    public static ArrayList<Corner> LoadAllCorners() {
        try {
            String query = CreateGetAllCornersQueryString();
            JSONArray array = AccessURLReturnJSON(query);
            return Corner.GetCornersFromJSONArray(array);

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

    private static ArrayList<Corner> GetCornersFromJSONArray(JSONArray array) {
        ArrayList<Corner> corners = new ArrayList<Corner>();

        for (int i = 0; i < array.length(); i++) {
            try {
                corners.add(new Corner(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return corners;
    }

    private static ArrayList<Comment> GetCommentsFromJSONArray(JSONArray array) {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        for (int i = 0; i < array.length(); i++) {
            try {
                comments.add(new Comment(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return comments;
    }


    //THERE BE DRAGONS AHEAD! Don't screw with anything below this comment unless you want to spend hours debugging
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

    private static JSONArray AccessURLReturnJSON(final String queryString) throws UnsupportedEncodingException {
        final JSONArray[] array = {new JSONArray()};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        array[0] = PerformHTTPGetAndReturnJSONArray(queryString);
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

        //OK, this is probably the worst line of code in this project. Freeze current thread will opened thread loads the object from the DB. Not elegant, not smart but it works.
        //noinspection StatementWithEmptyBody
        while (array[0].length() == 0)
            ;
        return array[0];
    }

    private static JSONArray PerformHTTPGetAndReturnJSONArray(String queryString) throws IOException, JSONException {
        DefaultHttpClient httpClient;
        httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("http://www.erik33045.webuda.com/default.php" + queryString);
        HttpResponse response;
        response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String entityString = EntityUtils.toString(entity);
        return new JSONArray(entityString);
    }
}

