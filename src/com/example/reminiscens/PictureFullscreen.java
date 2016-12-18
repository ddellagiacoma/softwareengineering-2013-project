package com.example.reminiscens;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class PictureFullscreen extends Activity {

	public static final String TAG = "PICTURE FULLSCREEN",
				
							   INT_PERS = "personal",
							   INT_IND = "index",
							   INT_PATHS = "path";
	
	boolean isPersonal;
	
	ImageView imgV = null;
	Point size = null;
	
	static int ind = -1;
	static ArrayList<String> paths = null;
	 
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_fullscreen);
		
		isPersonal = getIntent().getExtras().getBoolean(INT_PERS);
		if (ind < 0) ind = getIntent().getExtras().getInt(INT_IND, -1);
		if (paths == null) paths = getIntent().getExtras().getStringArrayList(INT_PATHS); 
		
		if (paths == null || paths.size() < 1) finish();
		if (ind < 0 || ind >= paths.size()) ind = 0;
		
		getWindowManager().getDefaultDisplay().getSize(size = new Point());
		
		imgV = (ImageView) findViewById(R.id.pictureFS);
		setImage(ind);
		
		imgV.setOnTouchListener(new SwipeBookListener() { 
			@Override public void onSwipeFromLeftToRight() { if (ind > 0) setImage(--ind); else makeToast("Nessun'immagine precedente"); } 
			@Override public void onSwipeFromRightToLeft() { if (ind + 1 < paths.size()) setImage(++ind); else makeToast("Nessun'immagine successiva"); }
		});
	}
	@Override
	protected void onDestroy() {
		ind = -1;
		paths = null;
		
		super.onDestroy();
	}
	
	private void setImage(final int i) { 
		
		Bitmap tmp = null;
		if (isPersonal)
			try { tmp = Utility.getFinalImageFromPath(paths.get(i), size); } 
			catch (IOException e) { makeToast("Unable to load image\n" + e.getMessage()); }
		else 
			try { tmp = Utility.getFinalImageFromAssets(getAssets().open(Utility.getGeneralImagePathFromId(paths.get(i))), size); } 
			catch (IOException e) { makeToast("Unable to load image\n" + e.getMessage()); }
		
		if (tmp != null) {
			final Bitmap image = tmp;
			runOnUiThread(new Runnable() { public void run() { imgV.setImageBitmap(image); }});
		} 
	}
	
	//debug
	private void makeToast(String val) { Log.d(TAG, val); Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG).show(); }
}
