package fr.wildcodeschool.cafeconcert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;


public class Profile extends AppCompatActivity {
    private final int requestCode = 20;
    private ImageView profilePic;
    String mCurrentPhotpPath;
    File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageButton editPhoto = findViewById(R.id.image_take_pic);

        profilePic = findViewById(R.id.image_pic_profile);
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(Profile.this, new String[] {Manifest.permission.CAMERA}, requestCode);

                }
                else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        Uri uriSavedImage=Uri.fromFile(new File("Environment.getExternalStorageDirectory() + \"/photo1.jpg\";"));
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode==RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profilePic.setImageBitmap(bitmap);

        }
    }
}


