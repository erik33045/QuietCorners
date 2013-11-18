package com.example.quietcorners;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.*;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Confirm extends Activity {
    RatingBar overallRatingBar;
    RatingBar quietRatingBar;
    RatingBar lightRatingBar;
    TextView hasOpenNetwork;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        GetCompleteButtonEvent();
        Variables application = (Variables)getApplication();

        //Sound Rating Bar
        quietRatingBar = (RatingBar)findViewById(R.id.rtbQuietRating);
        quietRatingBar.setRating(application.soundRating);
        quietRatingBar.setFocusable(false);
        quietRatingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Light Rating Bar
        lightRatingBar = (RatingBar)findViewById(R.id.rtbLightRating);
        lightRatingBar.setRating(application.lightRating);
        lightRatingBar.setFocusable(false);
        lightRatingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Over Rating Bar
        overallRatingBar = (RatingBar)findViewById(R.id.rtbOverallRating);
        overallRatingBar.setStepSize(1);

        //Wireless
        hasOpenNetwork = (TextView)findViewById(R.id.txtHasOpenNetwork);
        if(application.openNetwork) hasOpenNetwork.setText("YES");
        else hasOpenNetwork.setText("NO");

        image = (ImageView) findViewById(R.id.picture);
        image.setImageBitmap(application.cornerBitmap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showDescriptionDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddCommentDialog();
        dialog.show(getFragmentManager(), "AddCommentFragment");
    }

    @Override
    private void onDescriptionDialogPositiveClick(DialogFragment dialog, String value) {
        //Goal is to save the added comment then reload them showing the new one
        Variables application = (Variables)getApplication();
        int cornerId = application.cornerID;
        Comment comment = CreateDescription(value, cornerId);
        Corner.SaveComment(comment);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Comment> comments = Corner.LoadCommentsByCornerId(cornerId);
    }

    @Override
    private void onDescriptionDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    private Comment CreateDescription(String value, int cornerId) {
        Comment comment = new Comment();
        comment.Comment = value;
        comment.CornerId = cornerId;
        return comment;
    }


    private void GetCompleteButtonEvent() {
        Button button = (Button) findViewById(R.id.completeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Variables application = (Variables)getApplication();
                if(application.descriptionFlag){
                    android.content.Intent i = new android.content.Intent(Confirm.this, Main.class);
                    startActivity(i);
                }

                else{
                    Corner corner = new Corner();

                    application.overallRating = overallRatingBar.getNumStars();

                    corner.HasOpenNetwork = application.openNetwork;
                    corner.LightRating = application.lightRating;
                    corner.QuietRating = application.soundRating;
                    corner.Image = application.cornerBitmap;
                    corner.OverallRating = application.overallRating;
                    corner.Lat = application.latitude;
                    corner.Lng = application.longitude;

                    Corner.SaveCorner(corner);
                    application.descriptionFlag = true;
                    showDescriptionDialog();
                }
             }

        });
    }
}


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class AddDescriptionDialog extends DialogFragment {
    public interface AddDescriptionDialogListener {
        public void onDescriptionDialogPositiveClick(DialogFragment dialog, String value);

        public void onDescriptionDialogNegativeClick(DialogFragment dialog);
    }

    AddDescriptionDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddDescriptionDialogListener) activity;
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
                            mListener.onDescriptionDialogPositiveClick(AddDescriptionDialog.this, value);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    mListener.onDescriptionDialogNegativeClick(AddDescriptionDialog.this);
                }
            });
            return builder.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}