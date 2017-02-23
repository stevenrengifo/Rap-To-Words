package com.stevenrengifo.cypher;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class TrackSeekBar extends SeekBar implements Runnable, SeekBar.OnSeekBarChangeListener{

    private static final int MILLIS_IN_SECOND = 1000;

    private TrackPlayer trackPlayer;
    private Handler handler = new Handler();

    public TrackSeekBar(Context context) {
        super(context);
        this.setOnSeekBarChangeListener(this);
        trackPlayer = TrackPlayer.getInstance();
    }

    public TrackSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnSeekBarChangeListener(this);
        trackPlayer = TrackPlayer.getInstance();
    }

    public TrackSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnSeekBarChangeListener(this);
        trackPlayer = TrackPlayer.getInstance();
    }

    @Override
    public void run() {
        if(trackPlayer != null){
            int currentPosition = trackPlayer.getCurrentPosition() / MILLIS_IN_SECOND;
            this.setProgress(currentPosition);

            removeCallbacks();
            handler.postDelayed(this, MILLIS_IN_SECOND);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (trackPlayer != null && fromUser) {
            trackPlayer.seekTo(progress * MILLIS_IN_SECOND);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setUpTrackSeekBar(){
        this.setMax(trackPlayer.getDuration() / MILLIS_IN_SECOND);
        handler.post(this);
    }

    public void resetTrackSeekBar() {
        this.setProgress(0);
        removeCallbacks();
    }

    public void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

}
