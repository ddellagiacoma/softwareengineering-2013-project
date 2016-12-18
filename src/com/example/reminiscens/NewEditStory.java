package com.example.reminiscens;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class NewEditStory extends Activity implements OnItemClickListener, OnItemLongClickListener {

	private static final int GALLERY_REQUEST_CODE = 128;
	private static final int CAMERA_REQUEST_CODE = 129;
	private long image_index;
	private String image_path = null;

	private Context ctx;
	private PictureAdapter adapter;

	private TextView viewData;
	private EditText editDescription;
	private EditText editTitle;
	private EditText editLocation;

	private boolean isEdit;
	private int idUser;

	private File tmpDir;
	private File saveDir;
	DBTEventiPersonali dbPersonali = new DBTEventiPersonali(this);

	// initialized only for edit
	private int idStory;
	// initialized only for new
	private int currentStartYear;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_story);

		ctx = this;
		
		Time time = new Time();
		time.setToNow();
		image_index = time.toMillis(true);
		
		viewData = (TextView) this.findViewById(R.id.data);
		editDescription = (EditText) this.findViewById(R.id.edit_description);
		editTitle = (EditText) this.findViewById(R.id.edit_title);
		editLocation = (EditText) this.findViewById(R.id.editLocation);

		GridView gallery = (GridView) this.findViewById(R.id.pictures);

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		path += File.separator + "Reminiscens";
		path += File.separator + "tmp";
		tmpDir = new File(path);	

		removeDirectoryTmp();

		Bundle bundle = this.getIntent().getExtras();
		isEdit = bundle.getBoolean("isEdit");
		idUser = bundle.getInt("idUser");

		if (isEdit) {
			idStory = bundle.getInt("idStory");
			viewData.setText(bundle.getString("formattedData"));
			editDescription.setText(bundle.getString("description"));
			editTitle.setText(bundle.getString("title"));
			editLocation.setText(bundle.getString("location"));
			
			saveDir = Utility.getMediaFolder(idStory);
			
			gallery.setAdapter(adapter = new PictureAdapter(this, new File[] {
					saveDir, tmpDir }));
		} else {
			currentStartYear = bundle.getInt("currentStartYear");
			viewData.setText("01/01/" + currentStartYear);
			
			gallery.setAdapter(adapter = new PictureAdapter(this, new File[] { tmpDir }));
		}

		gallery.setOnItemClickListener(this);
		gallery.setOnItemLongClickListener(this);
		
		System.gc();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int ind, long id) {
		try {
		Intent pfsIntent = new Intent(NewEditStory.this, PictureFullscreen.class);
		pfsIntent.putExtra(PictureFullscreen.INT_IND, ind);
		pfsIntent.putExtra(PictureFullscreen.INT_PATHS, adapter.getIteams());
		
		startActivity(pfsIntent);
		} catch (Exception e) { Toast.makeText(this, "Error in onIteamClick:\n" + e.getMessage(), Toast.LENGTH_LONG).show(); }
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int ind, long id) {
		final String path = (String) adapter.getItem(ind);

		PopupMenu popup = new PopupMenu(ctx, v);
		popup.getMenuInflater().inflate(R.menu.image_menu, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_del:
					if (Utility.deleteFile(path))
						adapter.removeImg(path);
					break;
				}
				return true;
			}
		});
		popup.show();

		return true;
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Salvataggio")
				.setMessage("Vuoi salvare prima di uscire?")
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						save();
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						removeDirectoryTmp();
						finish();
					}
				}).show();
	}

	public void onDataChange(View view) {
		OnDateSetListener dateSetListener = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				Time data = new Time();
				data.set(dayOfMonth, monthOfYear, year);
				viewData.setText(Utility.formatDate(Utility
						.dateFromTimeToString(data)));

			}

		};

		DatePickerDialog datePicker;

		if (isEdit) {
			String data = viewData.getText().toString();
			int year = Integer.parseInt(data.substring(6), 10);
			int month = Integer.parseInt(data.substring(3, 5), 10);
			int day = Integer.parseInt(data.substring(0, 2), 10);

			datePicker = new DatePickerDialog(this, dateSetListener, year,
					month - 1, day);
		} else {
			datePicker = new DatePickerDialog(this, dateSetListener,
					currentStartYear, 1, 1);
		}

		datePicker.show();
	}

	public void onSaveClick(View view) {
		save();
		finish();
	}

	private void save() {
		dbPersonali.open();

		String data = Utility.unformatDate(viewData.getText().toString());
		String description = editDescription.getText().toString();
		String title = editTitle.getText().toString();
		String location = editLocation.getText().toString();

		if (isEdit) {
			ContentValues content = new ContentValues();

			content.put(DBTEventiPersonali.DBK_Date, data);
			content.put(DBTEventiPersonali.DBK_Description, description);
			content.put(DBTEventiPersonali.DBK_Title, title);
			content.put(DBTEventiPersonali.DBK_Location, location);

			dbPersonali.update(idStory, content);
		} else {
			idStory = dbPersonali.insert(Utility.dateFromStringToTime(data),
					description, location, idUser, title,
					DBTEventiPersonali.SET_NULL);
			
			saveDir = Utility.getMediaFolder(idStory);
			isEdit = true;
		}

		dbPersonali.close();

		if (tmpDir.exists()) {

			if (!saveDir.exists()) {
				saveDir.mkdirs();
			}

			// merge audio tracks
			File audioTrack = new File(saveDir.getAbsoluteFile()
					+ File.separator + AudioManager.NAME_AUDIO_TRACK);

			if (audioTrack.exists()) {
				String audioPathTmp = tmpDir.getAbsolutePath() + File.separator
						+ AudioManager.NAME_AUDIO_TRACK;

				AudioManager.merge(audioTrack.getAbsolutePath(), audioPathTmp);
			} else {
				File audioTrackTmp = new File(tmpDir.getAbsolutePath()
						+ File.separator + AudioManager.NAME_AUDIO_TRACK);
				audioTrackTmp.renameTo(audioTrack);
			}

			// sposta tutti i file tmp
			String[] tmpPaths = tmpDir.list();

			for (int i = 0; i < tmpPaths.length; i++) {
				File toCopy = new File(tmpDir.getAbsolutePath()
						+ File.separator + tmpPaths[i]);
				toCopy.renameTo(new File(saveDir.getAbsolutePath()
						+ File.separator + tmpPaths[i]));
			}

			removeDirectoryTmp();
		}

		if (saveDir.exists()) {
			if (saveDir.list().length == 0) {
				saveDir.delete();
			}
		}
	}

	public void onManageMedia(View view) {
		CharSequence[] options = { "Aggiungi audio", "Elimina audio",
				"Carica foto", "Scatta foto" };
		AlertDialog.Builder choiceDialog = new AlertDialog.Builder(this);

		choiceDialog.setTitle("Scegli cosa fare");

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				tmpDir.mkdirs();
				
				switch (which) {
				case 0:
					addAudio();
					break;
				case 1:
					confirmDeleteAudio();
					break;
				case 2:
					loadPicture();
					break;
				case 3:
					takePicture();
					break;
				}
			}
		};

		choiceDialog.setItems(options, listener);
		choiceDialog.show();
	}

	private void removeDirectoryTmp() {
		if (tmpDir.exists()) {
			String[] filesPath = tmpDir.list();

			if (filesPath != null) {
				for (int i = 0; i < filesPath.length; i++) {
					File toDelete = new File(tmpDir.getAbsolutePath()
							+ File.separator + filesPath[i]);
					toDelete.delete();
				}
			}

			tmpDir.delete();
		}
	}

	private void removeAndCreateDirectoryTmp() {
		removeDirectoryTmp();
		tmpDir.mkdirs();
	}

	private void confirmDeleteAudio() {
		new AlertDialog.Builder(this).setTitle("Conferma eliminazione")
				.setMessage("Vuoi veramente eliminare la traccia audio?")
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						deleteAudio();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				}).show();
	}

	private void deleteAudio() {
		String pathAudioTrack = Environment.getExternalStorageDirectory()
				+ File.separator + "Reminiscens" + File.separator + idStory
				+ File.separator + AudioManager.NAME_AUDIO_TRACK;

		File audioTrack = new File(pathAudioTrack);

		if (audioTrack.exists()) {
			audioTrack.delete();
			Toast.makeText(this, "File audio eliminato", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "Nessun file audio presente",
					Toast.LENGTH_LONG).show();
		}
	}

	private void addAudio() {
		Intent record = new Intent(NewEditStory.this, Recorder.class);
		record.putExtra("pathTmp", tmpDir.getAbsolutePath() + File.separator
				+ AudioManager.NAME_AUDIO_TRACK);
		startActivity(record);
	}

	private void loadPicture() {
		try { startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), GALLERY_REQUEST_CODE); } 
		catch (Exception e) { Log.d("LOAD PICTURE", e.getMessage()); image_path = null; }
	}

	// scatta foto
	private void takePicture() {
		try {
			File next_img = null;
			do { next_img = new File(tmpDir, "img_" + String.valueOf(++image_index) + ".jpg"); } 
			while (next_img.exists());
			
			image_path = next_img.getAbsolutePath();
			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
										.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(next_img))
										.putExtra("return-data", true), 
								   CAMERA_REQUEST_CODE);
			
		} catch (Exception e) { Log.d("TAKE PICTURE", e.getMessage()); image_path = null; }
	}
	
	// ricevi risultati activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String path = null;
		
		switch (requestCode) {
			case CAMERA_REQUEST_CODE:
				if (image_path != null && image_path.length() > 0 && resultCode == RESULT_OK) { path = image_path; image_path = null; }
			break;
			case GALLERY_REQUEST_CODE:
				if (resultCode == RESULT_OK) { 
					try { path = Utility.copyFileToDirecotry(Utility.getPathFromUri(this, data.getData()), tmpDir.getAbsolutePath(), false); } 
					catch (Exception e) { Log.d("GALLERY REQUEST RESULT", e.getMessage()); }
				}
			break;
		}
		
		if (path != null && path.length() > 0) adapter.insertImg(path);
	}
	
	//DEGUB
	private void makeToast(String val) { Toast.makeText(this, val, Toast.LENGTH_LONG).show(); }
}
