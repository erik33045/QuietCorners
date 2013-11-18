package com.example.quietcorners;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationDetails extends FragmentActivity implements AddCommentDialog.AddCommentDialogListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationdetails);

        GetAddCommentsButtonAndBindClickEvent();
        Corner corner = Corner.LoadCorner(GetCornerIdFromBundle());
        SetScreenValuesFromCorner(corner);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddCommentDialog();
        dialog.show(getFragmentManager(), "AddCommentFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String value) {
        //Goal is to save the added comment then reload them showing the new one
        int cornerId = GetCornerIdFromBundle();
        Comment comment = CreateComment(value, cornerId);
        Corner.SaveComment(comment);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Comment> comments = Corner.LoadCommentsByCornerId(cornerId);
        ListView listView = (ListView) findViewById(R.id.lvComments);
        SetListViewDataSet(comments, listView);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    private void SetScreenValuesFromCorner(Corner corner) {

        RatingBar lightRatingBar = (RatingBar) findViewById(R.id.rtbLightRating);
        lightRatingBar.setRating(corner.LightRating);

        RatingBar quietRatingBar = (RatingBar) findViewById(R.id.rtbQuietRating);
        quietRatingBar.setRating(corner.QuietRating);

        TextView hasOpenNetwork = (TextView) findViewById(R.id.txtHasOpenNetwork);
        hasOpenNetwork.setText(corner.HasOpenNetwork ? "Yes" : "No");

        RatingBar overallRatingBar = (RatingBar) findViewById(R.id.rtbOverallRating);
        overallRatingBar.setRating(corner.OverallRating);

        //Note, this will need to change when we store pictures. Will have to be to a bitmap or URL, depending on what we choose.
        ImageView picture = (ImageView) findViewById(R.id.picture);
        picture.setImageResource(R.drawable.ic_launcher);

        ListView comments = (ListView) findViewById(R.id.lvComments);
        SetListViewDataSet(corner.Comments, comments);
    }

    private void SetListViewDataSet(ArrayList<Comment> commentList, ListView comments) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        comments.setAdapter(arrayAdapter);

        //Go through each Comment and add to the adapter
        for (Comment aCommentList : commentList) {
            arrayAdapter.add(aCommentList.Comment);
        }
        //Notify that the data set has changed.
        arrayAdapter.notifyDataSetChanged();
    }

    private void GetAddCommentsButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.addComment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private Comment CreateComment(String value, int cornerId) {
        Comment comment = new Comment();
        comment.Comment = value;
        comment.CornerId = cornerId;
        return comment;
    }

    private int GetCornerIdFromBundle() {
        Bundle extras = getIntent().getExtras();
        int cornerId = 1;
        if (extras != null)
            cornerId = Integer.parseInt(extras.getString("CornerId"));
        return cornerId;
    }


}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class AddCommentDialog extends DialogFragment {
    public interface AddCommentDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String value);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AddCommentDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddCommentDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        try {
            if (activity == null)
                throw new Exception("Something has gone terribly wrong!");

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final EditText input = new EditText(activity);
            builder.setTitle("Add Comment")
                    .setView(input)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Editable editable = input.getText();
                            assert editable != null;
                            String value = editable.toString();
                            mListener.onDialogPositiveClick(AddCommentDialog.this, value);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    mListener.onDialogNegativeClick(AddCommentDialog.this);
                }
            });
            return builder.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
