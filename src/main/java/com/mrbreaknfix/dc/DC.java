package com.mrbreaknfix.dc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mrbreaknfix.event.events.DiscordMessageReceived;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.util.Timer;
import java.util.TimerTask;

import static com.mrbreaknfix.DiscordMC.EventBus;
import static com.mrbreaknfix.DiscordMC.settings;

public class DC {
    private static final String GATEWAY_URL = "wss://gateway.discord.gg/?v=10&encording=json";
    private WebSocketClient webSocketClient;


    void connect() {
        webSocketClient = new WebSocketClient(java.net.URI.create(GATEWAY_URL)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket connection opened.");
                authenticate();
            }

            @Override
            public void onMessage(String message) {
                handleMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket error: " + ex.getMessage());
            }
        };

        webSocketClient.connect();
    }

    public void shutDown() {
        webSocketClient.close();
    }

    private void authenticate() {
        JsonObject authPayload = new JsonObject();
        authPayload.addProperty("op", 2);
        JsonObject data = new JsonObject();
        data.addProperty("token", settings.token);
        JsonObject properties = new JsonObject();
        properties.addProperty("$os", "windows");
        properties.addProperty("$browser", "chrome");
        properties.addProperty("$device", "pc");
        data.add("properties", properties);
        authPayload.add("d", data);
        sendMessage(authPayload.toString());
    }

    private void handleMessage(String message) {
        Gson gson = new Gson();
        JsonObject event = gson.fromJson(message, JsonObject.class);

        int opCode = event.get("op").getAsInt();
        if (opCode == 10) {
            int heartbeatInterval = event.getAsJsonObject("d").get("heartbeat_interval").getAsInt();
            startHeartbeat(heartbeatInterval / 1000);
        } else if (opCode == 0) {
            // if d exists
            if (event.has("d")) {
                JsonObject data = event.getAsJsonObject("d");
                if (data.has("content") && data.has("author")) {
                    String username = data.getAsJsonObject("author").get("username").getAsString();
                    String content = data.get("content").getAsString();
                    if (!content.isEmpty()) {
                        System.out.println(username + ": " + content);
                        EventBus.trigger(new DiscordMessageReceived(content, username, data.get("channel_id").getAsString()));
                    }
                }

            }
            // all the data
//            String rawData = data.toString();
//            System.out.println(rawData);
        } else if (opCode == 11) {
//            System.out.println("Heartbeat received");
        }
    }

    private void startHeartbeat(int interval) {
        Timer heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        }, interval * 1000L, interval * 1000L);
        System.out.println("Heartbeat started with interval: " + interval + " seconds");
    }

    private void sendHeartbeat() {
        JsonObject heartbeatPayload = new JsonObject();
        heartbeatPayload.addProperty("op", 1);
        heartbeatPayload.addProperty("d", "null");
        sendMessage(heartbeatPayload.toString());
        System.out.println("Heartbeat sent");
    }

    private void sendMessage(String message) {
        webSocketClient.send(message);
    }
}

