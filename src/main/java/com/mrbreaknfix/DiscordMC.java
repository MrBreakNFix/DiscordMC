package com.mrbreaknfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrbreaknfix.dc.Client;
import com.mrbreaknfix.event.EventManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiscordMC implements ModInitializer {
	public static Settings settings;
    public static final Logger LOGGER = LoggerFactory.getLogger("DiscordMC");
	public static MinecraftClient mc = MinecraftClient.getInstance();
	private static final Gson GSON = new Gson();
	public static EventManager EventBus = new EventManager();


	@Override
	public void onInitialize() {
		LOGGER.info("Starting DiscordMC");
		loadSettings();

		EventBus.addListener(new Client());

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
					dispatcher.register(ClientCommandManager.literal("dc")
							.then(ClientCommandManager.literal("refresh")
									.executes(context -> {
//										updateNameCaches();
										return 1;
									}))
									.then(ClientCommandManager.literal("save")
											.executes(context -> {
												saveSettings();
												return 1;
											}))
									.then(ClientCommandManager.literal("load")
											.executes(context -> {
												loadSettings();
												return 1;
											}))
							.then(ClientCommandManager.literal("start")
									.executes(context -> {
										Client.start();
										return 1;
									}))
							.then(ClientCommandManager.literal("stop")
									.executes(context -> {
										Client.stop();
										return 1;
									}))
							);




//							.then(ClientCommandManager.literal("listen")
//									.executes(context -> {
////										listListeningChannels();
//										return 1;
//									}))
//							.then(ClientCommandManager.literal("enable")
//									.executes(context -> {
//										settings.enabled = true;
//										mc.player.sendMessage(Text.of("DiscordMC ENABLED"));
//										return 1;
//									}))
//							.then(ClientCommandManager.literal("disable")
//									.executes(context -> {
//										settings.enabled = false;
//										mc.player.sendMessage(Text.of("DiscordMC DISABLED"));
//										return 1;
//									}))
//							.then(ClientCommandManager.literal("toggle")
//									.executes(context -> {
//										settings.enabled = !settings.enabled;
//										mc.player.sendMessage(Text.of("DiscordMC " + (settings.enabled ? "ENABLED" : "DISABLED")));
//										return 1;
//									}))
//							.then(ClientCommandManager.literal("select")
//									.executes(context -> {
////										select();
//										return 1;
//									}))
//							.then(ClientCommandManager.literal("debug")
//									.executes((context) -> {
//										var r = Text.literal("Sending list:");
//										for (String sendingChannel : settings.sendingChannels.values()) {
//											r.append("\n" + sendingChannel);
//										}
//										r.append("\nListening list:");
//										for (String listeningChannel : settings.listeningChannels.values()) {
//											r.append("\n" + listeningChannel);
//										}
//										mc.player.sendMessage(r);
//										return 1;
//									}))
//							.then(ClientCommandManager.literal("setToken")
//									.then(ClientCommandManager.argument("token", StringArgumentType.string())
//											.executes((context) -> {
////												setBotToken(StringArgumentType.getString(context, "token"));
//												return 1;
//											})))
//							.then(ClientCommandManager.literal("dest")
//									.executes(context -> {
////										select();
//										return 1;
//									})
//									.then(ClientCommandManager.argument("channel id", StringArgumentType.string())
//											.executes((context) -> {
////												setSendingChannel(StringArgumentType.getString(context, "channel id"));
//												return 1;
//											}))));
				});


		LOGGER.info("DiscordMC has been initialized");
		if (settings.token != null && settings.token.isEmpty()) {
			LOGGER.warn("No discord account is set!");
		} else {
			LOGGER.info("Starting client...");
		}
	}

	public static void saveSettings() {
		try	{
			Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
			String json = gson.toJson(settings);
			Files.writeString(FabricLoader.getInstance().getConfigDir().resolve("DiscordMC.json"), json);
		} catch (Exception e) {
			LOGGER.error("Failed to save settings: ", e);
        }
    }
	public static void loadSettings() {
		try {
			Path path = FabricLoader.getInstance().getConfigDir().resolve("DiscordMC.json");
			if (Files.exists(path)) {
				settings = GSON.fromJson(Files.readString(path), Settings.class);
			} else {
				settings = new Settings();
				saveSettings();
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load settings: ", e);
		}
	}
}