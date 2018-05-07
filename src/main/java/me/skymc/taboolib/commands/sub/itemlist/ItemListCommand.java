package me.skymc.taboolib.commands.sub.itemlist;

import me.skymc.taboolib.commands.SubCommand;
import me.skymc.taboolib.commands.sub.itemlist.listener.ItemLibraryPatch;
import me.skymc.taboolib.other.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author sky
 * @since 2018年2月4日 下午8:08:22
 */
public class ItemListCommand extends SubCommand {

    public ItemListCommand(CommandSender sender, String[] args) {
        super(sender, args);
        if (isPlayer()) {
            if (args.length == 1) {
                ItemLibraryPatch.openInventory((Player) sender, 1);
            } else {
                ItemLibraryPatch.openInventory((Player) sender, NumberUtils.getInteger(args[1]));
            }
        }
    }
}
