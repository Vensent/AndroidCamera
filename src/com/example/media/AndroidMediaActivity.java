package com.example.media;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidMediaActivity extends ListActivity {

	private String[] showNames, classNames;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showNames = getResources().getStringArray(R.array.name_array);
		classNames = getResources().getStringArray(R.array.class_array);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, showNames);
		setListAdapter(arrayAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Intent intent = new Intent(this, Class.forName(getPackageName()
					+ "." + classNames[position]));
			intent.putExtra("title", showNames[position]);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}