package com.example.reminiscens;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewStory extends Activity implements OnItemClickListener {

	private static final String TAG = "VISUAL STORY";
	
	private static final int NEW_STORE_CODE = 128;
	private PictureAdapter adapter;

	private File storyDir;

	private int idStory;
	private boolean isPersonal;
	private int idUser;

	private TextView titleText;
	private TextView descText;
	private TextView dataText;
	private Button modifyButton;
	private Button playButton;

	// for modify intent
	private String locationPersonal;
	private String descriptionPersonal;

	private DBTEventiGenerali dbGenerali;
	private DBTEventiPersonali dbPersonali;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_story);

		titleText = (TextView) this.findViewById(R.id.titolo);
		descText = (TextView) this.findViewById(R.id.testo_storia);
		dataText = (TextView) this.findViewById(R.id.data);
		modifyButton = (Button) this.findViewById(R.id.button_modifica);
		playButton = (Button) this.findViewById(R.id.button_ascolta_registrazione);

		idUser = getIntent().getExtras().getInt("idUser");
		idStory = getIntent().getExtras().getInt("idStory");
		isPersonal = getIntent().getExtras().getBoolean("isPersonal");

		dbGenerali = new DBTEventiGenerali(this, idUser);
		dbPersonali = new DBTEventiPersonali(this);

		populateViews();
		if (!isPersonal) {
			modifyButton.setVisibility(View.INVISIBLE);
			playButton.setVisibility(View.INVISIBLE);
		}
		
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int ind, long id) {
		try {
		Intent pfsIntent = new Intent(ViewStory.this, PictureFullscreen.class);
		pfsIntent.putExtra(PictureFullscreen.INT_PERS, isPersonal);
		pfsIntent.putExtra(PictureFullscreen.INT_IND, ind);
		pfsIntent.putExtra(PictureFullscreen.INT_PATHS, adapter.getIteams());
		
		startActivity(pfsIntent);
		} catch (Exception e) { Toast.makeText(this, "Error in onIteamClick:\n" + e.getMessage(), Toast.LENGTH_LONG).show(); }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateViews();
	}

	private void populateViews() {
		if (isPersonal) {
			adapter = new PictureAdapter(this, storyDir = new File(Utility.getThisProjectPath() + File.separator + idStory));
			
			playButton.setVisibility(new File(storyDir.getAbsolutePath() + File.separator + AudioManager.NAME_AUDIO_TRACK).exists() ? View.VISIBLE : View.GONE);
			
			dbPersonali.open();
			Cursor c = dbPersonali.fetchById(idStory);
			c.moveToFirst();

			locationPersonal = c.getString(c
					.getColumnIndex(DBTEventiPersonali.DBK_Location));

			String title = c.getString(c
					.getColumnIndex(DBTEventiPersonali.DBK_Title));
			String date = Utility.formatDate(c.getString(c
					.getColumnIndex(DBTEventiPersonali.DBK_Date)));
			String desc = (locationPersonal.equals("") ? ""
					: (locationPersonal + " - "));
			descriptionPersonal = c.getString(c
					.getColumnIndex(DBTEventiPersonali.DBK_Description));

			titleText.setText(title);
			descText.setText(desc + descriptionPersonal);
			dataText.setText(date);

			dbPersonali.close();
		} else {
			adapter = new PictureAdapter(this, false, new String[] {String.valueOf(idStory)});
			
			dbGenerali.open();
			Cursor c = dbGenerali.fetchById(idStory);
			c.moveToFirst();

			String title = c.getString(c.getColumnIndex(DBTTipologia.DBK_Name));
			String year = c.getString(c.getColumnIndex(DBTEventiGenerali.DBK_Year));
			String desc = c.getString(c
					.getColumnIndex(DBTEventiGenerali.DBK_Description));

			titleText.setText(title);
			descText.setText(desc);
			dataText.setText(year);

			dbGenerali.close(); 
		}
		
		if (adapter != null) {
			GridView gallery = null;
			(gallery = (GridView) this.findViewById(R.id.pictures)).setAdapter(adapter);
			if (gallery != null) gallery.setOnItemClickListener(this);
		}
	}

	public void invia_storia(View v) {
		Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show();
	}

	public void onPlay(View view) {
		File audioTrack = new File(storyDir.getAbsolutePath() + File.separator + AudioManager.NAME_AUDIO_TRACK);

		if (!audioTrack.exists()) {
			Toast.makeText(this, "Nessun file da riprodurre", Toast.LENGTH_LONG)
					.show();
		} else {
			AudioManager.startPlayer(this, audioTrack.getAbsolutePath());
		}
	}

	public void onModifica(View v) {
		CharSequence[] options = { "Modifica Storia", "Elimina storia" };

		AlertDialog.Builder choiceDialog = new AlertDialog.Builder(this);
		choiceDialog.setTitle("Scegli cosa fare");

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					modifyStory();
				} else if (which == 1) {
					deleteStory();
				}
			}
		};

		choiceDialog.setItems(options, listener);
		choiceDialog.show();
	}

	private void modifyStory() {
		Intent modifyIntent = new Intent(ViewStory.this, NewEditStory.class);
		modifyIntent.putExtra("isEdit", true);

		modifyIntent.putExtra("idStory", idStory);
		modifyIntent.putExtra("idUser", idUser);
		modifyIntent.putExtra("title", titleText.getText().toString());
		modifyIntent.putExtra("description", descriptionPersonal);
		modifyIntent.putExtra("location", locationPersonal);
		modifyIntent.putExtra("formattedData", dataText.getText().toString());

		this.startActivityForResult(modifyIntent, NEW_STORE_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case NEW_STORE_CODE: if (resultCode == RESULT_OK) adapter.setImgs(new File[] {storyDir});
		break;
		}
	}

	private void deleteStory() {
		new AlertDialog.Builder(this)
				.setTitle("Conferma eliminazione")
				.setMessage(
						"Vuoi veramente eliminare la storia e tutte le sue fotografie?")
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// delete record in db

						dbPersonali.open();
						dbPersonali.remove(idStory);
						dbPersonali.close();

						// delete media folder
						String pathSaveDir = Environment
								.getExternalStorageDirectory() + File.separator;
						pathSaveDir += "Reminiscens" + File.separator + idStory;

						File saveDir = new File(pathSaveDir);

						if (saveDir.exists()) {
							String[] filesPath = saveDir.list();

							if (filesPath != null) {
								for (int i = 0; i < filesPath.length; i++) {
									File toDelete = new File(saveDir
											.getAbsoluteFile()
											+ File.separator
											+ filesPath[i]);
									toDelete.delete();
								}
							}
							saveDir.delete();
						}
						// exit the activity
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				}).show();
	}

	private void makeToast(String val) { Log.d(TAG, val); Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG).show(); }
}
