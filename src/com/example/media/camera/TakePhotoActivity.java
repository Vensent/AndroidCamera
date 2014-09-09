package com.example.media.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.media.R;

public class TakePhotoActivity extends Activity implements OnClickListener {

	private Button takeBtn1, takeBtn2;
	private LinearLayout linearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);

		takeBtn1 = (Button) findViewById(R.id.takeBtn1);
		takeBtn1.setOnClickListener(this);
		takeBtn2 = (Button) findViewById(R.id.takeBtn2);
		takeBtn2.setOnClickListener(this);

		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
	}

	@Override
	public void onClick(View v) {
		v.setClickable(false);
		switch (v.getId()) {
		case R.id.takeBtn1: {
			// 点击第一个按钮进入系统照相界�?
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 1);
			break;
		}
		case R.id.takeBtn2: {
			// 点击第二个按钮进入自定义照相界面
			Intent intent = new Intent(this, CameraActivity.class);
			startActivityForResult(intent, 2);
			break;
		}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (1 == requestCode) 
		{ 
			// 系统相机返回处理
			if (resultCode == Activity.RESULT_OK) 
			{
				Bitmap cameraBitmap = (Bitmap) intent.getExtras().get("data");
				previewBitmap(cameraBitmap); // 处理图像
			}
			takeBtn1.setClickable(true);
		} 
		else if (2 == requestCode) 
		{ 
			// 这里表示返回的是来自自定义相机的内容
			// 自定义相机的返回码是20
			if (resultCode == 20) 
			{
				Bundle bundle = intent.getExtras();
				// 获取照片uri
				Uri uri = Uri.parse(bundle.getString("uriStr"));
				// 获取拍照时间
				long dateTaken = bundle.getLong("dateTaken");
				try {
					// 从媒体数据库获取该照�? 
					Bitmap cameraBitmap = MediaStore.Images.Media.getBitmap(
							getContentResolver(), uri);
					previewBitmap(cameraBitmap); // 预览图像
					// 从媒体数据库删除该照片（按拍照时间）
//					getContentResolver().delete(CameraActivity.IMAGE_URI,
//							MediaStore.Images.Media.DATE_TAKEN + "="
//									+ String.valueOf(dateTaken), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			takeBtn2.setClickable(true);
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	/** 图像预览 */
	private void previewBitmap(Bitmap bmp) {
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(bmp);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new Gallery.LayoutParams(1200, 1000));
		linearLayout.addView(imageView);
	}

}