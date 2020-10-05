package io.izzel.taboolib.module.locale;

import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.TabooLibAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-05-12 14:01
 */
public abstract class TLocaleSerialize implements TLocaleSender, ConfigurationSerializable {

    public static boolean isPlaceholderEnabled(Map<String, Object> map) {
        Object placeholderObject = map.getOrDefault("papi", TLocale.Translate.isPlaceholderUseDefault());
        return placeholderObject instanceof Boolean ? (boolean) placeholderObject : placeholderObject instanceof String && "true".equals(placeholderObject);
    }

    public static String getStringOrDefault(Map<String, Object> map, String path, String def) {
        Object var = map.getOrDefault(path, def);
        return var instanceof String ? (String) var : def;
    }

    public static Integer getIntegerOrDefault(Map<String, Object> map, String path, Integer def) {
        Object var = map.getOrDefault(path, def);
        return var instanceof Integer ? (Integer) var : def;
    }

    public static Double getDoubleOrDefault(Map<String, Object> map, String path, Double def) {
        Object var = map.getOrDefault(path, def);
        return var instanceof Double ? (Double) var : def;
    }

    static TLocaleSerialize getEmpty() {
        return new TLocaleSerialize() {

            @Override
            public void sendTo(CommandSender sender, String... args) {
            }

            @Override
            public Map<String, Object> serialize() {
                return null;
            }
        };
    }

    static TLocaleSerialize getEmpty(Plugin plugin, String path) {
        return new TLocaleSerialize() {

            @Override
            public Map<String, Object> serialize() {
                return null;
            }

            @Override
            public void sendTo(CommandSender sender, String... args) {
                if (TabooLibAPI.isDependTabooLib(plugin)) {
                    TLocaleLoader.sendTo(TabooLib.getPlugin(), path, sender, args);
                } else {
                    sender.sendMessage("§8Notfound: " + path);
                }
            }

            @Override
            public String asString(String... args) {
                if (TabooLibAPI.isDependTabooLib(plugin)) {
                    return TLocaleLoader.asString(TabooLib.getPlugin(), path, args);
                } else {
                    return "§8Notfound: " + path;
                }
            }

            @Override
            public List<String> asStringList(String... args) {
                if (TabooLibAPI.isDependTabooLib(plugin)) {
                    return TLocaleLoader.asStringList(TabooLib.getPlugin(), path, args);
                } else {
                    return Collections.singletonList("§8Notfound: " + path);
                }
            }
        };
    }

    @Override
    public String asString(String... args) {
        return "";
    }

    @Override
    public List<String> asStringList(String... args) {
        return Collections.emptyList();
    }
}
