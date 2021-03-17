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

import me.robertlit.wireless.highlight.HighlightExecutor;
import me.robertlit.wireless.settings.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WirelessHighlightCommand implements WirelessCommand {

    private final HighlightExecutor executor;

    public WirelessHighlightCommand(HighlightExecutor executor) {
        this.executor = executor;
    }

    @Override
    public @NotNull String getLabel() {
        return "highlight";
    }

    @Override
    public int getRequiredArgsLength() {
        return 0;
    }

    @Override
    public @NotNull String getPermission() {
        return "wireless.command.highlight";
    }

    @Override
    public @NotNull String getUsageMessage() {
        return "/wireless highlight";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.disallowed);
            return;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        executor.toggle(uuid);
        player.sendMessage(Lang.highlightToggle);
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
