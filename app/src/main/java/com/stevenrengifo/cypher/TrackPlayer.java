package com.stevenrengifo.cypher;

import android.media.MediaPlayer;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class TrackPlayer extends MediaPlayer {

    private static TrackPlayer trackPlayer;
    private Track currentTrack;

    private TrackPlayer() {
        //prevent instantiation
    }

    public static TrackPlayer getInstance() {
        if (trackPlayer == null) {
            trackPlayer = new TrackPlayer();
        }
        return trackPlayer;
    }

    public void setupMediaPlayer() {
        trackPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                EventBus.getDefault().post(new TrackLoadedEvent());
                togglePlayPause();
            }
        });
        trackPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                EventBus.getDefault().post(new TrackFinishedEvent());
                currentTrack = null;
                mp.reset();
            }
        });
    }

    public void resetMediaPlayer() {
        currentTrack = null;
        trackPlayer.reset();
    }

    public void togglePlayPause() {
        if (trackPlayer.isPlaying()) {
            trackPlayer.pause();
        } else {
            trackPlayer.start();
        }
    }

    public void playRecordedTrack(String trackFileName) throws IOException {
        trackPlayer.stop();
        trackPlayer.reset();

        try {
            trackPlayer.setDataSource(trackFileName);
            trackPlayer.prepareAsync();
            trackPlayer.start();
        } catch (IllegalStateException e) {

            Log.wtf("TrackPlayer: ", e.toString());
        }
    }

    public void setMediaPlayerDatasource(Track track) {
        if (trackPlayer.isPlaying()) {
            trackPlayer.stop();
            trackPlayer.reset();
        }

        try {
            trackPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
            currentTrack = track;
            trackPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            trackPlayer.reset();
            currentTrack = null;
            setMediaPlayerDatasource(track);
            Log.wtf("TrackPlayer: ", e.toString());
        }
    }

    public void destroyMediaPlayer() {
        if (trackPlayer != null) {
            if (trackPlayer.isPlaying()) {
                trackPlayer.stop();
            }
            trackPlayer.release();
            trackPlayer = null;
        }
    }

    public boolean isCurrentTrack(Track track) {
        return currentTrack != null && currentTrack.equals(track);
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void configureSeekBar() {

    }
}
