package com.example.quietcorners;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
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
    SaveCornerImage,
    LoadComment,
    LoadCommentsByCornerId,
    LoadCorner,
    LoadCornerImage,
    LoadCornersWithinRange,
    LoadAllCorners,
    LoadCornerIdByPosition
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
    public Bitmap Image;

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
            String saveCornerQuery = TurnCornerIntoQueryStringForSave(corner);
            if (AccessURLNoReturnData(saveCornerQuery) == 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (corner.Image != null && new PicRecord().GetByteArrayFromBitmap(corner.Image).length > 0) {
                    int cornerId = GetCornerIdByPosition(new LatLng(corner.Lat, corner.Lng));
                    return SaveCornerImage(corner.Image, cornerId);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
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

            //Load the corner
            Corner corner = new Corner(array.getJSONObject(0));

            //Load the comments if it has one
            corner.Comments = Corner.LoadCommentsByCornerId(corner.Id);

            //Load Corner Picture if it has one
            corner.Image = Corner.GetCornerImageById(corner.Id);

            return corner;
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

    public static int GetCornerIdByPosition(LatLng position) {
        try {
            String query = CreateGetCornerIdByPositionQueryString(position);
            JSONArray array = AccessURLReturnJSON(query);

            Variables application = (Variables)getApplication();
            application.cornerID = Corner.GetCornerIdFromJSONArray(array);

            return Corner.GetCornerIdFromJSONArray(array);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Bitmap GetCornerImageById(int cornerId) {
        String queryString = GetCornerImageQueryString(cornerId);
        try {
            return Corner.ReturnImageFromHTTPGet(Corner.PerformHTTPGetAndReturnEntity(queryString));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String GetCornerImageQueryString(int cornerId) {
        return "?Intent=" + Intent.LoadCornerImage + "&CornerId=" + cornerId;
    }

    private static int SaveCornerImage(Bitmap image, int cornerId) {
        return new Corner().SendImageThroughPOST(new PicRecord().GetByteArrayFromBitmap(image), cornerId);
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

    private static String CreateSaveCornerImageQueryString(int cornerId) {
        return "?Intent=" + Intent.SaveCornerImage.toString() + "&CornerId=" + cornerId;
    }

    private static String CreateGetCornerIdByPositionQueryString(LatLng position) {
        return "?Intent=" + Intent.LoadCornerIdByPosition + "&Lat=" + position.latitude + "&Lng=" + position.longitude;
    }

    private static int GetCornerIdFromJSONArray(JSONArray array) {
        try {
            return array.getJSONObject(0).getInt("CornerId");
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static ArrayList<Corner> GetCornersFromJSONArray(JSONArray array) {
        ArrayList<Corner> corners = new ArrayList<Corner>();

        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    corners.add(new Corner(array.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return corners;
    }

    private static ArrayList<Comment> GetCommentsFromJSONArray(JSONArray array) {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    comments.add(new Comment(array.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        final boolean[] canProceed = {false};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        JSONArray returnArray = PerformHTTPGetAndReturnJSONArray(queryString);
                        if (returnArray.length() < 1) {
                            array[0] = new JSONArray("[]");
                            canProceed[0] = true;
                        } else {
                            array[0] = returnArray;
                            canProceed[0] = true;
                        }
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
        while (!canProceed[0])
            ;
        return array[0];
    }

    private static JSONArray PerformHTTPGetAndReturnJSONArray(String queryString) throws IOException, JSONException {
        HttpEntity entity = PerformHTTPGetAndReturnEntity(queryString);
        return getJsonArrayFromHTTPEntity(entity);
    }

    private static JSONArray getJsonArrayFromHTTPEntity(HttpEntity entity) throws IOException, JSONException {
        String entityString = EntityUtils.toString(entity);
        return new JSONArray(entityString);
    }

    private static HttpEntity PerformHTTPGetAndReturnEntity(String queryString) throws IOException {
        DefaultHttpClient httpClient;
        httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("http://www.erik33045.webuda.com/default.php" + queryString);
        HttpResponse response;
        response = httpClient.execute(request);
        return response.getEntity();
    }

    private static Bitmap ReturnImageFromHTTPGet(HttpEntity entity) {
        try {
            return new PicRecord().GetBitMapFromByteArray(EntityUtils.toByteArray(entity));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int SendImageThroughPOST(final byte[] byteArray, final int cornerId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String queryString = CreateSaveCornerImageQueryString(cornerId);
                    HttpPost post = new HttpPost("http://www.erik33045.webuda.com/default.php" + queryString);

                    post.setEntity(new ByteArrayEntity(byteArray));
                    try {
                        httpClient.execute(post);
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

