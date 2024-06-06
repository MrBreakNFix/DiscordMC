package com.mrbreaknfix.mixin;

import com.mrbreaknfix.DiscordMC;
import com.mrbreaknfix.dc.Client;
import com.mrbreaknfix.utils.NamingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
	protected ChatScreenMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
	public void sendMessage(String chatText, boolean addToHistory, CallbackInfo ci) throws IOException {
		if (chatText.startsWith("/") || chatText.startsWith(".") || chatText.startsWith(",")) {
			return;
		}
		// Why would you want discord messages sent to the server?
		if (DiscordMC.settings.enabled) {
			Client.sendText(chatText);
			ci.cancel();
			MinecraftClient.getInstance().setScreen(null);
		}
	}

	@Inject(at = @At("TAIL"), method = "init")
	public void init(CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();

		var cc = NamingUtils.getSendingChannel();
		if (cc.isEmpty() || !DiscordMC.settings.enabled) {
			return;
		}

		MutableText channel = Text.literal( cc + " ").formatted(Formatting.WHITE);
		MutableText chatDestination = Text.literal("Chatting in #").formatted(Formatting.GRAY).append(channel);

		MutableText text = Text.literal("").formatted(Formatting.GREEN).append(chatDestination);

		MultilineTextWidget multilineTextWidget = getTextWidget(text, mc);
		addDrawableChild(multilineTextWidget);
	}

	@Unique
	@NotNull
	private MultilineTextWidget getTextWidget(MutableText text, MinecraftClient mc) {
		MultilineTextWidget multilineTextWidget = new MultilineTextWidget(text, this.textRenderer);
		// bottom right version
//        multilineTextWidget.setPosition(mc.getWindow().getScaledWidth() - multilineTextWidget.getWidth() - 2, mc.getWindow().getScaledHeight() - 25); // bottom right version
		// top right version
		multilineTextWidget.setPosition(mc.getWindow().getScaledWidth() - multilineTextWidget.getWidth() - 2, 2); // top right version
		return multilineTextWidget;
	}
}