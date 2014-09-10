package com.example.media.camera;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements Callback {
	
	/* 分辨率 */
	private static final int WIDTH = 2560;
	private static final int HEIGHT = 1440;
	
	/** 监听接口 */ 
	private OnCameraStatusListener listener;

	private SurfaceHolder holder;
	private Camera camera;
	
	// 创建一个PictureCallback对象，并实现其中的onPictureTaken方法 
    private PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 停止照片拍摄
			camera.stopPreview();
			camera = null;
			// 调用结束事件
			if (listener != null)
			{
				listener.onCameraStopped(data);
			}
		}
    };

    // Preview类构造方法
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获得SurfaceHolder对象
		holder = getHolder();
		// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
		holder.addCallback(this);
		// 设置SurfaceHolder对象的类型 
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// 在surface创建时激发 
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("Note 1", "====surfaceCreated。=====");
		// 获得camera对象
		camera = Camera.open();
		try {
			// 设置用于显示拍照摄像的SurfaceHolder对象
			camera.setPreviewDisplay(holder);
			getFocus();
		} catch (IOException e) {
			e.printStackTrace(); 
            // 释放手机摄像头 
            camera.release(); 
            camera = null; 
		}
	}

	// 在surface的大小发生变化时调用
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("Note 3", "====surfaceChanged.=====");
		try {
			// 获取相机的参数
			Parameters parameters = camera.getParameters();
			// 设置相片的格式
			parameters.setPictureFormat(PixelFormat.JPEG);
			// 设置预览尺寸
			parameters.setPreviewSize(WIDTH, HEIGHT);
			// 设置相片参数
			parameters.setPictureSize(WIDTH, HEIGHT);
			// 设置相机参数
			camera.setParameters(parameters);
			// 开始拍照
			camera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 在surface销毁时激发
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("Note 2", "====surfaceDestotyed.=====");
		// 释放手机摄像头
		camera.release();
	}
	
	public void getFocus(){
		Log.d("Note 4", "====GetFocus.=====");
		if (camera != null)
		{
			// 自动对焦
			camera.autoFocus(new AutoFocusCallback() {
				@Override 
				public void onAutoFocus(boolean success, Camera camera) {
					if (listener != null && success)
					{
						listener.onAutoFocus(success);
					}
					else if (!success) { 
						getFocus();
                    } 
				}
			});
		}
	}
	
	protected Camera getCamera() {
		return camera;
	}

	public void takePicture(){
		Log.d("Note 5", "====takePicture.=====");
		getFocus();
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				camera.takePicture(null, null, pictureCallback);
			}
		}, 800);
	}
	
	// 设置监听事件
	public void setOnCameraStatusListener(OnCameraStatusListener listener) {
		this.listener = listener;
	}
	
	/** 
	 * 相机拍照监听接口 
	 */
	public interface OnCameraStatusListener {
		// 相机拍照结束事件
		void onCameraStopped(byte[] data);
		// 拍摄自动对焦事件
		void onAutoFocus(boolean success);
	}

}
