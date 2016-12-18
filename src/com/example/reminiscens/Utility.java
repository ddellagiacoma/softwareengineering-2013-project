package com.example.reminiscens;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.content.Context;
import android.content.CursorLoader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;

public class Utility {

	public static Time dateFromStringToTime(String stringDate) {
		int year = Integer.parseInt(stringDate.substring(0, 4), 10);
		int month = Integer.parseInt(stringDate.substring(4, 6), 10);
		int day = Integer.parseInt(stringDate.substring(6, 8), 10);

		Time result = new Time();
		result.set(day, month - 1, year);

		return result;
	}

	public static String dateFromTimeToString(Time date) {
		return date.toString().substring(0, 8);
	}

	public static String formatDate(String date) {
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6);

		return day + "/" + month + "/" + year;
	}

	public static String unformatDate(String date) {
		String day = date.substring(0, 2);
		String month = date.substring(3, 5);
		String year = date.substring(6);

		return year + month + day;
	}

	public static String getDescriptionPreview(String description, int length) {
		String result = description.substring(0,
				Math.min(length, description.length()))
				+ "...";

		return result;
	}

	public static int[] fromIntegerListToArray(List<Integer> list) {
		int size = list.size();
		int result[] = new int[size];

		for (int i = 0; i < size; i++) {
			result[i] = list.get(i).intValue();
		}

		return result;
	}

	public static File getMediaFolder(int idStory) {
		String path = Utility.getThisProjectPath() + File.separator + idStory;
		return new File(path);
	}

	public static String getThisProjectPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "Reminiscens";
	}

	// file management
	public static boolean deleteFile(final String path) {
		File file = new File(path);
		if (file.exists())
			file.delete();
		else
			return false;

		return true;
	}

	public static String copyFileToDirecotry(final String file,
			String directory, final boolean cut) throws IOException {

		directory += directory.endsWith("/") ? "" : "/";
		String filePath = directory + file.substring(file.lastIndexOf("/") + 1);

		byte[] buff = new byte[1024];
		InputStream in = new FileInputStream(new File(file));
		OutputStream out = new FileOutputStream(new File(filePath));

		int lng = -1;
		while ((lng = in.read(buff)) > 0)
			out.write(buff, 0, lng);

		in.close();
		out.close();

		if (cut)
			deleteFile(file);

		return filePath;
	}

	public static void copyFilesToDirectory(String[] files, String directory,
			boolean cut) throws IOException {
		for (String file : files)
			copyFileToDirecotry(file, directory, cut);
	}

	public static String getPathFromUri(Context ctx, Uri src) {
		Cursor cursor = null;

		try {
			final String DATA = MediaStore.Images.Media.DATA;
			CursorLoader loader = new CursorLoader(ctx, src,
					new String[] { DATA }, null, null, null);
			cursor = loader.loadInBackground();
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(DATA));
		} catch (Exception e) {
			return null;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	// image management
	final static FileFilter jpgFilter = new FileFilter() {
		@Override
		public boolean accept(File fl) {
			return fl.getName().endsWith(".jpg");
		}
	};

	public static String getGeneralImagePathFromId(int id) { return "g" + (id-1) + ".jpg"; }
	public static String getGeneralImagePathFromId(String id) { return getGeneralImagePathFromId(Integer.valueOf(id)); }
	public static String[] getListOfJPGs(File root) {
		File[] files = root.listFiles(jpgFilter);

		if (files == null)
			return null;

		int lng = files.length;
		String[] paths = new String[lng];
		for (int x = 0; x < lng; x++)
			paths[x] = files[x].getAbsolutePath();

		return paths;
	}

	private static Options getResizedImageOption(String path, int reqW, int reqH) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		int height = options.outHeight, width = options.outWidth, ratio = 1;
		if (height > reqH || width > reqW) {
			final int heightRatio = Math.round((float) height / (float) reqH);
			final int widthRatio = Math.round((float) width / (float) reqW);

			ratio = Math.min(heightRatio, widthRatio);
		}
		options.inSampleSize = ratio;
		options.inJustDecodeBounds = false;

		return options;
	}
	private static Options getResizedImageOption(InputStream in, int reqW, int reqH) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options); 
		
		int height = options.outHeight, width = options.outWidth, ratio = 1;
		if (height > reqH || width > reqW) {
			final int heightRatio = Math.round((float) height / (float) reqH);
			final int widthRatio = Math.round((float) width / (float) reqW);

			ratio = Math.min(heightRatio, widthRatio);
		}
		options.inSampleSize = ratio;
		options.inJustDecodeBounds = false;

		return options;
	}

	public static int getBitmapOrientationFromPath(String path)
			throws IOException {
		int deg = 0;
		switch (new ExifInterface(path)
				.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL)) {
		case ExifInterface.ORIENTATION_ROTATE_270:
			deg = 270;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			deg = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			deg = 90;
			break;
		}
		return deg;
	}

	public static Bitmap rotateBitmap(Bitmap src, int degrees) {
		Log.d("rotate degrees", String.valueOf(degrees));
		Bitmap tmpB = null;
		try {
			Matrix m = new Matrix();
			m.postRotate(degrees);
			tmpB = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
					src.getHeight(), m, true);
		} catch (Exception e) {
			Log.d("rotateBitmap ERROR", e.getMessage());
		}
		return tmpB;
	}

	public static Bitmap getFinalImageFromPath(String path, Point size)
			throws IOException {
		return getFinalImageFromPath(path, size.x, size.y);
	}

	public static Bitmap getFinalImageFromPath(String path, int reqW, int reqH) throws IOException {
		return rotateBitmap(BitmapFactory.decodeFile(path, Utility.getResizedImageOption(path, reqW, reqH)),
							getBitmapOrientationFromPath(path));
	}
	public static Bitmap getFinalImageFromAssets(InputStream in, Point size) throws IOException {
		return getFinalImageFromAssets(in, size.x, size.y);
	}
	public static Bitmap getFinalImageFromAssets(InputStream in, int reqW, int reqH) throws IOException {
		return BitmapFactory.decodeStream(in, null, Utility.getResizedImageOption(in, reqW, reqH));
	}
}
