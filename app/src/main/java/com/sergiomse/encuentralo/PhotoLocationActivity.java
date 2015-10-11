package com.sergiomse.encuentralo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sergiomse.encuentralo.adapters.TagsAdapter;
import com.sergiomse.encuentralo.adapters.ThingsAdapter;
import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoLocationActivity extends AppCompatActivity {

    private static final String TAG = PhotoLocationActivity.class.getSimpleName();

    public static final int VIEW_STATE = 0;
    public static final int EDIT_STATE = 1;
    public static final int NEW_STATE = 2;

    private int state;
    private long thingId;

    private LinearLayout scrollWrapLayout;
    private ImageView imagePhoto;
    private EditText etTags;
    private RecyclerView tagsRecyclerView;
    private TagsAdapter adapter;
    private EditText etLocation;
    private Button btnDelete;
    private Button btnEdit;
    private Button btnCancel;
    private Button btnSave;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_location);

        scrollWrapLayout    = (LinearLayout) findViewById(R.id.scrollWrapLayout);
        imagePhoto          = (ImageView) findViewById(R.id.imagePhoto);
        etTags              = (EditText) findViewById(R.id.etTags);
        tagsRecyclerView    = (RecyclerView) findViewById(R.id.recyclerView);
        etLocation          = (EditText) findViewById(R.id.etLocation);
        btnDelete           = (Button) findViewById(R.id.btnDelete);
        btnEdit             = (Button) findViewById(R.id.btnEdit);
        btnCancel           = (Button) findViewById(R.id.btnCancel);
        btnSave             = (Button) findViewById(R.id.btnSave);


        Intent intent = getIntent();
        state = intent.getIntExtra("state", 0);
        setState();
        switch (state) {
            case NEW_STATE:
                photoFile = new File( (String) intent.getExtras().get("photoFile"));
                break;
            case VIEW_STATE:
                thingId = intent.getLongExtra("id", -1);

                ThingsDB db = new ThingsDB(this);
                Thing thing = db.getThingById(thingId);
                db.cleanup();

                photoFile = new File(thing.getImagePath());
                etTags.setText(thing.getTags());
                etLocation.setText(thing.getLocation());
                break;
        }



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tagsRecyclerView.setLayoutManager(layoutManager);

        List<String> tags = new ArrayList<>();
        tags.add(photoFile.getAbsolutePath());
        tags.add("hola");
        adapter = new TagsAdapter(this, tags);
        tagsRecyclerView.setAdapter(adapter);

//        setPic();
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        imagePhoto.setImageBitmap(bitmap);
    }

    private void setState() {
        if(state == VIEW_STATE) {
            etTags.setEnabled(false);
            etLocation.setEnabled(false);
        } else if(state == EDIT_STATE) {
            etTags.setEnabled(true);
            etLocation.setEnabled(true);
        }
        displayButtonsByState();
    }

    private void displayButtonsByState() {
        switch (state) {
            case VIEW_STATE:
                btnDelete.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                break;
            case EDIT_STATE:
                btnDelete.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                break;
            case NEW_STATE:
                btnDelete.setVisibility(View.GONE);
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (state) {
            case VIEW_STATE | EDIT_STATE:
                super.onBackPressed();
                break;
            case NEW_STATE:
                photoFile.delete();
                returnToMainActivity();
                break;
        }
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

    public void buttonDeleteClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea borrar este objeto?")
                .setTitle("Borrar")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ThingsDB db = new ThingsDB(PhotoLocationActivity.this);
                        db.deleteThing(PhotoLocationActivity.this.thingId);
                        db.cleanup();

                        dialog.cancel();
                        PhotoLocationActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void buttonEditClick(View view) {
        state = EDIT_STATE;
        setState();
    }

    public void buttonCancelClick(View view) {
        if(state == NEW_STATE) {
            photoFile.delete();
            returnToMainActivity();
        } else {
            finish();
        }
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void buttonSaveClick(View view) {
        if(!checkValidations()) {
            return;
        }

        if(state == NEW_STATE) {
            Thing thing = new Thing();
            thing.setImagePath(photoFile.getAbsolutePath());
            thing.setTags(etTags.getText().toString());
            thing.setLocation(etLocation.getText().toString());
            thing.setModifDate(new Date());

            ThingsDB db = new ThingsDB(this);
            db.insertThing(thing);
            db.cleanup();
        } else {
            Thing thing = new Thing();
            thing.setId(thingId);
            thing.setImagePath(photoFile.getAbsolutePath());
            thing.setTags(etTags.getText().toString());
            thing.setLocation(etLocation.getText().toString());
            thing.setModifDate(new Date());

            ThingsDB db = new ThingsDB(this);
            db.updateThing(thing);
            db.cleanup();
        }

        returnToMainActivity();
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

}
