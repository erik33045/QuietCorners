package com.example.quietcorners;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View;
import java.io.ByteArrayOutputStream;


public class PicRecord extends Activity {

    private ImageView image;
    public Bitmap bmp;
    String encodedImageString;



    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picrecord);
        takePictureButtonEvent();
        confirmPictureButtonEvent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            bmp = (Bitmap) data.getExtras().get("data");
            bmp = BitmapFactory.decodeByteArray(GetByteArrayFromBitmap(bmp), 0,
                    GetByteArrayFromBitmap(bmp).length);
            image = (ImageView) findViewById(R.id.cornerPicture);
            image.setImageBitmap(bmp);

        }

    }

    public byte[] GetByteArrayFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] outputChunk = baos.toByteArray();
        encodedImageString = Base64.encodeToString(outputChunk, Base64.DEFAULT);
        return Base64.decode(encodedImageString, Base64.DEFAULT);
    }

    private void confirmPictureButtonEvent(){
            Button button = (Button) findViewById(R.id.confirmPictureButton);
            button.setOnClickListener(new View.OnClickListener(){
               @Override
                       public void onClick(View view){
                            Variables application = (Variables)getApplication();
                            application.cornerBitmap = bmp;
                            Intent i = new Intent(PicRecord.this, Record.class);
                            startActivity(i);
                        }

                });
            }

    private void takePictureButtonEvent(){
        Button button = (Button) findViewById(R.id.takePictureButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dispatchTakePictureIntent(1);
            }
        });
    }
}

/*Crop Stuff
Intent in = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            in.putExtra("crop", "true");
                            in.putExtra("outputX", 100);
                            in.putExtra("outputY", 100);
                            in.putExtra("scale", true);
                            in.putExtra("return-data", true);

                            startActivityForResult(in, 1);
 */
