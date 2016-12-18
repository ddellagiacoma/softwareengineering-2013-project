package com.example.reminiscens;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CursorAdapterPersonal extends CursorAdapter {

	private Context ctx;
	private ImageCache cache;

	public CursorAdapterPersonal(Context context, Cursor c, ImageCache cache) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		this.cache = cache;
		this.ctx = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		String title = c.getString(c
				.getColumnIndex(DBTEventiPersonali.DBK_Title));
		String text = c.getString(c
				.getColumnIndex(DBTEventiPersonali.DBK_Description));
		String time = c
				.getString(c.getColumnIndex(DBTEventiPersonali.DBK_Date));
		int id = c.getInt(c.getColumnIndex(DBTEventiPersonali.DBK_Id));

		text = Utility.getDescriptionPreview(text, 50);

		ViewHolder holder = (ViewHolder) view.getTag();

		holder.titleText.setText(title + " - " + Utility.formatDate(time));
		holder.descText.setText(text);

		cache.loadBitmap(id, holder.imageView, ImageCache.PERSONAL);
	}

	@Override
	public View newView(Context context, Cursor cursors, ViewGroup parent) {

		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newView = mInflater.inflate(R.layout.row, null);
		holder = new ViewHolder();

		holder.titleText = (TextView) newView.findViewById(R.id.title_row);
		holder.descText = (TextView) newView.findViewById(R.id.text_row);
		holder.imageView = (ImageView) newView.findViewById(R.id.image_row);

		newView.setTag(holder);

		return newView;
	}

	static class ViewHolder {
		public TextView titleText;
		public TextView descText;
		public ImageView imageView;
	}

}
