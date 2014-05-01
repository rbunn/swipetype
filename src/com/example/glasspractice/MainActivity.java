/*https://github.com/googleglass/gdk-timer-sample/tree/master/src/com/google/android/glass/sample/timer*/

package com.example.glasspractice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class MainActivity extends Activity {
	
	Card mainCard;
	private GestureDetector mGestureDetector;
	public List<Gesture> gestures;
	StringBuilder stringBuilder;
	public boolean gestureMade;
	public boolean running;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainCard = new Card(this);
		mGestureDetector = createGestureDetector(this);
		setContentView(mainCard.toView());
	}
	
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		//Create a base listener for generic gestures
		gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				gestureMade = true;
				switch (gesture) {
				case TAP:
					//exit input mode
				case SWIPE_RIGHT:
					gestures.add(gesture);
					if(!running) {
						new asyncTask().execute();
					}
					return true;
				case SWIPE_LEFT:
					if(running)
						gestures.add(gesture);
					else
						if(stringBuilder.length() > 0) {
							stringBuilder.setLength(stringBuilder.length() - 1);
							mainCard.setText(stringBuilder.toString());
						}
					return true;
				case SWIPE_UP:
					if(running) {
						gestures.add(gesture);
						}
					return true;
				case SWIPE_DOWN:
					if(running) {
						gestures.add(gesture);
						}
					return true;
				default:
					Toast.makeText(getApplicationContext(), "not handled", Toast.LENGTH_LONG);
					break;
				}
				return false;
				}
			});
		return gestureDetector;
		}
	
	class asyncTask extends AsyncTask<Void, Void, Void>{
		long startTime;
		long elapsedTime;
		
		@Override
		protected Void doInBackground(Void... params) {
			startTime = System.currentTimeMillis();
			elapsedTime = 0L;
			running = true;
			Toast.makeText(getApplicationContext(), toLetter(gestures), Toast.LENGTH_LONG);
			while(elapsedTime < 1*1000) {
				elapsedTime = (new Date()).getTime() - startTime;
				if(gestureMade) {
					startTime = System.currentTimeMillis();
					gestureMade = false;
				}
			}
			return null;
		}
		
		protected void onPostExectute() {
			running = false;
			stringBuilder.append(toLetter(gestures));
			gestures.clear();
			mainCard.setText(stringBuilder.toString());
		}
		
	}
	
	public char toLetter(List<Gesture> gestures){
		 if(gestures == Arrays.asList(Gesture.SWIPE_RIGHT))
			 return 'a';
		 if(gestures == Arrays.asList(Gesture.SWIPE_RIGHT, Gesture.SWIPE_DOWN))
			 return 'b';
		 if(gestures == Arrays.asList(Gesture.SWIPE_RIGHT, Gesture.SWIPE_UP))
			 return 'c';
		 if(gestures == Arrays.asList(Gesture.SWIPE_RIGHT, Gesture.SWIPE_LEFT))
			 return 'd';
		 else
			 Toast.makeText(getApplicationContext(), "bad input", Toast.LENGTH_LONG);
		 
		return ' ';
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

	@Override
	public void onResume() {
		mainCard.setText(stringBuilder.toString());
		super.onResume();
	}
	
}
