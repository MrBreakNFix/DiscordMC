package com.mrbreaknfix.dc.rest;

import okhttp3.*;

import java.io.IOException;
import java.util.TreeMap;

import static com.mrbreaknfix.DiscordMC.settings;

public class guilds {
    public TreeMap<String, String> getGuilds() throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://discord.com/api/v9/users/@me/guilds")
                .method("GET", body)
                .addHeader("authorization", settings.token)
                .build();
        Response response = client.newCall(request).execute();


        // Extract id and name
        TreeMap<String, String> guilds = new TreeMap<>();
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            String responseString = responseBody.string();
            String[] guildsArray = responseString.split("},");
            for (String guild : guildsArray) {
                String[] guildParts = guild.split(",");
                String id = guildParts[0].split(":")[1];
                String name = guildParts[1].split(":")[1];
                guilds.put(id, name);
            }
        }
        return guilds;
    }
}
