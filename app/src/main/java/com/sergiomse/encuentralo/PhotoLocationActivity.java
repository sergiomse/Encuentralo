package com.sergiomse.encuentralo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;

import java.io.File;
import java.util.Date;

public class PhotoLocationActivity extends AppCompatActivity {

    private static final String TAG = PhotoLocationActivity.class.getSimpleName();

    private LinearLayout scrollWrapLayout;
    private ImageView imagePhoto;
    private EditText etTags;
    private EditText etLocation;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_location);

        scrollWrapLayout    = (LinearLayout) findViewById(R.id.scrollWrapLayout);
        imagePhoto          = (ImageView) findViewById(R.id.imagePhoto);
        etTags              = (EditText) findViewById(R.id.etTags);
        etLocation          = (EditText) findViewById(R.id.etLocation);

        Intent intent = getIntent();
        photoFile = (File) intent.getExtras().get("photoFile");
        Log.d(TAG, "PhotoFile = " + photoFile.getAbsolutePath());

//        setPic();
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        imagePhoto.setImageBitmap(bitmap);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = scrollWrapLayout.getWidth();
        int targetH = 500;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        imagePhoto.setImageBitmap(bitmap);
    }

    public void buttonSaveClick(View view) {
        if(!checkValidations()) {
            return;
        }

        Thing thing = new Thing();
        thing.setImagePath(photoFile.getAbsolutePath());
        thing.setTags(etTags.getText().toString());
        thing.setLocation(etLocation.getText().toString());
        thing.setModifDate(new Date());

        ThingsDB db = new ThingsDB(this);
        db.insertThing(thing);
        db.cleanup();

        finish();
    }

    private boolean checkValidations() {
        if(etTags.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
