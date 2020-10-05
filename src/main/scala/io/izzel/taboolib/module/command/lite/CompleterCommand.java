package io.izzel.taboolib.module.command.lite;

import org.bukkit.command.CommandSender;

/**
 * 为了防止与 CommandExecutor 混淆所以名称改为 CompleterCommand
 *
 * @author sky
 */
public interface CompleterCommand {

    void execute(CommandSender sender, String[] args);

}