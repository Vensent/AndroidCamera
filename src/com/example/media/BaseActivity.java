package com.example.media;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	/** 标记 */
	protected String tag = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 固定竖屏
		setTitle(getIntent().getExtras().getString("title")); // 标题设置
	}

}
