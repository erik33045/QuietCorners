package com.example.quietcorners;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;


public class PicRecord extends Activity {

    private ImageView cornerPicture;
    public Bitmap image;
    public int picWidth;
    public int picHeight;


    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        image = (Bitmap) extras.get("data");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picrecord);
        dispatchTakePictureIntent(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Bitmap bmp = (Bitmap) data.getExtras().get("data");

            image.setImageBitmap(bmp);
            btnadd.requestFocus();

            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            byte[] outputChunk = outputstream.toByteArray();
            encodedImageString = Base64.encodeToString(outputChunk, Base64.DEFAULT);

            byte[] bytarray = Base64.decode(encodedImageString, Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);

        }

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        picWidth = mImageBitmap.getWidth();
        picHeight = mImageBitmap.getHeight();
        cornerPicture.setImageBitmap(mImageBitmap);
        MediaStore.Images.Media.insertImage(getContentResolver(), mImageBitmap, "cornerImage", picDescription);
    }*/


    /*Button.OnClickListener mTakePicSOnClickListener =
            new Button.OnClickListener() {
               @Override
                public void onClick(View v) {
                   dispatchTakePictureIntent(1);
                }
            };*/

}

