package com.example.hoverballdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void buttonListener(View v) {
		Intent intent = new Intent(MainActivity.this, FloatingService.class);
		switch (v.getId()) {
			case R.id.open_button:
				startService(intent);
				break;
			case R.id.close_button:
				stopService(intent);
				break;
			case R.id.open_dialog_button:
				startService(new Intent(MainActivity.this, DialogFloatingService.class));
				break;
			case R.id.close_dialog_button:
				stopService(new Intent(MainActivity.this, DialogFloatingService.class));
				break;
			default:
				break;
		}
	}
}
