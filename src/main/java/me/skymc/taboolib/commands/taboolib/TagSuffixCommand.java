package me.skymc.taboolib.commands.taboolib;

import com.ilummc.tlib.resources.TLocale;
import me.clip.placeholderapi.PlaceholderAPI;
import me.skymc.taboolib.commands.SubCommand;
import me.skymc.taboolib.team.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author sky
 * @since 2018-03-19 23:13:35
 */
public class TagSuffixCommand extends SubCommand {

    public TagSuffixCommand(CommandSender sender, String[] args) {
        super(sender, args);
        if (args.length < 3) {
            TLocale.sendTo(sender, "COMMANDS.PARAMETER.UNKNOWN");
            return;
        }

        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            TLocale.sendTo(sender, "COMMANDS.TABOOLIB.PLAYERTAG.INVALID-PLAYER", args[1]);
            return;
        }

        String value = getArgs(2).replace("&", "§");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            value = PlaceholderAPI.setPlaceholders(player, value);
        }

        TagManager.getInst().setSuffix(player, value);
        if (sender instanceof Player) {
            TLocale.sendTo(sender, "COMMANDS.TABOOLIB.PLAYERTAG.SUCCESS-SUFFIX-SET", args[1], value);
        }
    }

}
