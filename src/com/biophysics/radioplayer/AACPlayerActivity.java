/*
 ** AACPlayer - Freeware Advanced Audio (AAC) Player for Android
 ** Copyright (C) 2011 Spolecne s.r.o., http://www.spoledge.com
 **  
 ** This program is free software; you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation; either version 3 of the License, or
 ** (at your option) any later version.
 ** 
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 ** GNU General Public License for more details.
 ** 
 ** You should have received a copy of the GNU General Public License
 ** along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/
package com.biophysics.radioplayer;

//import java.net.URL;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.lang.String;
//import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import android.app.Activity;
//import android.content.Intent;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;



//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
import android.widget.ImageButton; //import android.widget.EditText;
import android.widget.ProgressBar; //import android.widget.TextView;
import android.widget.TextView;


import com.biophysics.radioplayer.R;

/**
 * This is the main activity.
 */
public class AACPlayerActivity extends Activity implements
		View.OnClickListener, PlayerCallback {

	private ImageButton mPlay1;
	private ImageButton mPlay1xtra;
	private ImageButton mPlay2;
	private ImageButton mPlay3;
	private ImageButton mPlay4;
	private ImageButton mPlay4xtra;
	private ImageButton mPlay5;
	private ImageButton mPlay5xtra;
	private ImageButton mPlayAsian;
	private ImageButton mStop;
	private ImageButton mPlayWs;

	TelephonyManager telephonyManager;
	PhoneStateListener listener;
	

    AudioManager mAudioManager;
    NotificationManager mNotificationManager;

// This buffer does not matter karthik
	static final int txtBufAudio = 1900;
	static final int txtBufDecode = 700;
    private static final String LOG = "Progress bar ";

	private ProgressBar progress;
	private Handler uiHandler;


	private AACPlayer aacPlayer;


	// //////////////////////////////////////////////////////////////////////////
	// PlayerCallback
	// //////////////////////////////////////////////////////////////////////////

	private boolean playerStarted;


	public void displayAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("No network connection");
		builder.setMessage("Please check your internet connection.");
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.show();

		// Must call show() prior to fetching text view
		TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.LEFT);
		
	}
	
	public void playerStarted() {
		uiHandler.post(new Runnable() {
			public void run() {
				mStop.setEnabled(true);
				progress.setProgress(0);
				progress.setVisibility(View.VISIBLE);
				playerStarted = true;
			}
		});
	}

	public void playerPCMFeedBuffer(final boolean isPlaying,
			final int audioBufferSizeMs, final int audioBufferCapacityMs) {

		uiHandler.post(new Runnable() {
			public void run() {
				progress.setProgress(audioBufferSizeMs * progress.getMax()/ audioBufferCapacityMs);
			}
		});
	}

	public void playerStopped(final int perf) {
		uiHandler.post(new Runnable() {
			public void run() {
				enableButtons();
				mStop.setEnabled(false);

				progress.setVisibility(View.INVISIBLE);

				playerStarted = false;
			}
		});
	}

	public void playerException(final Throwable t) {

		if (playerStarted)
			playerStopped(0);
	};

	// //////////////////////////////////////////////////////////////////////////
	// OnClickListener
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Called when a view has been clicked.
	 */
	public void onClick(View v) {
		try {
			
			switch (v.getId()) {

			case R.id.play1:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay1.setEnabled(false);
				mPlay1.setAlpha(128);
				startOne(Decoder.DECODER_FFMPEG_WMA);
				// txtStatus.setText( R.string.text_using_FFmpeg );
				break;

			case R.id.play1xtra:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay1xtra.setEnabled(false);
				mPlay1xtra.setAlpha(128);
				startOnextra(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play2:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay2.setEnabled(false);
				mPlay2.setAlpha(128);
				startTwo(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play3:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay3.setEnabled(false);
				mPlay3.setAlpha(128);
				startThree(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play4:
				pause_before_play();				
				if (checkNetwork() == false) break;
				mPlay4.setEnabled(false);
				mPlay4.setAlpha(128);
				startFour(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play5:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay5.setEnabled(false);
				mPlay5.setAlpha(128);
				startFive(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play4xtra:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay4xtra.setEnabled(false);
				mPlay4xtra.setAlpha(128);
				startFourxtra(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play5xtra:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlay5xtra.setEnabled(false);
				mPlay5xtra.setAlpha(128);
				startFivextra(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.playAsian:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlayAsian.setEnabled(false);
				mPlayAsian.setAlpha(128);
				startAsian(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.playWs:
				pause_before_play();
				if (checkNetwork() == false) break;
				mPlayWs.setEnabled(false);
				mPlayWs.setAlpha(128);
				startWs(Decoder.DECODER_FFMPEG_WMA);
				
				break;

			case R.id.stop:
				mStop.setAlpha(255);
//                try {
//                	Thread.sleep(1000);
//                    //Log.d( LOG, "AACPlayerActivity inside stop sleeping");
//                }
//                catch (Exception e) {
//                }
				pause_before_stop();
				//System.exit(0);
				//enableButtons();
				finish();
				break;
			}
		} catch (Exception e) {
			mStop.setEnabled(true);
        	displayAlert();
			Log.e("AACPlayerActivity", "exc exception when clicked and no network", e);
		}
	}


	// //////////////////////////////////////////////////////////////////////////
	// Protected
	// //////////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mPlay1 = (ImageButton) findViewById(R.id.play1);
		mPlay1xtra = (ImageButton) findViewById(R.id.play1xtra);
		mPlay2 = (ImageButton) findViewById(R.id.play2);
		mPlay3 = (ImageButton) findViewById(R.id.play3);
		mPlay4 = (ImageButton) findViewById(R.id.play4);
		mPlay4xtra = (ImageButton) findViewById(R.id.play4xtra);
		mPlay5 = (ImageButton) findViewById(R.id.play5);
		mPlay5xtra = (ImageButton) findViewById(R.id.play5xtra);
		mPlayAsian = (ImageButton) findViewById(R.id.playAsian);
		mStop = (ImageButton) findViewById(R.id.stop);
		mPlayWs = (ImageButton) findViewById(R.id.playWs);

		progress = (ProgressBar) findViewById(R.id.view_main_progress);

		mPlay1.setOnClickListener(this);
		mPlay1xtra.setOnClickListener(this);
		mPlay2.setOnClickListener(this);
		mPlay3.setOnClickListener(this);
		mPlay4.setOnClickListener(this);
		mPlay4xtra.setOnClickListener(this);
		mPlay5.setOnClickListener(this);
		mPlay5xtra.setOnClickListener(this);
		mPlayAsian.setOnClickListener(this);
		mPlayWs.setOnClickListener(this);

		mStop.setOnClickListener(this);
		enableButtons();
		uiHandler = new Handler();
		



	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stop();
	}

	private void stop() {
		// TODO Auto-generated method stub
		
		
	}

	// //////////////////////////////////////////////////////////////////////////
	// Private
	// //////////////////////////////////////////////////////////////////////////
	private void pause_before_play() {
//		mStop.setAlpha(255);
//		if (mWifiLock.isHeld()) mWifiLock.release();
		// if (aacFileChunkPlayer != null) { aacFileChunkPlayer.stop();
		// aacFileChunkPlayer = null; }
		//aacPlayer.stop();
		//pcmFeed.stop();
		//mmsinputStream.close();
		disableButtons();
		if (aacPlayer != null) {
			aacPlayer.stop();
			aacPlayer = null;
		}
		try {
			Thread.sleep(10);
			Log.d( LOG, "AACPlayerActivity inside PAUSE sleeping");
			}
		catch (Exception e) {
		}
	}

	
	private void pause_before_stop() {
//		mStop.setAlpha(255);
//		if (mWifiLock.isHeld()) mWifiLock.release();
		// if (aacFileChunkPlayer != null) { aacFileChunkPlayer.stop();
		// aacFileChunkPlayer = null; }
		//aacPlayer.stop();
		//pcmFeed.stop();
		//mmsinputStream.close();
		enableButtons();
		try {
			Thread.sleep(10);
				if (aacPlayer.stopped) {
					finish();
				}
			aacPlayer.stop();
			aacPlayer = null;
			mStop.setEnabled(true);

			Log.d( LOG, "AACPlayerActivity inside PAUSE_before_stop sleeping");
			}
		catch (Exception e) {
		}
	}
	
	private void startOne(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);
		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlOne());
	}

	private void startOnextra(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlOnextra());
	}

	private void startTwo(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlTwo());
	}

	private void startThree(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlThree());
	}

	private void startFour(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFour());
	}

	private void startFourxtra(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFourxtra());
	}

	private void startFive(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFive());
	}

	private void startFivextra(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFivextra());
	}

	private void startAsian(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlAsian());
	}

	private void startWs(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlWs());

	}


	private String getUrlFour() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio4/radio4_bb_live_int_eq1_sl0";
		return ret;
	}

	private String getUrlOne() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio1/radio1_bb_live_int_eq1_sl0";
		return ret;
	}

	private String getUrlOnextra() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/1xtra/1xtra_bb_live_int_eq1_sl0";
		return ret;
	}

	private String getUrlTwo() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio2/radio2_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlThree() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio3/radio3_bb_live_int_eq1_sl0";
		return ret;
	}

	private String getUrlFive() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio5/radio5_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlFivextra() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio5/5spxtra_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlFourxtra() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio4/radio4xtra_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlAsian() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/asiannet/asiannet_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlWs() throws Exception {
//		URL url4 = new URL("https://media-translate.googlecode.com/svn/trunk/etc/playlists/radio-bbc-world.xspf");
//		URL below obtained from http://www.bbc.co.uk/worldservice/institutional/2009/03/000000_mobile.shtml
		String ret = "mms://a243.l3944038972.c39440.g.lm.akamaistream.net/D/243/39440/v0001/reflector:38972";
		return ret;
	}

	private void enableButtons() {
		// if ((dfeatures & Decoder.DECODER_FAAD2) != 0) btnFaad2.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) btnFFmpeg.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) mPlay4.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) mPlay1.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) mPlayAsian.setEnabled(
		// true );
		mStop.setAlpha(255);
		mStop.setEnabled(true);

		mPlay1.setEnabled(true);
		mPlay1xtra.setEnabled(true);
		mPlay2.setEnabled(true);
		mPlay3.setEnabled(true);
		mPlay4.setEnabled(true);
		mPlay4xtra.setEnabled(true);
		mPlay5.setEnabled(true);
		mPlay5xtra.setEnabled(true);
		mPlayWs.setEnabled(true);
		mPlayAsian.setEnabled(true);

		mPlay1.setAlpha(255);
		mPlay2.setAlpha(255);
		mPlay3.setAlpha(255);
		mPlay4.setAlpha(255);
		mPlay1xtra.setAlpha(255);
		mPlay4xtra.setAlpha(255);
		mPlay5.setAlpha(255);
		mPlay5xtra.setAlpha(255);
		mPlayWs.setAlpha(255);
		mPlayAsian.setAlpha(255);

	}

	private void disableButtons() {
		mPlay1.setEnabled(false);
		mPlay1xtra.setEnabled(false);
		mPlay2.setEnabled(false);
		mPlay3.setEnabled(false);
		mPlay4.setEnabled(false);
		mPlay4xtra.setEnabled(false);
		mPlay5.setEnabled(false);
		mPlay5xtra.setEnabled(false);
		mPlayWs.setEnabled(false);
		mPlayAsian.setEnabled(false);
		
		mPlay1.setAlpha(255);
		mPlay1xtra.setAlpha(255);
		mPlay2.setAlpha(255);
		mPlay3.setAlpha(255);
		mPlay4.setAlpha(255);
		mPlay4xtra.setAlpha(255);
		mPlay5.setAlpha(255);
		mPlay5xtra.setAlpha(255);
		mPlayWs.setAlpha(255);
		mPlayAsian.setAlpha(255);
	}

	
	
	private boolean checkNetwork() throws Exception {
		URL url4 = new URL("http://bbc.net.uk");
		HttpURLConnection urlc = (HttpURLConnection) url4.openConnection();
		urlc.setRequestProperty("Connection", "close");
	    urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
        urlc.connect();
        if (urlc.getResponseCode() == 200) {
        	return true;
        } else {
        	displayAlert();
        	return false;
        }
	}
	
}
