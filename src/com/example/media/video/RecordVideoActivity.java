package com.example.media.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.example.media.R;

public class RecordVideoActivity extends Activity implements OnClickListener {
	
	private static final int ACTION_TAKE_VIDEO = 1;
	private Button sysBtn;
	private LinearLayout linearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_preview);
		
		sysBtn = (Button) findViewById(R.id.btn_system);
		sysBtn.setOnClickListener(this);
		
		linearLayout = (LinearLayout) findViewById(R.id.video_linearLayout);
	}

	@Override
	public void onClick(View v) {
		v.setClickable(false);
		switch (v.getId()) {
			case R.id.btn_system:
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				startActivityForResult(intent, ACTION_TAKE_VIDEO);
				break;
	
			default:
				break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// 系统相机返回处理
		if (requestCode == ACTION_TAKE_VIDEO && resultCode == RESULT_OK) {
			previewBitmap(intent);
		}
		sysBtn.setClickable(true);
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	/** 摄像预览 */
	@SuppressWarnings("deprecation")
	private void previewBitmap(Intent intent) {
		
		Uri mVideoUri = intent.getData();
		Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(getRealPathFromURI(this, mVideoUri),
		        MediaStore.Images.Thumbnails.MINI_KIND);
		VideoView mVideoView = new VideoView(this);
		mVideoView.setBackgroundDrawable(new BitmapDrawable(thumbnail));
        mVideoView.setVideoURI(mVideoUri);
        mVideoView.setLayoutParams(new Gallery.LayoutParams(1200, 900));
        linearLayout.addView(mVideoView);
	}
	
	public String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
	}

}