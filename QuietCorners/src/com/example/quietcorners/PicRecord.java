package com.example.quietcorners;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;


public class PicRecord extends Activity {

    private ImageView image;
    public int picWidth;
    public int picHeight;
    String encodedImageString;
    Button addPictureButton;


    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picrecord);
        dispatchTakePictureIntent(1);
        addPictureButtonEvent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Bitmap bmp = (Bitmap) data.getExtras().get("data");

            image.setImageBitmap(bmp);
            addPictureButton.requestFocus();

            byte[] byteArray = GetByteArrayFromBitmap(bmp);

            Bitmap bmimage = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length);

        }

    }

    public byte[] GetByteArrayFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] outputChunk = baos.toByteArray();
        encodedImageString = Base64.encodeToString(outputChunk, Base64.DEFAULT);
        return Base64.decode(encodedImageString, Base64.DEFAULT);
    }

    private void addPictureButtonEvent(){
            Button button = (Button) findViewById(R.id.addPictureButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
                       public void onClick(View view){
                            Intent in = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            in.putExtra("crop", "true");
                            in.putExtra("outputX", 100);
                            in.putExtra("outputY", 100);
                            in.putExtra("scale", true);
                            in.putExtra("return-data", true);

                            startActivityForResult(in, 1);
                        }

        });
    }
}

