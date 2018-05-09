package me.skymc.taboolib;

import com.ilummc.tlib.TLib;
import me.skymc.taboolib.anvil.AnvilContainerAPI;
import me.skymc.taboolib.bstats.Metrics;
import me.skymc.taboolib.commands.MainCommands;
import me.skymc.taboolib.commands.internal.InternalCommandExecutor;
import me.skymc.taboolib.commands.language.Language2Command;
import me.skymc.taboolib.commands.locale.TabooLibLocaleCommand;
import me.skymc.taboolib.commands.plugin.TabooLibPluginCommand;
import me.skymc.taboolib.commands.sub.itemlist.listener.ItemLibraryPatch;
import me.skymc.taboolib.commands.sub.sounds.listener.SoundsLibraryPatch;
import me.skymc.taboolib.database.GlobalDataManager;
import me.skymc.taboolib.database.PlayerDataManager;
import me.skymc.taboolib.economy.EcoUtils;
import me.skymc.taboolib.entity.EntityUtils;
import me.skymc.taboolib.fileutils.ConfigUtils;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.inventory.speciaitem.SpecialItem;
import me.skymc.taboolib.javashell.JavaShell;
import me.skymc.taboolib.listener.*;
import me.skymc.taboolib.message.ChatCatcher;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboolib.mysql.protect.MySQLConnection;
import me.skymc.taboolib.nms.item.DabItemUtils;
import me.skymc.taboolib.other.NumberUtils;
import me.skymc.taboolib.permission.PermissionUtils;
import me.skymc.taboolib.playerdata.DataUtils;
import me.skymc.taboolib.sign.SignUtils;
import me.skymc.taboolib.skript.SkriptHandler;
import me.skymc.taboolib.string.StringUtils;
import me.skymc.taboolib.string.language2.Language2;
import me.skymc.taboolib.support.SupportPlaceholder;
import me.skymc.taboolib.team.TagUtils;
import me.skymc.taboolib.timecycle.TimeCycleManager;
import me.skymc.taboolib.update.UpdateTask;
import me.skymc.tlm.TLM;
import me.skymc.tlm.command.TLMCommands;
import me.skymc.tlm.module.TabooLibraryModule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Random;

@SuppressWarnings("deprecation")
public class Main extends JavaPlugin implements Listener {

    private static Plugin inst;

    private static Economy Economy;

    private static File playerDataFolder;

    private static File serverDataFolder;

    private static StorageType storageType;

    private static boolean disable = false;

    private static MySQLConnection connection = null;

    private static Language2 exampleLanguage2;

    private static boolean started;

    private FileConfiguration config = null;

    public static Plugin getInst() {
        return inst;
    }

    public static String getPrefix() {
        return "§8[§3§lTabooLib§8] §7";
    }

    public static net.milkbowl.vault.economy.Economy getEconomy() {
        return Economy;
    }

    public static void setEconomy(net.milkbowl.vault.economy.Economy economy) {
        Economy = economy;
    }

    public static File getPlayerDataFolder() {
        return playerDataFolder;
    }

    public static File getServerDataFolder() {
        return serverDataFolder;
    }

    public static StorageType getStorageType() {
        return storageType;
    }

    public static boolean isDisable() {
        return disable;
    }

    public static MySQLConnection getConnection() {
        return connection;
    }

    public static Language2 getExampleLanguage2() {
        return exampleLanguage2;
    }

    public static boolean isStarted() {
        return started;
    }

    public static Random getRandom() {
        return NumberUtils.getRandom();
    }

    public static String getTablePrefix() {
        return inst.getConfig().getString("MYSQL.PREFIX");
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void saveDefaultConfig() {
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveResource("config.yml", true);
        }
        config = ConfigUtils.load(this, file);
    }

    @Override
    public void onLoad() {
        inst = this;
        disable = false;

        // 载入配置
        saveDefaultConfig();

        // 加载依赖
        TLib.init();
        TLib.injectPluginManager();

        // 载入目录
        setupDataFolder();
        // 注册配置
        DataUtils.addPluginData("TabooLibrary", null);

        // 启用数据库
        if (getConfig().getBoolean("MYSQL.ENABLE")) {
            // 连接数据库
            connection = new MySQLConnection(getConfig().getString("MYSQL.HOST"), getConfig().getString("MYSQL.USER"), getConfig().getString("MYSQL.POST"), getConfig().getString("MYSQL.PASSWORD"), getConfig().getString("MYSQL.DATABASE"), 30, this);
            // 连接成功
            if (connection.isConnection()) {
                // 创建表
                connection.createTable(getTablePrefix() + "_playerdata", "username", "configuration");
                connection.createTable(getTablePrefix() + "_plugindata", "name", "variable", "upgrade");
                connection.createTable(getTablePrefix() + "_serveruuid", "uuid", "hash");

                // 如果没有数据
                if (!connection.isExists(getTablePrefix() + "_serveruuid", "uuid", TabooLib.getServerUID())) {
                    connection.intoValue(getTablePrefix() + "_serveruuid", TabooLib.getServerUID(), StringUtils.hashKeyForDisk(getDataFolder().getPath()));
                } else {
                    String hash = connection.getValue(getTablePrefix() + "_serveruuid", "uuid", TabooLib.getServerUID(), "hash").toString();
                    // 如果这个值和我的值不同
                    if (!hash.equals(StringUtils.hashKeyForDisk(getDataFolder().getPath()))) {
                        MsgUtils.warn("检测到本服序列号与其他服务器相同, 已重新生成!");
                        // 重新生成序列号
                        TabooLib.resetServerUID();
                        // 关服
                        Bukkit.shutdown();
                    }
                }
            } else {
                // 提示
                MsgUtils.warn("数据库连接失败, 请检查配置是否正确!");
                // 关服
                Bukkit.shutdown();
            }
            // 储存方式
            storageType = StorageType.SQL;
        } else {
            // 储存方式
            storageType = StorageType.LOCAL;
        }
    }

