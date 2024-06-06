package com.mrbreaknfix;

import com.google.gson.annotations.SerializedName;

import java.util.TreeMap;

// we can use LinkedHashMap for order by insertion
// we can use TreeMap for order by key (alphabetical)
// we can use HashMap for no order
public class Settings {
    // your discord token
    @SerializedName("token")
    public String token = null;
    // weather or not to have messages and stuff in chat
    @SerializedName("enabled")
    public boolean enabled = false;
    // chanelid channel name map
    @SerializedName("listeningChannels")
    public TreeMap<String, String> listeningChannels = new TreeMap<>();
    // sendingchanelid channel name map
    @SerializedName("sendingChannels")
    public TreeMap<String, String> sendingChannels = new TreeMap<>();
    // channel name cache
    @SerializedName("channelNameCache")
    public TreeMap<String, String> channelNamesCache = new TreeMap<>();
    // serverName cache
    @SerializedName("serverNameCache")
    public TreeMap<String, String> serverNamesCache = new TreeMap<>();
}
