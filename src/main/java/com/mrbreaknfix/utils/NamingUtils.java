package com.mrbreaknfix.utils;

import static com.mrbreaknfix.DiscordMC.settings;

public class NamingUtils {
    public static String getSendingChannel() {
        if (settings.sendingChannels.isEmpty()) {
            return "";
        }
        var ch = settings.sendingChannels.get(settings.sendingChannels.firstKey());
        return settings.channelNamesCache.getOrDefault(ch, ch);
    }
}
