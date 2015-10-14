package com.sergiomse.encuentralo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sergiomse.encuentralo.adapters.ThingsAdapter;
import com.sergiomse.encuentralo.camera.CameraActivity;
import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;
import com.sergiomse.encuentralo.views.MainButtonsView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ThingsAdapter.OnThingItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int ORIENTATION_VERTICAL = 0;
    private static final int ORIENTATION_HORIZONTAL = 1;

    private RecyclerView recyclerView;
    private ThingsAdapter adapter;

    private MainButtonsView buttonsLayout;
    private FrameLayout.LayoutParams buttonsLayoutParams;
    private Toolbar toolbar;

    private DisplayMetrics dm;
    private int scrollY;

    private List<Camera.Size> cameraSizes;
    private Camera.Size bestCameraSize;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dm = getResources().getDisplayMetrics();

        buttonsLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (150 * dm.density));
        buttonsLayout = (MainButtonsView) findViewById(R.id.buttonsLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        ThingsDB db = new ThingsDB(this);
        List<Thing> things = db.getThingsOrderedByDate();
        db.cleanup();

        adapter = new ThingsAdapter(things, dm, this);
        recyclerView.setAdapter(adapter);

    }

    public void cameraLayoutClick(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {

        if(!checkCameraHardware()) {
            Toast.makeText(this, "No hay cámaras disponibles", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            rescalePhoto();

            Intent photoLocationIntent = new Intent(this, PhotoLocationActivity.class);
            photoLocationIntent.putExtra("photoFile", photoFile);
            photoLocationIntent.putExtra("state", PhotoLocationActivity.NEW_STATE);
            startActivity(photoLocationIntent);
        }
    }

    private void rescalePhoto() {
        //aspect ratio and orientation of the camera
        double ratioCam = bestCameraSize.height / bestCameraSize.width;

        //aspect ratio and orientation of the photo
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double ratioPhoto = height / width;

        Bitmap out = Bitmap.createScaledBitmap(bitmap, 320, 480, false);
    }

    @Override
    public void onThingItemClick(long id) {
        Log.d(TAG, "Click in ThingsAdapter ViewHolder");

        Intent photoLocationIntent = new Intent(this, PhotoLocationActivity.class);
        photoLocationIntent.putExtra("state", PhotoLocationActivity.VIEW_STATE);
        photoLocationIntent.putExtra("id", id);
        startActivity(photoLocationIntent);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.searchMenuButton:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
