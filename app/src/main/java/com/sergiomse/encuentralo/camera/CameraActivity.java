package com.sergiomse.encuentralo.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

    private DisplayMetrics dm;
    private Camera mCamera;
    private CameraPreview mPreview;
    private String photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Get display metrics
        dm = getResources().getDisplayMetrics();

        // Create an instance of Camera
        mCamera = getCameraInstance();

        if(mCamera != null) {
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        } else {
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_LONG).show();
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
                Camera.Size bestCameraSize = calculateBestResolution(cameraSizes);
                Camera.Parameters parameters = c.getParameters();
                parameters.setPictureSize(bestCameraSize.width, bestCameraSize.height);
                c.setParameters(parameters);
            } catch (Exception e) {
                Log.d(TAG, getString(R.string.camera_error2) + e.getMessage());
            }
        }

        return c; // returns null if camera is unavailable
    }

    private Camera.Size calculateBestResolution(List<Camera.Size> cameraSizes) {

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
                Log.d(TAG, getString(R.string.file_error1));
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, getString(R.string.file_error2) + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, getString(R.string.file_error3) + e.getMessage());
            }

            saveThumbnailPhoto();

            Intent intent = new Intent(CameraActivity.this, PhotoLocationActivity.class);
            intent.putExtra("photoFile", photoFile);
            intent.putExtra("state", PhotoLocationActivity.NEW_STATE);
            startActivity(intent);
        }
    };

    private void saveThumbnailPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double ratioPhoto = width / (double) height;
        int newWidth = 0;
        int newHeight = 0;
        //TODO Use constants instead of hardcoded 100 px
        if(width > height) {
            newWidth = (int) (100 * dm.density);
            newHeight = (int) (newWidth / ratioPhoto);
        } else {
            newHeight = (int) (100 * dm.density);
            newWidth = (int) (newHeight * ratioPhoto);
        }
        Bitmap outBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        //TODO Refactor to use global file name without extension or two variables (one for normal picture and another for thumbnail)
        String thumbFile = photoFile.substring(0, photoFile.lastIndexOf("."));
        thumbFile += "_THUMB.jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(thumbFile);
            outBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Encuentralo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, getString(R.string.file_error4));
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
