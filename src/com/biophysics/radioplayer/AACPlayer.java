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

//import android.util.Log;

import java.io.InputStream;

import java.net.URLConnection;

import android.util.Log;


/**
 * This is the AACPlayer parent class.
 * It uses Decoder to decode AAC stream into PCM samples.
 * This class is not thread safe.
 */
public abstract class AACPlayer {

    /**
     * The default expected bitrate.
     * Used only if not specified in play() methods.
     */
    public static final int DEFAULT_EXPECTED_KBITSEC_RATE = 48;


    /**
     * The default capacity of the audio buffer (AudioTrack) in ms.
     * @see setAudioBufferCapacityMs(int)
     * Default = 1500
     */
    public static final int DEFAULT_AUDIO_BUFFER_CAPACITY_MS = 1500;


    /**
     * The default capacity of the output buffer used for decoding in ms.
     * @see setDecodeBufferCapacityMs(int)
     */
    public static final int DEFAULT_DECODE_BUFFER_CAPACITY_MS = 700;


    private static final String LOG = "AACPlayer";


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    protected boolean stopped;

    protected int audioBufferCapacityMs;
    protected int decodeBufferCapacityMs;
    protected PlayerCallback playerCallback;

    // variables used for computing average bitrate
    private int sumKBitSecRate = 0;
    private int countKBitSecRate = 0;
    private int avgKBitSecRate = 0;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new player.
     */
    protected AACPlayer() {
        this( null );
    }


    /**
     * Creates a new player.
     * @param playerCallback the callback, can be null
     */
    protected AACPlayer( PlayerCallback playerCallback ) {
        this( playerCallback, DEFAULT_AUDIO_BUFFER_CAPACITY_MS, DEFAULT_DECODE_BUFFER_CAPACITY_MS );
    }


