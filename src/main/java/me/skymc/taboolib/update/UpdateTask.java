package me.skymc.taboolib.update;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ilummc.tlib.resources.TLocale;
import me.skymc.taboolib.Main;
import me.skymc.taboolib.TabooLib;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.player.PlayerUtils;
import me.skymc.taboolib.plugin.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * @author sky
 * @since 2018年2月23日 下午10:39:14
 */
public class UpdateTask {

    private static double newVersion = 0;
    private static int updateLocationUsing;
    private static String[][] updateLocation = {
            {
                    "https://api.github.com/repos/Bkm016/TabooLib/releases",
                    "https://github.com/Bkm016/TabooLib/releases/download/?/TabooLib-?.jar"
            },
            {
                    "https://gitee.com/bkm016/TabooLibCloud/raw/master/release.json",
                    "https://gitee.com/bkm016/TabooLibCloud/raw/master/core/TabooLib.jar"
            }
    };

    public UpdateTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!Main.getInst().getConfig().getBoolean("UPDATE-CHECK")) {
                    return;
                }
                boolean success = false;
                for (int i = 0; i < updateLocation.length; i++) {
                    String[] location = updateLocation[i];
                    String value = FileUtils.getStringFromURL(location[0], null);
                    if (value == null) {
                        continue;
                    }
                    JsonElement json = new JsonParser().parse(value);
                    if (json.isJsonArray()) {
                        updateLocationUsing = i;
                        newVersion = json.getAsJsonArray().get(0).getAsJsonObject().get("tag_name").getAsDouble();
                        if (TabooLib.getPluginVersion() >= newVersion) {
                            TLocale.Logger.info("UPDATETASK.VERSION-LATEST");
                        } else {
                            TLocale.Logger.info("UPDATETASK.VERSION-OUTDATED", String.valueOf(TabooLib.getPluginVersion()), String.valueOf(newVersion));
                            Bukkit.getScheduler().runTask(TabooLib.instance(), () -> updatePlugin(true, false));
                        }
                        return;
                    }
                }
                if (!success) {
                    TLocale.Logger.error("UPDATETASK.VERSION-FAIL");
                }
            }
        }.runTaskTimerAsynchronously(Main.getInst(), 100, 20 * 60 * 60 * 6);
    }

    public static boolean isHaveUpdate() {
        return newVersion > TabooLib.getPluginVersion();
    }

    public static double getNewVersion() {
        return newVersion;
    }

    public static int getUpdateLocationUsing() {
        return updateLocationUsing;
    }

    public static String[][] getUpdateLocation() {
        return updateLocation;
    }

    public static void updatePlugin(boolean shutdown, boolean force) {
        if (!UpdateTask.isHaveUpdate() || (newVersion == 0 || !force)) {
            TLocale.Logger.info("COMMANDS.TABOOLIB.UPDATEPLUGIN.UPDATE-NOT-FOUND");
            return;
        }
        if (PlayerUtils.getOnlinePlayers().size() > 0) {
            TLocale.Logger.info("COMMANDS.TABOOLIB.UPDATEPLUGIN.PLAYER-ONLINE");
            return;
        }
        File pluginFile = PluginUtils.getPluginFile(Main.getInst());
        if (pluginFile == null) {
            TLocale.Logger.info("COMMANDS.TABOOLIB.UPDATEPLUGIN.FILE-NOT-FOUND");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(TabooLib.instance(), () -> {
            FileUtils.download(updateLocation[updateLocationUsing][1].replace("?", String.valueOf(newVersion)), pluginFile);
            TLocale.Logger.info("COMMANDS.TABOOLIB.UPDATEPLUGIN.UPDATE-SUCCESS");
            if (shutdown) {
                Bukkit.shutdown();
            }
        });
    }
}
