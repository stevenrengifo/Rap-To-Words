package com.stevenrengifo.cypher;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int userId;

    @SerializedName("permalink")
    private String userPermalink;

    @SerializedName("username")
    private String userName;

    @SerializedName("uri")
    private String userUri;

    @SerializedName("permalink_url")
    private String userPermalinkUrl;

    @SerializedName("avatar_url")
    private String userAvatarUrl;


    public int getUserId() {
        return userId;
    }

    public String getUserPermalink() {
        return userPermalink;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUri() {
        return userUri;
    }

    public String getUserPermalinkUrl() {
        return userPermalinkUrl;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }
}
