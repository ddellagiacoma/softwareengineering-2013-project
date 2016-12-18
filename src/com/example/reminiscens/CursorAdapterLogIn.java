package com.example.reminiscens;

import com.example.reminiscens.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CursorAdapterLogIn extends CursorAdapter {

	public CursorAdapterLogIn(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		String name = c.getString(c.getColumnIndex(DBTUser.DBK_Name))+ "   " + c.getString(c.getColumnIndex(DBTUser.DBK_Surname));
		String date= c.getString(c.getColumnIndex(DBTUser.DBK_Birthday));
		
		
		TextView nameText = (TextView) view.findViewById(R.id.nome_rowUtente);
		TextView dateText = (TextView) view.findViewById(R.id.data_rowUtente);
		
		nameText.setText(name);
		dateText.setText(Utility.formatDate(date));
	}

	@Override
	public View newView(Context context, Cursor cursors, ViewGroup parent) {
		final View view = LayoutInflater.from(context).inflate(R.layout.rowlogin, parent, false);
	    return view;
	}

}