    /**
     * Creates a new player.
     * @param playerCallback the callback, can be null
     * @param audioBufferCapacityMs the capacity of the audio buffer (AudioTrack) in ms
     * @param decodeBufferCapacityMs the capacity of the buffer used for decoding in ms
     * @see setAudioBufferCapacityMs(int)
     * @see setDecodeBufferCapacityMs(int)
     */
    protected AACPlayer( PlayerCallback playerCallback, int audioBufferCapacityMs, int decodeBufferCapacityMs ) {
        setPlayerCallback( playerCallback );
        // Karthik change buffer here - does not make any difference
        setAudioBufferCapacityMs( audioBufferCapacityMs );
        setDecodeBufferCapacityMs( decodeBufferCapacityMs );
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the audio buffer (AudioTrack) capacity.
     * The capacity can be expressed in time of audio playing of such buffer.
     * For example 1 second buffer capacity is 88100 samples for 44kHz stereo.
     * By setting this the audio will start playing after the audio buffer is first filled.
     *
     * NOTE: this should be set BEFORE any of the play methods are called.
     *
     * @param audioBufferCapacityMs the capacity of the buffer in milliseconds
     */
    public void setAudioBufferCapacityMs( int audioBufferCapacityMs ) {
        this.audioBufferCapacityMs = audioBufferCapacityMs;
    }


    /**
     * Gets the audio buffer capacity as the audio playing time.
     * @return the capacity of the audio buffer in milliseconds
     */
    public int getAudioBufferCapacityMs() {
        return audioBufferCapacityMs;
    }


    /**
     * Sets the capacity of the output buffer used for decoding.
     * The capacity can be expressed in time of audio playing of such buffer.
     * For example 1 second buffer capacity is 88100 samples for 44kHz stereo.
     * Decoder tries to fill out the whole buffer in each round.
     *
     * NOTE: this should be set BEFORE any of the play methods are called.
     *
     * @param decodeBufferCapacityMs the capacity of the buffer in milliseconds
     */
    public void setDecodeBufferCapacityMs( int decodeBufferCapacityMs ) {
        this.decodeBufferCapacityMs = decodeBufferCapacityMs;
    }


    /**
     * Gets the capacity of the output buffer used for decoding as the audio playing time.
     * @return the capacity of the decoding buffer in milliseconds
     */
    public int getDecodeBufferCapacityMs() {
        return decodeBufferCapacityMs;
    }


    /**
     * Sets the PlayerCallback.
     * NOTE: this should be set BEFORE any of the play methods are called.
     */
    public void setPlayerCallback( PlayerCallback playerCallback ) {
        this.playerCallback = playerCallback;
    }


    /**
     * Returns the PlayerCallback or null if no PlayerCallback was set.
     */
    public PlayerCallback getPlayerCallback() {
        return playerCallback;
    }


    /**
     * Plays a stream asynchronously.
     * This method starts a new thread.
     * @param url the URL of the stream or file
     */
    public void playAsync( final String url ) {
    	playAsync( url, -1 );
    }


    /**
     * Plays a stream asynchronously.
     * This method starts a new thread.
     * @param url the URL of the stream or file
     * @param expectedKBitSecRate the expected average bitrate in kbit/sec; -1 means unknown
     */
    public void playAsync( final String url, final int expectedKBitSecRate ) {
        new Thread(new Runnable() {
            public void run() {
                try {
                	Thread.sleep(1000);
                    //Log.d( LOG, "AACPlayer: Just before playing  " + url);
                    play( url, expectedKBitSecRate );
                }
                catch (Exception e) {
                    //Log.e( LOG, "playAsync():", e);

                    if (playerCallback != null) playerCallback.playerException( e );
                }
            }
        }).start();
    }



    /**
     * Plays a stream synchronously.
     * @param url the URL of the stream or file
     * @param expectedKBitSecRate the expected average bitrate in kbit/sec; -1 means unknown
     */
    public void play( String url, int expectedKBitSecRate ) throws Exception {
        if (url.startsWith( "mms://" )) {
            play( new MMSInputStream( url ), expectedKBitSecRate );
        }
    }

    
    
    
 


    /**
     * Plays a stream synchronously.
     * @param is the input stream
     * @param expectedKBitSecRate the expected average bitrate in kbit/sec; -1 means unknown
     */
    public final void play( InputStream is, int expectedKBitSecRate ) throws Exception {
        stopped = false;
        //Log.d( LOG, "AACPlayer: expectedKBitSecRate " + expectedKBitSecRate);

        if (playerCallback != null) playerCallback.playerStarted();

        if (expectedKBitSecRate <= 0) expectedKBitSecRate = DEFAULT_EXPECTED_KBITSEC_RATE;

        sumKBitSecRate = 0;
        countKBitSecRate = 0;
        Log.d( LOG, "AACPlayer: expectedKBitSecRate " + expectedKBitSecRate);

        playImpl( is, expectedKBitSecRate );

    }


    /**
     * Stops the execution thread.
     */
    synchronized public void stop() {
        stopped = true;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Protected
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Plays a stream synchronously.
     * This is the implementation method calle by every play() and playAsync() methods.
     * @param is the input stream
     * @param expectedKBitSecRate the expected average bitrate in kbit/sec
     */
    protected abstract void playImpl( InputStream is, int expectedKBitSecRate ) throws Exception;
/*
    protected void dumpHeaders( URLConnection cn ) {
        for (java.util.Map.Entry<String, java.util.List<String>> me : cn.getHeaderFields().entrySet()) {
            for (String s : me.getValue()) {
                Log.d( LOG, "header: key=" + me.getKey() + ", val=" + s);
                
            }
        }
    }*/


    protected int computeAvgKBitSecRate( Decoder.Info info ) {
        // do not change the value after a while - avoid changing of the out buffer:
        if (countKBitSecRate < 64) {
            int kBitSecRate = computeKBitSecRate( info );
            int frames = info.getRoundFrames();

            sumKBitSecRate += kBitSecRate * frames;
            countKBitSecRate += frames;
            avgKBitSecRate = sumKBitSecRate / countKBitSecRate;
        }

        return avgKBitSecRate;
    }


    protected static int computeKBitSecRate( Decoder.Info info ) {
        if (info.getRoundSamples() <= 0) return -1;

        return computeKBitSecRate( info.getRoundBytesConsumed(), info.getRoundSamples(),
                                   info.getSampleRate(), info.getChannels());
    }


    protected static int computeKBitSecRate( int bytesconsumed, int samples, int sampleRate, int channels ) {
        long ret = 8L * bytesconsumed * channels * sampleRate / samples;

        return (((int)ret) + 500) / 1000;
    }


    protected static int computeInputBufferSize( int kbitSec, int durationMs ) {
        return kbitSec * durationMs / 8;
    }


    protected static int computeInputBufferSize( Decoder.Info info, int durationMs ) {

        return computeInputBufferSize( info.getRoundBytesConsumed(), info.getRoundSamples(),
                                        info.getSampleRate(), info.getChannels(), durationMs );
    }


    protected static int computeInputBufferSize( int bytesconsumed, int samples,
                                                 int sampleRate, int channels, int durationMs ) {

        return (int)(((long) bytesconsumed) * channels * sampleRate * durationMs  / (1000L * samples));
    }

}
