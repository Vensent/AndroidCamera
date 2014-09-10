package com.example.media.video;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class RecorderPreview extends SurfaceView implements SurfaceHolder.Callback {
	
	/* 分辨率 */
	private static final int WIDTH = 2560;
	private static final int HEIGHT = 1440;
	
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public Camera getmCamera() {
		return mCamera;
	}

	// Preview类构造方法
 	@SuppressWarnings("deprecation")
 	public RecorderPreview(Context context, AttributeSet attrs) {
 		super(context, attrs);
 		// 获得SurfaceHolder对象
 		mHolder = getHolder();
 		// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
 		mHolder.addCallback(this);
 		// 设置SurfaceHolder对象的类型 
 		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
 	}
 	
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
    	mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("TAG", "Error setting camera preview: " + e.getMessage());
            mCamera.release(); 
            mCamera = null; 
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	mCamera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
        	// 获取相机的参数
			Parameters parameters = mCamera.getParameters();
			// 设置相片的格式
			parameters.setPictureFormat(PixelFormat.JPEG);
			// 设置预览尺寸
			parameters.setPreviewSize(WIDTH, HEIGHT);
			// 设置相片参数
			parameters.setPictureSize(WIDTH, HEIGHT);
			// 设置相机参数
			mCamera.setParameters(parameters);
			// 开始拍照
			mCamera.startPreview();

        } catch (Exception e){
            Log.d("TAG", "Error starting camera preview: " + e.getMessage());
        }
    }
}