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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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

    private static final int ORIENTATION_VERTICAL = 0;
    private static final int ORIENTATION_HORIZONTAL = 1;

    private RecyclerView recyclerView;
    private ThingsAdapter adapter;

    private MainButtonsView buttonsLayout;
    private FrameLayout.LayoutParams buttonsLayoutParams;

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
        getBackCameraResolutionList();
        calculateBestResoultion();

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
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Log.e(TAG, "Exception creating photo file:" + ex.getMessage());
//                return;
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }

        if(!checkCameraHardware()) {
            Toast.makeText(this, "No hay cámaras disponibles", Toast.LENGTH_LONG).show();
            return;
        }

        Camera camera = getCameraInstance();
        if(camera == null) {
            Toast.makeText(this, "No obtener una cámara", Toast.LENGTH_LONG).show();
            return;
        }
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

    /**
     * Try to get the Facing Back Camera, if not the first camera in the list
     * @return
     */
    public Camera getCameraInstance(){
        Camera c = null;

        int noOfCameras = Camera.getNumberOfCameras();

        int cameraFacingBackId = -1;
        for (int i = 0; i < noOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraFacingBackId = i;
            }
        }

        int cameraId = -1;
        if(cameraFacingBackId == -1 && noOfCameras > 0) {
            cameraId = 0;
        }

        if(cameraId != -1) {
            try {
                c = Camera.open(cameraId);
            } catch (Exception e) {
                Log.d(TAG, "No se puede abrir la cámara: " + e.getMessage());
            }
        }
        return c; // returns null if camera is unavailable
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



    private void getBackCameraResolutionList() {
        getBackCameraResolutionListAPI16();
    }

    @TargetApi(21)
    public void getBackCameraResolutionListAPI21() {
        float maxResolution = -1;
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            String cameraIdList[] =  cameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void getBackCameraResolutionListAPI16() {
        int noOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < noOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera camera = Camera.open(i);

                Camera.Parameters cameraParams = camera.getParameters();
//                for (int j = 0; j < cameraParams.getSupportedPictureSizes().size(); j++) {
//                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(i).width * cameraParams.getSupportedPictureSizes().get(i).height;
//                    if (pixelCountTemp > pixelCount) {
//                        pixelCount = pixelCountTemp;
//                        maxResolution = ((float) pixelCountTemp) / (1024000.0f);
//                    }
//                }

                cameraSizes = cameraParams.getSupportedPictureSizes();
                camera.release();
            }
        }
    }

    private void calculateBestResoultion() {

        long min = -1;
        Camera.Size minSize = null;
        for(Camera.Size size : cameraSizes) {
            if(min == -1) {
                min = size.height * size.width;
                minSize = size;
                continue;
            }

            long res = size.height * size.width;
            if(res < min) {
                min = res;
                minSize = size;
            }
        }

        bestCameraSize = minSize;
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
