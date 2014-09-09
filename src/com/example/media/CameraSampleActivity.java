package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.media.camera.TakePhotoActivity;
import com.example.media.video.RecordVideoActivity;

public class CameraSampleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_sample);
	}

	public void takePhoto(View v) {
		// �����TakePhotoActivity
		Intent intent = new Intent(this, TakePhotoActivity.class);
		startActivity(intent);
	}

	public void recordVideo(View v) {
		// �����RecordVideoActivity
		Intent intent = new Intent(this, RecordVideoActivity.class);
		startActivity(intent);
	}

}
