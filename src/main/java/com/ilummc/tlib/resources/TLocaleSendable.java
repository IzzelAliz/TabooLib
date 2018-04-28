package com.ilummc.tlib.resources;

import org.bukkit.command.CommandSender;

public interface TLocaleSendable {

    static TLocaleSendable getEmpty(String path) {
        return new TLocaleSendable() {
            @Override
            public void sendTo(CommandSender sender, String... args) {
                sender.sendMessage("§4<" + path + "§4>");
            }

            @Override
            public String asString(String... args) {
                return "§4<" + path + "§4>";
            }
        };
    }

    static TLocaleSendable getEmpty() {
        return new TLocaleSendable() {
            @Override
            public void sendTo(CommandSender sender, String... args) {
            }

            @Override
            public String asString(String... args) {
                return "";
            }
        };
    }

    void sendTo(CommandSender sender, String... args);

    String asString(String... args);
}
