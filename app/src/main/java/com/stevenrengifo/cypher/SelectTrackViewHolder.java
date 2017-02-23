package com.stevenrengifo.cypher;

import android.view.View;
import android.widget.Button;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SelectTrackViewHolder extends ChildViewHolder {

    public final Button selectTrackButton;

    public SelectTrackViewHolder(View itemView) {
        super(itemView);
        selectTrackButton = (Button) itemView.findViewById(R.id.select_track_button);
    }

}
