package com.example.media.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.media.R;
import com.example.media.camera.CameraPreview.OnCameraStatusListener;


public class CameraActivity extends Activity implements OnCameraStatusListener{

	public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final String PATH = Environment.getExternalStorageDirectory().
			getPath() + "/AndroidMedia/";
	
	private CameraPreview mCameraPreview;
	private ImageView focusView;
	private boolean isTaking = false; //拍照中
	private ImageButton btnTakePicture;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置布局视图
        setContentView(R.layout.camera);
        // 照片预览界面
        mCameraPreview = (CameraPreview)findViewById(R.id.preview);
        mCameraPreview.setOnCameraStatusListener(this);
        // 焦点图片
        focusView = (ImageView) findViewById(R.id.focusView);
        btnTakePicture = (ImageButton) findViewById(R.id.btn_takePicture);
    }

    /**
     * 触屏事件
     */
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN && !isTaking){
    		mCameraPreview.getFocus();
    	}
		return super.onTouchEvent(event);
	}

    /**
     * 存储图像并将信息添加入媒体数据库
     */
    private Uri insertImage(ContentResolver cr, String name, long dateTaken,
    		String directory, String filename, Bitmap source, byte[] jpegData){
    	
    	FileOutputStream fos = null;
    	String filePath = directory + filename;
    	try {
    		File dir = new File(directory);
    		if (!dir.exists())
    		{
    			dir.mkdir();
    		}
    		File file = new File(dir, filename);
    		if (file != null) 
    		{
    			fos = new FileOutputStream(file);
    			if (source != null && fos != null)
    			{
    				source.compress(CompressFormat.JPEG, 90, fos);
    				fos.flush();
    			}
    			else
    			{
					fos.write(jpegData);
				}
    		}
    			
			
		}  catch (FileNotFoundException e) { 
            e.printStackTrace(); 
            return null; 
        } catch (IOException e) { 
            e.printStackTrace(); 
            return null; 
        } finally { 
            if (fos != null) { 
                try { 
                    fos.close(); 
                } catch (Throwable t) { 
                } 
            } 
        } 
    	
    	ContentValues values = new ContentValues(7);
    	values.put(MediaStore.Images.Media.TITLE, name);
    	values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
    	values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
    	values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
    	values.put(MediaStore.Images.Media.DATA, filePath);
    	return cr.insert(IMAGE_URI, values); 
    }
    
	/**
	 * 相机拍照结束事件
	 */
	@Override
	public void onCameraStopped(byte[] data) {

		Log.d("onCameraStopped", "====onCameraStopped====");
		// 创建图像
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		// 系统时间
		long dateTaken = System.currentTimeMillis();
		// 图像名称
		String filename = DateFormat.format("yyyy-MM-dd_kkmmss", dateTaken)
				.toString() + ".jpeg";
		// 存储图像（PATH目录）
		Uri uri = insertImage(getContentResolver(), filename, dateTaken, PATH, 
				filename, bitmap, data);
		// 返回结果
		Intent intent = getIntent();
		intent.putExtra("uriStr", uri.toString());
		intent.putExtra("dateTaken", dateTaken);
		intent.putExtra("filePath", PATH + filename);
//		intent.putExtra("orientation", Orientation);
		setResult(20, intent);
		finish();
	}

	/** 
     * 拍摄时自动对焦事件 
     */ 
	@Override
	public void onAutoFocus(boolean success) {
		// 改变对焦状态图像
		if (success)
		{
			focusView.setImageResource(R.drawable.focus2);
		}
	}
	
	public void onClick_takePicture(View view){
		
		btnTakePicture.setImageResource(R.drawable.btn_shutter_new_pressed);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				btnTakePicture.setImageResource(R.drawable.btn_shutter_new_default);
			}
		}, 500);
		
		isTaking = true;
		mCameraPreview.takePicture();
	}
	
}
