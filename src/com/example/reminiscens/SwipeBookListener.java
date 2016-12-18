package com.example.reminiscens;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

public class SwipeBookListener implements OnTouchListener {

	private final String TAG = "SWIPE BOOK LISTENER";
	
	private float startPosX;
	private float endPosX;
	private float deltaX;

	private float startPosY;
	private float endPosY;
	private float deltaY;

	private float minDeltaSwipeX;
	private float minDeltaSwipeY;

	private ListView listView = null;

	public SwipeBookListener() {}
	public SwipeBookListener(ListView listView) {
		this.listView = listView;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		minDeltaSwipeX = view.getWidth() / 2;
		minDeltaSwipeY = view.getHeight() / 3;

		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			startPosX = motionEvent.getX();
			startPosY = motionEvent.getY();

		} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
			endPosX = motionEvent.getX();
			endPosY = motionEvent.getY();

			deltaX = startPosX - endPosX;
			deltaY = startPosY - endPosY;

			if ((Math.abs(deltaX) >= minDeltaSwipeX)
					&& (Math.abs(deltaY) <= minDeltaSwipeY)) {
				if (deltaX > 0) {
					onSwipeFromRightToLeft();
				} else {
					onSwipeFromLeftToRight();
				}
			} else if (listView != null && Math.abs(deltaY) <= minDeltaSwipeY) {
				int position = listView.pointToPosition((int) endPosX, (int) endPosY);
				if (position != AbsListView.INVALID_POSITION) onClick(position);
			}
		}

		return false;
	}

	public void onClick(int position) {

	}

	public void onSwipeFromRightToLeft() {

	}

	public void onSwipeFromLeftToRight() {

	}

}
