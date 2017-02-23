package com.stevenrengifo.cypher;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface Soundcloud {

    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getRecentTracks();

    @GET("/tracks?client_id=" + Config.CLIENT_ID + "&limit=10")
    Call<List<Track>> getInstrumentals(@Query("q") String query, @Query("offset") int offset);

}