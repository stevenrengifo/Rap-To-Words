package com.stevenrengifo.cypher;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class ApiServices {

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Config.API_URL)
            .build();

    private static final Soundcloud SOUNDCLOUD = RETROFIT.create(Soundcloud.class);

    public static Soundcloud getSoundcloud() {
        return SOUNDCLOUD;
    }

}
