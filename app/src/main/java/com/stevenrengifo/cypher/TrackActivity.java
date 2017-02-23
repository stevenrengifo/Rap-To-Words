package com.stevenrengifo.cypher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TrackActivity extends Activity {

    private static final String EXTRA_TRACK = "EXTRA_TRACK";
    private static final String TAG = "TrackActivity";

    private static final TrackPlayer trackPlayer = TrackPlayer.getInstance();
    private final Track currentTrack = trackPlayer.getCurrentTrack();

    private Handler handler = new Handler();
    private Runnable randomWordRunnable;

    private TextView rapWord;
    private TextView seekBarTextView;
    private SeekBar seekBar;
    private TrackSeekBar trackSeekBar;
    private String[] rapWordsArray;
    private ImageView trackImage;
    private ImageView playStopImage;
    private ProgressBar loadSpinner;
    private TextView trackTitle;
    private TextView trackArtist;
    private TextView trackDuration;
    private LinearLayout trackLayout;

    private int rapWordDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        initializeViews();

        setUpTrackCard();

        setUpWordSeekBar();

        setUpRandomWordRunnable();

        setUpRapWordsIntroCountdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackSeekBar.setUpTrackSeekBar();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        trackSeekBar.removeCallbacks();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_acknowledgements:
                Intent intent = new Intent(this, AcknowledgementActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void initializeViews() {
        rapWord = (TextView) findViewById(R.id.rap_word);
        seekBarTextView = (TextView) findViewById(R.id.seek_bar_text_view);
        seekBar = (SeekBar) findViewById(R.id.seconds_per_word_seek_bar);
        trackSeekBar = (TrackSeekBar) findViewById(R.id.track_seek_bar);
        rapWordsArray = getResources().getStringArray(R.array.rap_words);
        String multipleSecondsText = getResources().getString(R.string.multiple_seconds_per_word_text);
        seekBarTextView.setText(String.format(multipleSecondsText, 5));

        trackLayout = (LinearLayout) findViewById(R.id.track_card_view);
        trackImage = (ImageView) findViewById(R.id.track_image);
        playStopImage = (ImageView) findViewById(R.id.play_stop_image);
        trackTitle = (TextView) findViewById(R.id.track_title);
        trackArtist = (TextView) findViewById(R.id.track_artist);
        trackDuration = (TextView) findViewById(R.id.track_duration);
        loadSpinner = (ProgressBar) findViewById(R.id.load_spinner);
    }

    private void setUpTrackCard() {
        trackTitle.setText(currentTrack.getTitle());
        trackArtist.setText(currentTrack.getTrackUser().getUserName());
        playStopImage.setImageResource(android.R.drawable.ic_media_pause);
        trackDuration.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentTrack.getTrackDuration()),
                TimeUnit.MILLISECONDS.toSeconds(currentTrack.getTrackDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTrack.getTrackDuration()))
        ));

        trackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackPlayer.isCurrentTrack(currentTrack)) {
                    trackPlayer.togglePlayPause();
                    if (trackPlayer.isPlaying()) {
                        playStopImage.setImageResource(android.R.drawable.ic_media_pause);
                    } else if (!trackPlayer.isPlaying()) {
                        playStopImage.setImageResource(android.R.drawable.ic_media_play);
                    }
                } else {
                    playStopImage.setVisibility(View.GONE);
                    loadSpinner.setVisibility(View.VISIBLE);
                    trackPlayer.setMediaPlayerDatasource(currentTrack);
                }
            }
        });
    }

    private void setUpWordSeekBar() {
        final String multipleSecondsText = getResources().getString(R.string.multiple_seconds_per_word_text);
        seekBar.setMax(10);
        seekBar.setProgress(5);
        seekBar.setEnabled(false);
        rapWordDelay = seekBar.getProgress() * 1000;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    seekBarTextView.setText(R.string.no_words_text);
                    rapWord.setVisibility(View.GONE);
                } else if (progress == 1) {
                    seekBarTextView.setText(R.string.one_second_per_word_text);
                    rapWord.setVisibility(View.VISIBLE);
                } else {
                    seekBarTextView.setText(String.format(multipleSecondsText, progress));
                    rapWord.setVisibility(View.VISIBLE);
                }

                if (rapWord.getVisibility() == View.VISIBLE) {
                    rapWordDelay = progress * 1000;
                    handler.post(setUpRandomWordRunnable());
                } else {
                    handler.removeCallbacks(randomWordRunnable);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private Runnable setUpRandomWordRunnable() {
        if (randomWordRunnable != null) {
            handler.removeCallbacks(randomWordRunnable);
        }
        randomWordRunnable = new Runnable() {
            @Override
            public void run() {
                Random randomGenerator = new Random();
                int num = randomGenerator.nextInt(rapWordsArray.length);

                String currentRapWord  = rapWordsArray[num];
                currentRapWord = currentRapWord.substring(0,1).toUpperCase() + currentRapWord.substring(1).toLowerCase();
                rapWord.setText(currentRapWord);

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(this, rapWordDelay);
            }
        };
        return randomWordRunnable;
    }

    private void setUpRapWordsIntroCountdown() {
        new CountDownTimer(9000, 1500) {

            public void onTick(long millisUntilFinished) {
                long currentSecond = millisUntilFinished / 1500;
                if (currentSecond <= 3L) {
                    rapWord.setText(Long.toString(currentSecond));
                }
            }

            public void onFinish() {
                handler.post(setUpRandomWordRunnable());
                seekBar.setEnabled(true);
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TrackFinishedEvent event) {
        trackSeekBar.resetTrackSeekBar();
        playStopImage.setImageResource(android.R.drawable.ic_media_play);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TrackLoadedEvent event) {
        playStopImage.setVisibility(View.VISIBLE);
        playStopImage.setImageResource(android.R.drawable.ic_media_pause);
        loadSpinner.setVisibility(View.GONE);
        trackSeekBar.setUpTrackSeekBar();
    }

}
