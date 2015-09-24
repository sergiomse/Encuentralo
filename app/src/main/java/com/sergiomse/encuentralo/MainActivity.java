package com.sergiomse.encuentralo;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sergiomse.encuentralo.adapters.ThingsAdapter;
import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;
import com.sergiomse.encuentralo.views.MainButtonsView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private RecyclerView recyclerView;
    private ThingsAdapter adapter;

    private MainButtonsView buttonsLayout;
    private FrameLayout.LayoutParams buttonsLayoutParams;

    private DisplayMetrics dm;
    private int scrollY;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dm = getResources().getDisplayMetrics();

        buttonsLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (150 * dm.density));
        buttonsLayout = (MainButtonsView) findViewById(R.id.buttonsLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollY += dy;
                if (scrollY > 0 && scrollY < 102 * dm.density) {
                    buttonsLayoutParams.height = (int) (150 * dm.density - scrollY);
                } else if (scrollY > 102 * dm.density) {
                    buttonsLayoutParams.height = (int) (48 * dm.density);
                } else if (scrollY == 0) {
                    buttonsLayoutParams.height = (int) (150 * dm.density);
                }
                buttonsLayout.setLayoutParams(buttonsLayoutParams);
            }
        });


//        recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//
//            @Override
//            public void onScrollChanged() {
//                int scrollY = recyclerView.getScrollY();
//                Log.d(TAG, "Scroll Y = " + scrollY);
//
//                if(scrollY > 0 && scrollY < 102 * dm.density) {
//                    buttonsLayoutParams.height = (int) (150 * dm.density - scrollY);
//                } else if(scrollY > 102 * dm.density) {
//                    buttonsLayoutParams.height = (int) (48 * dm.density);
//                } else if(scrollY == 0) {
//                    buttonsLayoutParams.height = (int) (150 * dm.density);
//                }
//                buttonsLayout.setLayoutParams(buttonsLayoutParams);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ThingsDB db = new ThingsDB(this);
        List<Thing> things = db.getThingsOrderedByDate();
        db.cleanup();

        adapter = new ThingsAdapter(things, dm);
        recyclerView.setAdapter(adapter);

    }

    public void cameraLayoutClick(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Exception creating photo file:" + ex.getMessage());
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent photoLocationIntent = new Intent(this, PhotoLocationActivity.class);
            photoLocationIntent.putExtra("photoFile", photoFile);
            startActivity(photoLocationIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
