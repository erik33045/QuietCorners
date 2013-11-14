package com.example.quietcorners;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationDetails extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationdetails);

        Corner corner = LoadCorner();
        SetScreenValuesFromCorner(corner);
    }

    private void SetScreenValuesFromCorner(Corner corner) {

        RatingBar lightRatingBar = (RatingBar) findViewById(R.id.rtbLightRating);
        lightRatingBar.setNumStars(corner.LightRating);

        RatingBar quietRatingBar = (RatingBar) findViewById(R.id.rtbQuietRating);
        quietRatingBar.setNumStars(corner.QuietRating);

        TextView hasOpenNetwork = (TextView) findViewById(R.id.txtHasOpenNetwork);
        hasOpenNetwork.setText(corner.HasOpenNetwork ? "Yes" : "No");

        RatingBar overallRatingBar = (RatingBar) findViewById(R.id.rtbOverallRating);
        overallRatingBar.setNumStars(corner.OverallRating);

        //Note, this will need to change when we store pictures. Will have to be to a bitmap or URL, depending on what we choose.
        ImageView picture = (ImageView) findViewById(R.id.picture);
        picture.setImageResource(R.drawable.ic_launcher);

        ListView comments = (ListView) findViewById(R.id.lvComments);
        SetListViewDataSet(corner, comments);
    }

    private void SetListViewDataSet(Corner corner, ListView comments) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        comments.setAdapter(arrayAdapter);

        //Go through each Comment and add to the adapter
        for (int i = 0; i < corner.Comments.size(); i++) {
            arrayAdapter.add(corner.Comments.get(i).Comment);
        }
        //Notify that the data set has changed.
        arrayAdapter.notifyDataSetChanged();
    }

    //Temporary method for right now. This method will eventually load the corner and its comments from the database.
    //TODO: Pull the data from the database
    private Corner LoadCorner() {
        Corner corner = new Corner();
        corner.Id = 12;
        corner.Lat = 1.3333;
        corner.Lng = 1.3333;
        corner.LightRating = 2;
        corner.QuietRating = 4;
        corner.OverallRating = 5;
        corner.HasOpenNetwork = true;
        corner.Comments = new ArrayList<Comment>();

        //Initializing three comments
        Comment commentOne = new Comment();
        commentOne.Id = 1;
        commentOne.CornerId = 12;
        commentOne.Comment = "I'm a crazy ass comment!";
        Comment commentTwo = new Comment();
        commentTwo.Id = 2;
        commentTwo.CornerId = 12;
        commentTwo.Comment = "So Am I!";
        Comment commentThree = new Comment();
        commentThree.Id = 3;
        commentThree.CornerId = 12;
        commentThree.Comment = "Y'all are crazy.";
        corner.Comments.add(commentOne);
        corner.Comments.add(commentTwo);
        corner.Comments.add(commentThree);

        return corner;
    }
}
