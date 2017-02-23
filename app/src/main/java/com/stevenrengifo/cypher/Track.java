package com.stevenrengifo.cypher;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Track implements Parent<Object> {

    private List<Object> childList = Collections.singletonList(new Object());

    @SerializedName("title")
    private String trackTitle;

    @SerializedName("id")
    private int trackId;

    @SerializedName("user")
    private User trackUser;

    @SerializedName("stream_url")
    private String trackStreamURL;

    @SerializedName("artwork_url")
    private String trackArtworkURL;

    @SerializedName("duration")
    private int trackDuration;

    public String getTitle() {
        return trackTitle;
    }

    public int getId() {
        return trackId;
    }

    public User getTrackUser() {
        return trackUser;
    }

    public String getStreamURL() {
        return trackStreamURL;
    }

    public String getArtworkURL() {
        return trackArtworkURL;
    }

    public int getTrackDuration() {
        return trackDuration;
    }

    @Override
    public List<Object> getChildList() {
        return childList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
