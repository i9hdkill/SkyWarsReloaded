package com.walrusone.skywars.utilities;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.dataStorage.DataStorage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Messaging {

    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)(&|" + String.valueOf(ChatColor.COLOR_CHAR) + ")[0-9A-FK-OR]");
    private final FileConfiguration storage;

    public Messaging(Plugin plugin) {
        File storageFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!storageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        DataStorage.copyDefaults(storageFile);
        storage = YamlConfiguration.loadConfiguration(storageFile);
    }

    public static String stripColor(String input) {
        if (input == null) {
            return "";
        }

        return COLOR_PATTERN.matcher(input).replaceAll("");
    }

    private String getPrefix() {
        return storage.getString("prefix", "");
    }

    public String getMessage(String format) {
        if (storage.contains(format)) {
            return storage.getString(format);
        }

        return null;
    }

    public static class MessageFormatter {

        private final static Pattern PATTERN = Pattern.compile("(?i)(\\{[a-z0-9_]+\\})");
        private final Map<String, String> variableMap = Maps.newHashMap();
        private boolean prefix;

        public MessageFormatter withPrefix() {
            prefix = true;
            return this;
        }

        public MessageFormatter setVariable(String format, String value) {
            if (format != null && !format.isEmpty()) {
                if (value == null) {
                    variableMap.remove( format );
                } else {
                    variableMap.put( format, value );
                }
            }
            return this;
        }

        public String format(String message) {
            if (message == null || message.isEmpty()) {
                return "";
            }

            if (SkyWarsReloaded.getMessaging().getMessage(message) != null) {
                message = SkyWarsReloaded.getMessaging().getMessage(message);
            }

            message = matcherReplace(message);

            if (prefix) {
                message = SkyWarsReloaded.getMessaging().getPrefix() + message;
            }

            return ChatColor.translateAlternateColorCodes('&', message);
        }
        
        public String formatNoColor(String message) {
            if (message == null || message.isEmpty()) {
                return "";
            }

            if (SkyWarsReloaded.getMessaging().getMessage(message) != null) {
                message = SkyWarsReloaded.getMessaging().getMessage(message);
            }

            message = matcherReplace(message);

            if (prefix) {
                message = SkyWarsReloaded.getMessaging().getPrefix() + message;
            }

            return message;
        }

        private String matcherReplace(String message) {
            Matcher matcher = PATTERN.matcher(message);

            while (matcher.find()) {
                String variable = matcher.group();
                variable = variable.substring(1, variable.length() - 1);

                String value = variableMap.get(variable);
                if (value == null) {
                    value = "";
                }

                message = message.replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(value));
            }
            return message;
        }
    }
    
}