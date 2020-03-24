package io.izzel.taboolib.common.listener;

import com.google.common.collect.Lists;
import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.TabooLibAPI;
import io.izzel.taboolib.common.loader.Startup;
import io.izzel.taboolib.common.loader.StartupLoader;
import io.izzel.taboolib.module.command.lite.CommandBuilder;
import io.izzel.taboolib.module.db.local.Local;
import io.izzel.taboolib.module.db.local.LocalPlayer;
import io.izzel.taboolib.module.hologram.Hologram;
import io.izzel.taboolib.module.hologram.THologram;
import io.izzel.taboolib.module.inject.TListener;
import io.izzel.taboolib.module.locale.logger.TLogger;
import io.izzel.taboolib.module.tellraw.TellrawJson;
import io.izzel.taboolib.util.Files;
import io.izzel.taboolib.util.book.BookFormatter;
import io.izzel.taboolib.util.item.Items;
import io.izzel.taboolib.util.lite.Signs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sky
 */
@TListener
public class ListenerCommand implements Listener {

    static {
        StartupLoader.register(ListenerCommand.class);
    }

    abstract class Module {

        abstract public String[] name();

        abstract public void run(Player player);
    }

    List<Module> testUtil = Lists.newArrayList(
            new Module() {
                @Override
                public String[] name() {
                    return new String[] {"json", "tellrawJson"};
                }

                @Override
                public void run(Player player) {
                    TellrawJson.create()
                            .append("§8[§fTabooLib§8] §7TellrawJson: §f[")
                            .append(Items.getName(player.getItemInHand())).hoverItem(player.getItemInHand())
                            .append("§f]")
                            .send(player);
                }
            },
            new Module() {
                @Override
                public String[] name() {
                    return new String[] {"sign", "fakeSign"};
                }

                @Override
                public void run(Player player) {
                    Signs.fakeSign(player, lines -> player.sendMessage("§8[§fTabooLib§8] §7FakeSign: §f" + Arrays.toString(lines)));
                }
            },
            new Module() {
                @Override
                public String[] name() {
                    return new String[] {"hd", "hologram"};
                }

                @Override
                public void run(Player player) {
                    player.sendMessage("§8[§fTabooLib§8] §7Hologram.");
                    Location location = player.getEyeLocation().add(player.getLocation().getDirection());
                    Hologram hologram = THologram.create(location, "TabooLib", player)
                            .flash(Lists.newArrayList(
                                    "§bT§fabooLib",
                                    "§bTa§fbooLib",
                                    "§bTab§fooLib",
                                    "§bTabo§foLib",
                                    "§bTaboo§fLib",
                                    "§bTabooL§fib",
                                    "§bTabooLi§fb",
                                    "§bTabooLib",
                                    "§bTabooLi§fb",
                                    "§bTabooL§fib",
                                    "§bTaboo§fLib",
                                    "§bTabo§foLib",
                                    "§bTab§fooLib",
                                    "§bTa§fbooLib",
                                    "§bT§fabooLib",
                                    "§fTabooLib"
                            ), 1).deleteOn(30);
                }
            },
            new Module() {
                @Override
                public String[] name() {
                    return new String[] {"book", "bookBuilder"};
                }

                @Override
                public void run(Player player) {
                    BookFormatter.writtenBook()
                            .generation(BookMeta.Generation.COPY_OF_COPY)
                            .addPage(TellrawJson.create()
                                    .append("BookBuilder")
                                    .hoverText("HoverText"))
                            .open(player);
                }
            });

    @Startup.Starting
    public void init() {
        // 版本命令
        CommandBuilder.create("taboolib", TabooLib.getPlugin())
                .aliases("lib")
                .execute((sender, args) -> {
                    sender.sendMessage("§8[§fTabooLib§8] §7Currently Version: §fv" + TabooLib.getVersion());
                }).build();
        // 调试命令
        CommandBuilder.create("taboolibtest", TabooLib.getPlugin())
                .permission("*")
                .aliases("libtest")
                .tab((sender, args) -> testUtil.stream().flatMap(module -> Arrays.stream(module.name())).filter(name -> name.toLowerCase().startsWith(args[0])).collect(Collectors.toList()))
                .execute((sender, args) -> {
                    if (sender instanceof Player) {
                        if (args.length == 0) {
                            sender.sendMessage("§8[§fTabooLib§8] §7/libtest §8[...]");
                            return;
                        }
                        for (Module module : testUtil) {
                            for (String name : module.name()) {
                                if (name.equalsIgnoreCase(args[0])) {
                                    module.run((Player) sender);
                                    return;
                                }
                            }
                        }
                    }
                }).build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void cmd(ServerCommandEvent e) {
        if (e.getCommand().equalsIgnoreCase("saveFiles")) {
            Local.saveFiles();
            LocalPlayer.saveFiles();
            TLogger.getGlobalLogger().info("Successfully.");
        } else if (e.getCommand().equalsIgnoreCase("tDebug")) {
            if (TabooLibAPI.isDebug()) {
                TabooLibAPI.debug(false);
                TLogger.getGlobalLogger().info("&cDisabled.");
            } else {
                TabooLibAPI.debug(true);
                TLogger.getGlobalLogger().info("&aEnabled.");
            }
        } else if (e.getCommand().equalsIgnoreCase("libUpdate")) {
            e.setCancelled(true);
            e.getSender().sendMessage("§8[§fTabooLib§8] §cWARNING §7| §4Update TabooLib will force to restart your server. Please confirm this action by type §c/libupdateconfirm");
        } else if (e.getCommand().equalsIgnoreCase("libUpdateConfirm") || e.getCommand().equalsIgnoreCase("libUpdate confirm")) {
            e.getSender().sendMessage("§8[§fTabooLib§8] §7Downloading TabooLib file...");
            Files.downloadFile("https://skymc.oss-cn-shanghai.aliyuncs.com/plugins/TabooLib.jar", new File("libs/TabooLib.jar"));
            e.getSender().sendMessage("§8[§fTabooLib§8] §2Download completed, the server will restart in 3 secs");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Bukkit.shutdown();
        }
    }
}
