package com.sergiomse.encuentralo.camera;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sergiomse.encuentralo.PhotoLocationActivity;
import com.sergiomse.encuentralo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private Camera mCamera;
    private CameraPreview mPreview;
    private String photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        if(mCamera != null) {
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        } else {
            Toast.makeText(this, "No se puede obtener la cámara", Toast.LENGTH_LONG).show();
            finish();
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
                Camera.Parameters cameraParams = c.getParameters();
                List<Camera.Size> cameraSizes = cameraParams.getSupportedPictureSizes();
                Camera.Size bestCameraSize = calculateBestResoultion(cameraSizes);
                Camera.Parameters parameters = c.getParameters();
                parameters.setPictureSize(bestCameraSize.width, bestCameraSize.height);
                c.setParameters(parameters);
            } catch (Exception e) {
                Log.d(TAG, "No se puede abrir la cámara: " + e.getMessage());
            }
        }

        return c; // returns null if camera is unavailable
    }

    private Camera.Size calculateBestResoultion(List<Camera.Size> cameraSizes) {

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

        return minSize;
    }

    public void captureImage(View view) {
        mCamera.takePicture(null, null, mPicture);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            photoFile = pictureFile.getAbsolutePath();
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            Intent intent = new Intent(CameraActivity.this, PhotoLocationActivity.class);
            intent.putExtra("photoFile", photoFile);
            intent.putExtra("state", PhotoLocationActivity.NEW_STATE);
            startActivity(intent);
        }
    };

    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Encuentralo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
}
