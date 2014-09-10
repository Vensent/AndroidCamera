package com.example.media.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.media.R;

public class RecorderActivity extends Activity {
	
	public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final String PATH = Environment.getExternalStorageDirectory().
			getPath() + "/AndroidMedia/";
	private RecorderPreview mRecorderPreview;
	
	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
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
//			intent.putExtra("orientation", Orientation);
			setResult(20, intent);
			finish();
		}
	};
	
	public void onClick_takePicture(View v)
	{
		mRecorderPreview.getmCamera().takePicture(null, null, mPicture);
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
//    		File file = File.createTempFile(filename, null, dir);
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
}
