package com.stevenrengifo.cypher;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private List<Track> trackList;
    private TrackRecyclerView expandableRecyclerViewAdapter;
    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private ProgressBar loadSpinner;
    private TextView errorMessage;
    private TrackPlayer trackPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackList = new ArrayList<>();
        trackPlayer = TrackPlayer.getInstance();
        trackPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        loadSpinner = (ProgressBar) findViewById(R.id.load_spinner);
        errorMessage = (TextView) findViewById(R.id.error_message);

        loadTracks();

        final RecyclerView trackRecyclerView = (RecyclerView) findViewById(R.id.track_recycler_view);
        expandableRecyclerViewAdapter = new TrackRecyclerView(this, trackList);
        trackRecyclerView.setAdapter(expandableRecyclerViewAdapter);
        trackRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ptrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_frame);

        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !trackRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadTracks();
                if (trackPlayer.isPlaying()) {
                    trackPlayer.togglePlayPause();
                }
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        trackPlayer.destroyMediaPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        expandableRecyclerViewAdapter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        expandableRecyclerViewAdapter.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TrackRecyclerView.BACK_BUTTON_REQUEST) {
            if (resultCode == RESULT_OK) {
                trackPlayer.resetMediaPlayer();
                expandableRecyclerViewAdapter.onActivityResult();
                expandableRecyclerViewAdapter.collapseAllParents();
            }
        }
    }

    private void loadTracks() {
        Log.d(TAG, "Loading tracks................");
        Soundcloud soundcloud = ApiServices.getSoundcloud();
        Call<List<Track>> call = soundcloud.getInstrumentals("rap, beats", randomNumber());
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Response<List<Track>> response, Retrofit retrofit) {

                if (loadSpinner.getVisibility() == View.VISIBLE) {
                    loadSpinner.setVisibility(View.GONE);
                }

                if (errorMessage.getVisibility() == View.VISIBLE) {
                    errorMessage.setVisibility(View.GONE);
                }

                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.refreshComplete();
                }

                if (response.isSuccess()) {
                    Log.d(TAG, "Successful track request");
                    trackList.clear();
                    trackList.addAll(response.body());
                    expandableRecyclerViewAdapter.notifyParentDataSetChanged(false);
                } else {
                    if (trackList.isEmpty()) {
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG, "Error: " + Integer.toString(statusCode) + " " + errorBody.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {

                if (loadSpinner.getVisibility() == View.VISIBLE) {
                    loadSpinner.setVisibility(View.GONE);
                }

                if (trackList.isEmpty() && errorMessage.getVisibility() != View.VISIBLE) {
                    errorMessage.setVisibility(View.VISIBLE);
                }

                if (errorMessage.getVisibility() == View.GONE) {
                    Toast.makeText(getApplicationContext(), R.string.load_error, Toast.LENGTH_SHORT).show();
                }

                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.refreshComplete();
                }
                Log.d(TAG, "Throwable: " + t.toString());
            }

        });
    }

    private int randomNumber() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(150);
    }
}
