package com.example.reminiscens;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ImageCache {

	private LruCache<String, Bitmap> mMemoryCache;
	private final int HEIGHT = 30;
	private final int WIDTH = 30;

	public static final int PERSONAL = 0;
	public static final int GENERAL = 1;
	private Context ctx;

	public ImageCache(Context ctx) {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		this.ctx = ctx;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void loadBitmap(int resId, ImageView mImageView, int type) {
		final String imageKey = (type == PERSONAL ? "pers" : "gen")
				+ String.valueOf(resId);

		final Bitmap bitmap = getBitmapFromMemCache(imageKey);
		if (bitmap != null) {
			mImageView.setImageBitmap(bitmap);
		} else {
			Bitmap imgScaled = (type == PERSONAL ? this.getFilePersonal(resId)
					: this.getFileGeneral(resId));

			if (imgScaled != null) {
				addBitmapToMemoryCache(imageKey, imgScaled);
				mImageView.setImageBitmap(imgScaled);
			} else {
				mImageView.setImageBitmap(null);
			}
		}

	}

	private Bitmap getFilePersonal(int id) {
		File imgDir = Utility.getMediaFolder(id);

		if (imgDir.exists()) {
			String[] jpgPaths = Utility.getListOfJPGs(imgDir);

			if (jpgPaths == null)
				return null;

			if (jpgPaths.length != 0) {
				try {
					return Utility.getFinalImageFromPath(jpgPaths[0],
							this.WIDTH, this.HEIGHT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private Bitmap getFileGeneral(int id) {
		AssetManager am = ctx.getAssets();
		InputStream in;
		try {
			in = am.open(Utility.getGeneralImagePathFromId(id));
			return Utility.getFinalImageFromAssets(in, this.WIDTH, this.HEIGHT);
		} catch (IOException e) {
			System.err.print("g" + id + "not found");
		}

		return null;
	}
}