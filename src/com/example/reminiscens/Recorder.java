package com.example.reminiscens;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Recorder extends Activity {

	private final int IS_RECORDING = 0;
	private final int IS_WAITING_FOR_REC = 1;

	private Button recButton;
	private ImageButton confirmButton;
	private ImageButton redoButton;
	private ImageButton playButton;
	private TextView timerText;

	private int recState = 1;
	private String URIsong;
	private AudioManager audioManager;

	private int timeCount;
	private String stringTimer;
	private Handler handler;
	private Runnable runnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recorder);
		
		recButton = (Button) this.findViewById(R.id.button_rec);
		redoButton = (ImageButton) this.findViewById(R.id.redo_button);
		playButton = (ImageButton) this.findViewById(R.id.play_button);
		timerText = (TextView) this.findViewById(R.id.timerText);
		confirmButton = (ImageButton) this.findViewById(R.id.button_confirm);

		URIsong = getIntent().getExtras().getString("pathTmp");

		audioManager = new AudioManager(URIsong);

		initHandler();
	}

	private void initHandler() {
		handler = new Handler();
		runnable = new Runnable() {

			@Override
			public void run() {
				timeCount++;
				if (timeCount / 60 < 10) {
					stringTimer += "0";
				}
				stringTimer = String.valueOf(timeCount / 60);
				stringTimer += ":";
				if (timeCount % 60 < 10) {
					stringTimer += "0";
				}
				stringTimer += String.valueOf(timeCount % 60);

				timerText.setText(stringTimer);
				handler.postDelayed(this, 1000);
			}

		};
	}

	private void startTimer() {
		handler.postDelayed(runnable, 0);
	}

	private void stopTimer() {
		handler.removeCallbacks(runnable);
		timerText.setVisibility(View.INVISIBLE);
		timerText.setText("0:00");
	}

	public void onRecStartAndStop(View view) {

		switch (recState) {
		case IS_WAITING_FOR_REC:
			audioManager.startRecording();
			recButton.setBackgroundResource(R.drawable.stop_selector);
			recState = IS_RECORDING;
			startTimer();
			break;
		case IS_RECORDING:
			audioManager.stopRecording();
			recButton.setVisibility(View.INVISIBLE);
			redoButton.setVisibility(View.VISIBLE);
			playButton.setVisibility(View.VISIBLE);
			confirmButton.setVisibility(View.VISIBLE);
			recState = IS_WAITING_FOR_REC;
			timerText.setVisibility(View.INVISIBLE);
			stopTimer();
			break;
		}
	}

	public void onRedoClick(View view) {
		redoButton.setVisibility(View.INVISIBLE);
		audioManager.deleteTmpFile();
		recButton.setVisibility(View.VISIBLE);
		playButton.setVisibility(View.INVISIBLE);
		confirmButton.setVisibility(View.INVISIBLE);
		timerText.setVisibility(View.VISIBLE);
		timeCount = 0;
		recButton.setBackgroundResource(R.drawable.rec_selector);
	}

	public void onConfirmClick(View view) {
		finish();
	}

	public void onDeleteClick(View view) {
		if (recState == IS_RECORDING) {
			audioManager.stopRecording();
		}
		audioManager.deleteTmpFile();
		finish();
	}

	public void onPlayClick(View view) {
		AudioManager.startPlayer(this, URIsong);
	}

	@Override
	public void onPause() {
		super.onPause();
		audioManager.onPause();
	}

}
