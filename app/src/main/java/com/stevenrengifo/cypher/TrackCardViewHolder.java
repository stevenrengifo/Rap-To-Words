package com.stevenrengifo.cypher;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TrackCardViewHolder extends ParentViewHolder {

    public final ImageView trackImage;
    public final ImageView playStopImage;
    public final ProgressBar loadSpinner;
    public final TextView trackTitle;
    public final TextView trackArtist;
    public final TextView trackDuration;
    public final LinearLayout trackLayout;
    public final View divider;

    public TrackCardViewHolder(View itemView) {
        super(itemView);

        trackLayout = (LinearLayout) itemView.findViewById(R.id.track_card_view);
        trackImage = (ImageView) itemView.findViewById(R.id.track_image);
        playStopImage = (ImageView) itemView.findViewById(R.id.play_stop_image);
        trackTitle = (TextView) itemView.findViewById(R.id.track_title);
        trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
        trackDuration = (TextView) itemView.findViewById(R.id.track_duration);
        loadSpinner = (ProgressBar) itemView.findViewById(R.id.load_spinner);
        divider = itemView.findViewById(R.id.adapter_divider_top);
    }

    public void toggleExpansion() {
        if(!isExpanded()) {
            expandView();
            divider.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }
}
