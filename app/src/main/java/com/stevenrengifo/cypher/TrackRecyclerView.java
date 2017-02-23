package com.stevenrengifo.cypher;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrackRecyclerView extends ExpandableRecyclerAdapter<Track, Object, TrackCardViewHolder, SelectTrackViewHolder> {

    public static final int BACK_BUTTON_REQUEST = 1;

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final TrackPlayer trackPlayer;
    private int previousParentPosition;
    private int currentParentPosition;
    private SelectTrackViewHolder previousChild;
    private boolean isTrackLoaded;
    private ArrayList<TrackCardViewHolder> viewHolderArrayList = new ArrayList<>();

    public TrackRecyclerView(Context context, @NonNull List<Track> parentList) {
        super(parentList);

        this.context = context;

        layoutInflater = LayoutInflater.from(context);

        trackPlayer = TrackPlayer.getInstance();
        trackPlayer.setupMediaPlayer();
    }

    @NonNull
    @Override
    public TrackCardViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.track_card, parentViewGroup, false);
        return new TrackCardViewHolder(view);
    }

    @NonNull
    @Override
    public SelectTrackViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.select_track_button, childViewGroup, false);
        return new SelectTrackViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(@NonNull final TrackCardViewHolder parentViewHolder, final int parentPosition, @NonNull final Track parent) {

        if (viewHolderArrayList.size() != getParentList().size()) {
            viewHolderArrayList.add(parentPosition, parentViewHolder);
        } else {
            viewHolderArrayList.set(parentPosition, parentViewHolder);
        }

        if (trackPlayer.isCurrentTrack(parent) && trackPlayer.isPlaying()) {
            parentViewHolder.playStopImage.setVisibility(View.VISIBLE);
            parentViewHolder.playStopImage.setImageResource(android.R.drawable.ic_media_pause);
        } else if (trackPlayer.isCurrentTrack(parent) && !trackPlayer.isPlaying() && isTrackLoaded) {
            parentViewHolder.playStopImage.setVisibility(View.VISIBLE);
            parentViewHolder.playStopImage.setImageResource(android.R.drawable.ic_media_play);
        } else {
            parentViewHolder.playStopImage.setVisibility(View.GONE);
        }

        parentViewHolder.trackTitle.setText(parent.getTitle());
        parentViewHolder.trackArtist.setText(parent.getTrackUser().getUserName());
        parentViewHolder.trackDuration.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(parent.getTrackDuration()),
                TimeUnit.MILLISECONDS.toSeconds(parent.getTrackDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(parent.getTrackDuration()))
        ));

        parentViewHolder.trackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentParentPosition = parentPosition;

                if (trackPlayer.isCurrentTrack(parent)) {
                    trackPlayer.togglePlayPause();
                    if (isTrackLoaded && trackPlayer.isPlaying() && previousParentPosition == parentPosition) {
                        parentViewHolder.playStopImage.setImageResource(android.R.drawable.ic_media_pause);
                    } else if (isTrackLoaded && !trackPlayer.isPlaying() && previousParentPosition == parentPosition) {
                        parentViewHolder.playStopImage.setImageResource(android.R.drawable.ic_media_play);
                    }
                } else {
                    collapseAllParents();
                    trackPlayer.setMediaPlayerDatasource(parent);
                    parentViewHolder.loadSpinner.setVisibility(View.VISIBLE);
                    setPreviousTrackView();
                }

                previousParentPosition = parentPosition;
                parentViewHolder.toggleExpansion();
            }
        });
    }

    @Override
    public void onBindChildViewHolder(@NonNull SelectTrackViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Object child) {

        if (previousChild != childViewHolder) {
            childViewHolder.selectTrackButton.setBackgroundColor(0xFF808080);
        }

        childViewHolder.selectTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    Intent intent = new Intent(context, TrackActivity.class);
                    ((MainActivity) context).startActivityForResult(intent, BACK_BUTTON_REQUEST);
                }
            }
        });

        previousChild = childViewHolder;
    }

    private void setPreviousTrackView() {
        if (currentParentPosition != previousParentPosition && previousParentPosition < viewHolderArrayList.size()) {
            viewHolderArrayList.get(previousParentPosition).divider.setVisibility(View.VISIBLE);
            viewHolderArrayList.get(previousParentPosition).playStopImage.setVisibility(View.GONE);
            viewHolderArrayList.get(previousParentPosition).loadSpinner.setVisibility(View.GONE);
        }

        if (previousChild != null) {
            previousChild.selectTrackButton.setEnabled(false);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TrackLoadedEvent event) {
        isTrackLoaded = true;

        if (currentParentPosition == previousParentPosition) {
            viewHolderArrayList.get(currentParentPosition).loadSpinner.setVisibility(View.GONE);
            viewHolderArrayList.get(currentParentPosition).playStopImage.setVisibility(View.VISIBLE);
            viewHolderArrayList.get(currentParentPosition).playStopImage.setImageResource(android.R.drawable.ic_media_pause);
        }

        if (previousChild != null) {
            previousChild.selectTrackButton.setEnabled(true);
            previousChild.selectTrackButton.setBackgroundColor(0xFFb38f00);
        }
    }

    @Override
    public void notifyParentDataSetChanged(boolean preserveExpansionState) {
        super.notifyParentDataSetChanged(preserveExpansionState);
        viewHolderArrayList.clear();
    }

    public void onActivityResult() {
        if (viewHolderArrayList.size() != 0) {
            viewHolderArrayList.get(currentParentPosition).divider.setVisibility(View.VISIBLE);
            viewHolderArrayList.get(currentParentPosition).playStopImage.setVisibility(View.GONE);
        }

        if (previousChild != null) {
            previousChild.selectTrackButton.setEnabled(false);
            previousChild.selectTrackButton.setBackgroundColor(0xFF808080);
        }
    }

    public void onResume() {
        EventBus.getDefault().register(this);
    }

    public void onPause() {
        EventBus.getDefault().unregister(this);
    }
}
