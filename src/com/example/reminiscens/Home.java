package com.example.reminiscens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class Home extends Activity {

	private static final String TAG = "HOME";
	
	public int idUser;
	private Context ctx = this;
	private DBTEventiGenerali dbGenerali;
	private DBTEventiPersonali dbPersonali = new DBTEventiPersonali(this);
	private SharedPreferences prefs;
	private ListView listViewGenerali;
	private ListView listViewPersonali;

	private ImageView entireBook;
	private AnimationDrawable animlibroDxToSx;
	private AnimationDrawable animlibroSxToDx;

	private int currentStartYear = 1930;

	private ImageCache cache;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		idUser = getIntent().getExtras().getInt("idUser");
		dbGenerali = new DBTEventiGenerali(this, idUser);

		listViewPersonali = (ListView) this
				.findViewById(R.id.listviewHomePersonal);
		listViewGenerali = (ListView) this
				.findViewById(R.id.listviewHomeGeneral);

		entireBook = (ImageView) this.findViewById(R.id.ImageViewHome);

		dbGenerali.open();
		dbPersonali.open();

		listViewGenerali.setFriction(0.2f);
		listViewPersonali.setFriction(0.2f);

		cache = new ImageCache(this);
		addListViewListeners();
		displayStories();
	}

	public void onPause() {
		super.onPause();

		dbGenerali.close();
		dbPersonali.close();
	}

	public void onRestart() {
		super.onRestart();

		dbGenerali.open();
		dbPersonali.open();

		displayStories();
	}

	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Log out")
				.setMessage("Vuoi uscire da Reminiscens?")
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						prefs = PreferenceManager
								.getDefaultSharedPreferences(Home.this);
						prefs.edit().clear().commit();
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// ritorna all'activity
					}
				}).show();
	}
	
	private void setListsInvisibles() {
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				listViewGenerali.setVisibility(View.VISIBLE);
				listViewPersonali.setVisibility(View.VISIBLE);
			}

		}, 700);
	}

	private void addListViewListeners() {

		listViewGenerali.setOnTouchListener(new SwipeBookListener(
				listViewGenerali) {

			@Override
			public void onClick(int position) {
				CursorAdapter adapterGen = (CursorAdapter) listViewGenerali
						.getAdapter();
				Cursor c = adapterGen.getCursor();
				c.moveToPosition(position);
				int idStory = c.getInt(c
						.getColumnIndex(DBTEventiGenerali.DBK_Id));
				startViewStory(idStory, false);
			}

			@Override
			public void onSwipeFromRightToLeft() {
				if (currentStartYear + 10 <= 2010) {
					currentStartYear += 10;

					listViewGenerali.setVisibility(View.INVISIBLE);
					listViewPersonali.setVisibility(View.INVISIBLE);
					entireBook.setBackgroundResource(R.drawable.animdxtosx);
					animlibroDxToSx = (AnimationDrawable) entireBook
							.getBackground();

					animlibroDxToSx.stop();
					animlibroDxToSx.start();

					displayStories();
					setListsInvisibles();
				}
			}

		});

		listViewPersonali.setOnTouchListener(new SwipeBookListener(
				listViewPersonali) {
			@Override
			public void onClick(int position) {
				CursorAdapter adapterPers = (CursorAdapter) listViewPersonali
						.getAdapter();
				Cursor c = adapterPers.getCursor();
				c.moveToPosition(position);
				int idStory = c.getInt(c
						.getColumnIndex(DBTEventiPersonali.DBK_Id));
				startViewStory(idStory, true);
			}

			@Override
			public void onSwipeFromLeftToRight() {
				if (currentStartYear - 10 >= 1930) {
					currentStartYear -= 10;

					listViewGenerali.setVisibility(View.INVISIBLE);
					listViewPersonali.setVisibility(View.INVISIBLE);
					entireBook.setBackgroundResource(R.drawable.animsxtodx);
					animlibroSxToDx = (AnimationDrawable) entireBook
							.getBackground();

					animlibroSxToDx.stop();
					animlibroSxToDx.start();

					displayStories();
					setListsInvisibles();
				}
			}

		});

	}

	private void displayStories() {

		Cursor cgen = dbGenerali.fetchFromYearToYear(currentStartYear, currentStartYear + 9);
		CursorAdapter adapterGen = new CursorAdapterGeneral(ctx, cgen, cache);

		Time timeBegin = new Time();
		Time timeEnd = new Time();
		timeBegin.set(1, 0, currentStartYear);
		timeEnd.set(31, 11, currentStartYear + 9);

		Cursor cpers = dbPersonali.fetchByUserAndFromDateToDate(idUser,
				timeBegin, timeEnd);

		CursorAdapter adapterPers = new CursorAdapterPersonal(ctx, cpers, cache);

		listViewPersonali.setAdapter(adapterPers);
		listViewGenerali.setAdapter(adapterGen);
	}

	private void startViewStory(int idStory, boolean isPersonal) {
		Intent viewStoryIntent = new Intent(Home.this, ViewStory.class);

		viewStoryIntent.putExtra("idStory", idStory);
		viewStoryIntent.putExtra("idUser", idUser);
		viewStoryIntent.putExtra("isPersonal", isPersonal);

		this.startActivity(viewStoryIntent);
	}

	public void impostazioni(View v) {
		Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show();
	}

	public void amici(View v) {
		Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show();
	}

	public void aggiungi(View v) {
		Intent newStory = new Intent(Home.this, NewEditStory.class);
		newStory.putExtra("isEdit", false);
		newStory.putExtra("idUser", idUser);
		newStory.putExtra("currentStartYear", currentStartYear);

		startActivity(newStory);
	}
	
	private void makeLog(String l) { makeLog(l, true); }
	private void makeLog(String l, boolean toast) { Log.d(TAG, l); if (toast) Toast.makeText(this, l, Toast.LENGTH_LONG).show(); }

}
