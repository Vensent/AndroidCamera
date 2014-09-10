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
	
	/* �ֱ��� */
	private static final int WIDTH = 2560;
	private static final int HEIGHT = 1440;
	
	/** �����ӿ� */ 
	private OnCameraStatusListener listener;

	private SurfaceHolder holder;
	private Camera camera;
	
	// ����һ��PictureCallback���󣬲�ʵ�����е�onPictureTaken���� 
    private PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// ֹͣ��Ƭ����
			camera.stopPreview();
			camera = null;
			// ���ý����¼�
			if (listener != null)
			{
				listener.onCameraStopped(data);
			}
		}
    };

    // Preview�๹�췽��
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ���SurfaceHolder����
		holder = getHolder();
		// ָ�����ڲ�׽�����¼���SurfaceHolder.Callback����
		holder.addCallback(this);
		// ����SurfaceHolder��������� 
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// ��surface����ʱ���� 
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("Note 1", "====surfaceCreated��=====");
		// ���camera����
		camera = Camera.open();
		try {
			// ����������ʾ���������SurfaceHolder����
			camera.setPreviewDisplay(holder);
			getFocus();
		} catch (IOException e) {
			e.printStackTrace(); 
            // �ͷ��ֻ�����ͷ 
            camera.release(); 
            camera = null; 
		}
	}

	// ��surface�Ĵ�С�����仯ʱ����
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("Note 3", "====surfaceChanged.=====");
		try {
			// ��ȡ����Ĳ���
			Parameters parameters = camera.getParameters();
			// ������Ƭ�ĸ�ʽ
			parameters.setPictureFormat(PixelFormat.JPEG);
			// ����Ԥ���ߴ�
			parameters.setPreviewSize(WIDTH, HEIGHT);
			// ������Ƭ����
			parameters.setPictureSize(WIDTH, HEIGHT);
			// �����������
			camera.setParameters(parameters);
			// ��ʼ����
			camera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��surface����ʱ����
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("Note 2", "====surfaceDestotyed.=====");
		// �ͷ��ֻ�����ͷ
		camera.release();
	}
	
	public void getFocus(){
		Log.d("Note 4", "====GetFocus.=====");
		if (camera != null)
		{
			// �Զ��Խ�
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
	
	// ���ü����¼�
	public void setOnCameraStatusListener(OnCameraStatusListener listener) {
		this.listener = listener;
	}
	
	/** 
	 * ������ռ����ӿ� 
	 */
	public interface OnCameraStatusListener {
		// ������ս����¼�
		void onCameraStopped(byte[] data);
		// �����Զ��Խ��¼�
		void onAutoFocus(boolean success);
	}

}
