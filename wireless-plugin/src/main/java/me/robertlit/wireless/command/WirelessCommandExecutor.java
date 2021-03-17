/*
    This file is a part of Wireless - a Spigot plugin that enables transmitting and receiving redstone signals without wires

    Copyright (C) 2021  robertlit
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License only.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.robertlit.wireless.command;

import me.robertlit.wireless.settings.Lang;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WirelessCommandExecutor implements TabExecutor {

    private final Map<String, WirelessCommand> commands = new LinkedHashMap<>();

    public void addCommand(@NotNull WirelessCommand command) {
        this.commands.put(command.getLabel(), command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String name = args.length > 0 ? args[0] : "help";
        WirelessCommand wirelessCommand = commands.get(name.toLowerCase());
        if (wirelessCommand != null) {
            if (!sender.hasPermission(wirelessCommand.getPermission())) {
                sender.sendMessage(Lang.disallowed);
                return false;
            }
            if (args.length - 1 < wirelessCommand.getRequiredArgsLength()) {
                sender.sendMessage(ChatColor.DARK_GREEN + wirelessCommand.getUsageMessage());
                return false;
            }
            wirelessCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        if (sender.hasPermission("wireless.command.help")) {
            sender.sendMessage(Lang.availableSubcommands);
            for (WirelessCommand available : this.commands.values()) {
                if (sender.hasPermission(available.getPermission())) {
                    sender.sendMessage(ChatColor.DARK_GREEN + available.getUsageMessage());
                }
            }
        } else {
            sender.sendMessage(Lang.disallowed);
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) {
            return complete(sender);
        } else if (args.length == 1) {
            return WirelessCommandExecutor.copyPartialMatches(args[0], complete(sender), new ArrayList<>());
        } else {
            WirelessCommand wirelessCommand = commands.get(args[0].toLowerCase());
            if (wirelessCommand != null) {
                if (sender.hasPermission(wirelessCommand.getPermission())) {
                    return wirelessCommand.complete(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> complete(CommandSender sender) {
        List<String> commands = new ArrayList<>(this.commands.keySet());
        commands.removeIf(s -> !sender.hasPermission(this.commands.get(s).getPermission()));
        if (sender.hasPermission("wireless.command.help")) {
            commands.add("help");
        }
        return commands;
    }

    protected static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<? extends String> iterable, T collection) {
        for (String str : iterable) {
            if (StringUtils.containsIgnoreCase(str, token)) {
                collection.add(str);
            }
        }
        return collection;
    }
}
