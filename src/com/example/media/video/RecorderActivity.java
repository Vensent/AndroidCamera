package com.example.media.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.media.R;

public class RecorderActivity extends Activity {
	
	public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final String PATH = Environment.getExternalStorageDirectory().
			getPath() + "/AndroidMedia/";
	public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	
	private RecorderPreview mRecorderPreview;
	private Camera mCamera;
	private MediaRecorder mMediaRecorder;
	private boolean isRecording = false;
	private ImageButton mImageButton;
	
	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			File pictureFile = null;
		    try {
		    	pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			} 
		    catch (Exception e) {
				if (pictureFile == null){
		            Log.d("TAG", "Error creating media file, check storage permissions: " +
		                e.getMessage());
		            return;
		        }
			}
	
	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d("TAG", "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d("TAG", "Error accessing file: " + e.getMessage());
	        }
			}
	};
	
//	public void onClick_takePicture(View v)
//	{
//		mRecorderPreview.getmCamera().takePicture(null, null, mPicture);
//		mRecorderPreview.getmCamera().release();
//	}
	
	public void onClick_RecordVideo(View v)
	{
		if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
//            setCaptureButtonText("Capture");
            isRecording = false;
            finish();
        } else {
        	mImageButton.setImageResource(R.drawable.btn_video_button_pressed);
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
            	mImageButton.postDelayed(new Runnable() {
    				@Override
    				public void run() {
    					mImageButton.setImageResource(R.drawable.btn_video_button_stop_default);
    				}
    			}, 2000);
                mMediaRecorder.start();

                // inform the user that recording has started
//                setCaptureButtonText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置布局视图
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recorder);
		mRecorderPreview = (RecorderPreview)findViewById(R.id.recorder_preview);
		mCamera = mRecorderPreview.getmCamera();
		mImageButton = (ImageButton) findViewById(R.id.btn_recordVideo);
	}
	
//	/**
//     * 存储图像并将信息添加入媒体数据库
//     */
//    private Uri insertImage(ContentResolver cr, String name, long dateTaken,
//    		String directory, String filename, Bitmap source, byte[] jpegData){
//    	
//    	FileOutputStream fos = null;
//    	String filePath = directory + filename;
//    	try {
//    		File dir = new File(directory);
//    		if (!dir.exists())
//    		{
//    			dir.mkdir();
//    		}
//    		File file = new File(dir, filename);
////    		File file = File.createTempFile(filename, null, dir);
//    		if (file != null) 
//    		{
//    			fos = new FileOutputStream(file);
//    			if (source != null && fos != null)
//    			{
//    				source.compress(CompressFormat.JPEG, 90, fos);
//    				fos.flush();
//    			}
//    			else
//    			{
//					fos.write(jpegData);
//				}
//    		}
//		}  catch (FileNotFoundException e) { 
//            e.printStackTrace(); 
//            return null; 
//        } catch (IOException e) { 
//            e.printStackTrace(); 
//            return null; 
//        } finally { 
//            if (fos != null) { 
//                try { 
//                    fos.close(); 
//                } catch (Throwable t) { 
//                } 
//            } 
//        } 
//    	
//    	ContentValues values = new ContentValues(7);
//    	values.put(MediaStore.Images.Media.TITLE, name);
//    	values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
//    	values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
//    	values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//    	values.put(MediaStore.Images.Media.DATA, filePath);
//    	return cr.insert(IMAGE_URI, values); 
//    }
    
    /** A safe way to get an instance of the Camera object. */
//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open(); // attempt to get a Camera instance
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//        }
//        return c; // returns null if camera is unavailable
//    }
    
    private boolean prepareVideoRecorder(){

//        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera = mRecorderPreview.getmCamera();
        mCamera.unlock();
//        mCamera.stopPreview();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mRecorderPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("TAG", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("TAG", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
	}
}
