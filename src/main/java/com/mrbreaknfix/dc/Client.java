package com.mrbreaknfix.dc;

import com.mrbreaknfix.event.Sub;
import com.mrbreaknfix.event.events.DiscordMessageReceived;
import com.mrbreaknfix.utils.NamingUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

import static com.mrbreaknfix.DiscordMC.*;

public class Client {
    public static DC listener = new DC();


    public static void sendText(String chatText) {
        new Thread(() -> {
            try {
                sendTextSync(chatText);
            } catch (IOException e) {
                LOGGER.error("Error sending message", e);
            }
        }).start();
    }

    public static void sendTextSync(String chatText) throws IOException {
        Map.Entry<String, String> firstEntry = settings.sendingChannels.firstEntry();
        String channelId = firstEntry.getKey();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"content\": \"" + chatText + "\"}");
        Request request = new Request.Builder()
                .url("https://discord.com/api/v9/channels/" + channelId + "/messages")
                .method("POST", body)
                .addHeader("authorization", settings.token)
                .addHeader("content-type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if (response.body() != null) {
            LOGGER.info("Response: " + response.body().string());
            LOGGER.info("Request: ChatText:" + chatText + " ChannelId:" + channelId);
        } else {
            LOGGER.warn("Response is null");
        }
    }

    public static void start() {
        listener.connect();
    }
    public static void stop() {
        listener.shutDown();
    }

    @Sub
    public void onDiscordMessageReceived(DiscordMessageReceived event) {
        if (mc.player == null) {
            return;
        }

        String channelId = event.getChannelId();
        if (!settings.listeningChannels.containsKey(channelId)) {
            return;
        }
        String channel = settings.channelNamesCache.getOrDefault(channelId, channelId);

        MutableText name = Text.literal("<" + event.getAuthor() + ">").formatted(Formatting.GRAY);
        if (true) {

            String fancyname = channel.toUpperCase().replace("A", "ᴀ").replace("B", "ʙ").replace("C", "ᴄ").replace("D", "ᴅ").replace("E", "ᴇ").replace("F", "ғ").replace("G", "ɢ").replace("H",
                    "ʜ").replace("I", "ɪ").replace("J", "ᴊ").replace("K", "ᴋ").replace("L", "ʟ").replace("M", "ᴍ").replace("N", "ɴ").replace(
                    "O", "ᴏ").replace("P", "ᴘ").replace("Q", "ǫ").replace("R",
                    "ʀ").replace("S", "s").replace("T", "ᴛ").replace("U", "ᴜ").replace("V", "ᴠ").replace("W", "ᴡ").replace("X", "").replace("Y", "ʏ").replace("Z", "ᴢ").replace("1", "𝟷").replace("2", "𝟸").replace("3", "𝟹").replace("4", "𝟺").replace("5", "𝟻").replace("6", "𝟼").replace("7", "𝟽").replace("8", "𝟾").replace("9", "𝟿").replace("0", "𝟶");

            name = Text.literal("#" + fancyname).formatted(Formatting.DARK_AQUA).append(Text.literal(" <" + event.getAuthor() + ">").formatted(Formatting.GRAY));
        }
        MutableText text = Text.literal(" " + event.getMessage()).formatted(Formatting.WHITE);

//        if (event.getAuthor().getName().equalsIgnoreCase(mc.player.getName().getString())) {
//            name = Text.literal("<" + event.getAuthor().getName() + ">").setStyle(Style.EMPTY.withColor(0x5865F2));
//        }
        MutableText finalText = name.append(text);

        mc.player.sendMessage(finalText);
    }

}
