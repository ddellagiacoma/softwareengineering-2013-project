package com.example.reminiscens;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PictureAdapter extends BaseAdapter {

	private static final String TAG = "PICTURE ADAPTER";
	
	static final int image_w = 110,
			 image_h = 110;
	
	final ArrayList<String> imgs = new ArrayList<String>();
	static Context ctx = null;
	
	boolean personal;
	
	public PictureAdapter(Context ctx, boolean personal, String[] imgs) { 
		PictureAdapter.ctx = ctx; 
		this.personal = personal;
		for (String img : imgs) this.imgs.add(img); 
	}
	public PictureAdapter(Context ctx, boolean personal, ArrayList<String> imgs) {
		PictureAdapter.ctx = ctx;
		this.personal = personal;
		this.imgs.addAll(imgs);
	}
	public PictureAdapter(Context ctx, File root) {
		PictureAdapter.ctx = ctx;
		this.personal = true;
		if (root != null && root.exists()) for (String img : Utility.getListOfJPGs(root)) imgs.add(img);
	}
	public PictureAdapter(Context ctx, File[] roots) {
		PictureAdapter.ctx = ctx;
		this.personal = true;
		for (File root : roots) if (root != null && root.exists()) for (String img : Utility.getListOfJPGs(root)) imgs.add(img);
	}
	
	public PictureAdapter setPersonal(boolean personal) { this.personal = personal; return this; }
	public boolean getPersonal() { return personal; }
	
	public boolean insertImg(String path) { boolean ret; if (ret = !imgs.contains(path)) { imgs.add(path); notifyDataSetChanged(); } return ret; }
	public boolean removeImg(String path) { boolean ret; if (ret = imgs.contains(path)) { imgs.remove(path); notifyDataSetChanged(); } return ret; }
	
	public void setImgs(String[] imgs) { this.imgs.clear(); for (String img : imgs) this.imgs.add(img); notifyDataSetChanged(); }
	public void setImgs(ArrayList<String> imgs) { this.imgs.clear(); this.imgs.addAll(imgs); notifyDataSetChanged(); }
	public void setImgs(File[] files) { 
		this.imgs.clear();
		personal = true;
		for (File file : files) 
			if (file != null && file.exists()) 
				for (String path : Utility.getListOfJPGs(file)) 
					imgs.add(path); 
		
		notifyDataSetChanged(); 
	}
	public String[] getImgs() { String[] ret = new String[imgs.size()]; return imgs.toArray(ret); }
	
	@Override public int getCount() { return this.imgs.size(); }

	@Override public Object getItem(int ind) { if (getCount() > ind) return this.imgs.get(ind); return null; }
	public ArrayList<String> getIteams() { return imgs; }

	@Override public long getItemId(int ind) { return ind; }

	@Override public View getView(int ind, View view, ViewGroup grp) {
		if (PictureAdapter.ctx == null || ind >= getCount()) return null;
		
		Log.d(TAG, "In getView with id = " + ind);
		
		Bitmap tmpBitmap = null;
		if (personal) 
			try { tmpBitmap = Utility.getFinalImageFromPath(this.imgs.get(ind), image_w, image_h); } 
			catch (Exception e) { Log.d(TAG, "Error: " + e.getMessage()); }
		else 
			try { tmpBitmap = Utility.getFinalImageFromAssets(PictureAdapter.ctx.getAssets().open(Utility.getGeneralImagePathFromId(this.imgs.get(ind))), image_w, image_h); }
			catch (Exception e) { Log.d(TAG, "Error: " + e.getMessage()); }
		
		ImageView ret = new ImageView(ctx);
		if (tmpBitmap != null) { 
			ret.setImageBitmap(tmpBitmap);
			ret.setScaleType(ScaleType.FIT_XY);
	        ret.setLayoutParams(new GridView.LayoutParams(image_w, image_h));
		}
		
		return ret;
	}

}
