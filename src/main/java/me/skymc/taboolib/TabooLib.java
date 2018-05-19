package me.skymc.taboolib;

import me.skymc.taboolib.other.NumberUtils;
import me.skymc.taboolib.playerdata.DataUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author sky
 */
public class TabooLib {

    private static boolean spigot = false;

    static {
        try {
            spigot = Bukkit.getConsoleSender() != null;
        } catch (Exception ignored) {
        }
    }

    public static boolean isSpigot() {
        return spigot;
    }

    public static boolean isDebug() {
        return Main.getInst().getConfig().getBoolean("DEBUG");
    }

    public static void debug(Plugin plugin, String... args) {
        if (Main.getInst().getConfig().getBoolean("DEBUG")) {
            Arrays.stream(args).forEach(var -> Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TabooLib - DEBUG][" + plugin.getName() + "] " + ChatColor.RED + var));
        }
    }

    public static double getPluginVersion() {
        return NumberUtils.getDouble(Main.getInst().getDescription().getVersion());
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static String getServerUID() {
        if (!DataUtils.getPluginData("TabooLibrary", null).contains("serverUID")) {
            DataUtils.getPluginData("TabooLibrary", null).set("serverUID", UUID.randomUUID().toString());
        }
        return DataUtils.getPluginData("TabooLibrary", null).getString("serverUID");
    }

    public static void resetServerUID() {
        DataUtils.getPluginData("TabooLibrary", null).set("serverUID", UUID.randomUUID().toString());
    }

    public static int getVerint() {
        if (getVersion().startsWith("v1_7")) {
            return 10700;
        } else if (getVersion().startsWith("v1_8")) {
            return 10800;
        } else if (getVersion().startsWith("v1_9")) {
            return 10900;
        } else if (getVersion().startsWith("v1_10")) {
            return 11000;
        } else if (getVersion().startsWith("v1_11")) {
            return 11100;
        } else if (getVersion().startsWith("v1_12")) {
            return 11200;
        } else if (getVersion().startsWith("v1_13")) {
            return 11300;
        }
        return 0;
    }
}
