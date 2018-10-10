package fr.wildcodeschool.cafeconcert;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Profile extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private final int requestCode = 20;
    String mCurrentPhotoPath;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageButton editPhoto = findViewById(R.id.image_take_pic);

        profilePic = findViewById(R.id.image_pic_profile);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentPhotoPath = sharedPreferences.getString("mPhotoPath", null);

        if (mCurrentPhotoPath != null) {
            File imgFile = new File(mCurrentPhotoPath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            profilePic.setImageBitmap(myBitmap);
        }

        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                }
                if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.CAMERA}, requestCode);

                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        dispatchTakePictureIntent();
                    }
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentPhotoPath = sharedPreferences.getString("mPhotoPath", null);
        if (mCurrentPhotoPath != null) {
            File imgFile = new File(mCurrentPhotoPath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            profilePic.setImageBitmap(myBitmap);
        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "File not Created", Toast.LENGTH_LONG);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mPhotoPath", mCurrentPhotoPath);
        editor.commit();

        return image;
    }
}