    @Override
    public void onEnable() {
        // 注册指令
        getCommand("taboolib").setExecutor(new MainCommands());
        getCommand("language2").setExecutor(new Language2Command());
        getCommand("taboolibrarymodule").setExecutor(new TLMCommands());
        getCommand("tabooliblocale").setExecutor(new TabooLibLocaleCommand());
        InternalCommandExecutor.createCommandExecutor("taboolibplugin", new TabooLibPluginCommand());

        // 注册监听
        registerListener();
        // 载入经济
        EcoUtils.setupEconomy();
        // 载入权限
        PermissionUtils.loadRegisteredServiceProvider();
        // 物品名称
        ItemUtils.LoadLib();
        // 低层工具
        DabItemUtils.getInstance();
        // 载入周期管理器
        TimeCycleManager.load();
        // 启动脚本
        JavaShell.javaShellSetup();
        // 载入语言文件
        exampleLanguage2 = new Language2("Language2", this);
        // 注册脚本
        SkriptHandler.getInst();

        // 启动数据库储存方法
        if (getStorageType() == StorageType.SQL) {
            GlobalDataManager.SQLMethod.startSQLMethod();
        }

        // 载入完成
        MsgUtils.send("§7插件载入完成!");
        MsgUtils.send("§7插件版本: §f" + getDescription().getVersion());
        MsgUtils.send("§7插件作者: §f" + getDescription().getAuthors());
        MsgUtils.send("§7游戏版本: §f" + TabooLib.getVerint());

        // 文件保存
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, DataUtils::saveAllCaches, 20, 20 * 120);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerDataManager.saveAllCaches(true, false), 20, 20 * 60);

        // 插件联动
        new BukkitRunnable() {

            @Override
            public void run() {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    new SupportPlaceholder(getInst(), "taboolib").hook();
                }
                // 载入 SpecialItem 接口
                SpecialItem.getInst().loadItems();
                // 载入 TLM 接口
                TLM.getInst();
            }
        }.runTask(this);

        // 更新检测
        new UpdateTask();
        // 启动监控
        new Metrics(this);

        // 启动
        started = true;
    }

    @Override
    public void onDisable() {
        disable = true;

        // 如果插件尚未启动完成
        if (!started) {
            MsgUtils.send("&c插件尚未启动完成, 已跳过卸载代码");
            MsgUtils.send("&c插件作者: &4坏黑");
            return;
        }

        // 保存数据
        Bukkit.getOnlinePlayers().forEach(x -> DataUtils.saveOnline(x.getName()));
        // 结束线程
        Bukkit.getScheduler().cancelTasks(this);
        // 保存插件数据
        DataUtils.saveAllCaches();
        // 保存玩家数据
        PlayerDataManager.saveAllPlayers(false, true);
        // 结束脚本
        JavaShell.javaShellCancel();
        // 注销 SpecialItem 接口
        SpecialItem.getInst().unloadItems();
        // 注销 TLM 接口
        TabooLibraryModule.getInst().unloadModules();

        // 结束数据库储存方法
        if (getStorageType() == StorageType.SQL) {
            GlobalDataManager.SQLMethod.cancelSQLMethod();
        }

        // 清理数据
        if (getStorageType() == StorageType.LOCAL && getConfig().getBoolean("DELETE-DATA")) {
            getPlayerDataFolder().delete();
        }
        // 清理数据
        if (getStorageType() == StorageType.SQL && getConfig().getBoolean("DELETE-VARIABLE")) {
            GlobalDataManager.clearInvalidVariables();
        }

        // 提示信息
        MsgUtils.send("&c插件已卸载, 感谢您使用&4禁忌书库");
        MsgUtils.send("&c插件作者: &4坏黑");

        // 清理头衔
        TagUtils.delete();

        // 结束连接
        if (connection != null && connection.isConnection()) {
            connection.closeConnection();
        }

        TLib.unload();

        // 关闭服务器
        Bukkit.shutdown();
    }

    private void setupDataFolder() {
        playerDataFolder = new File(getConfig().getString("DATAURL.PLAYER-DATA"));
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
        serverDataFolder = new File(getConfig().getString("DATAURL.SERVER-DATA"));
        if (!serverDataFolder.exists()) {
            serverDataFolder.mkdirs();
        }
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ListenerPlayerCommand(), this);
        getServer().getPluginManager().registerEvents(new ListenerPlayerJump(), this);
        getServer().getPluginManager().registerEvents(new ListenerPlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new ChatCatcher(), this);
        getServer().getPluginManager().registerEvents(new DataUtils(), this);
        getServer().getPluginManager().registerEvents(new AnvilContainerAPI(), this);
        getServer().getPluginManager().registerEvents(new ListenerPluginDisable(), this);
        getServer().getPluginManager().registerEvents(new PlayerDataManager(), this);
        getServer().getPluginManager().registerEvents(new ItemLibraryPatch(), this);
        getServer().getPluginManager().registerEvents(new SoundsLibraryPatch(), this);

        if (TabooLib.getVerint() > 10700) {
            getServer().getPluginManager().registerEvents(new EntityUtils(), this);
            getServer().getPluginManager().registerEvents(new SignUtils(), this);
        }

        if (Bukkit.getPluginManager().getPlugin("YUM") != null) {
            getServer().getPluginManager().registerEvents(new ListenerNetWork(), this);
        }
    }

    public enum StorageType {
        LOCAL, SQL
    }
}
