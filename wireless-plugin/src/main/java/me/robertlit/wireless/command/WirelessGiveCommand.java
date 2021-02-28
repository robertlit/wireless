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

import com.google.common.base.Enums;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.settings.Constants;
import me.robertlit.wireless.settings.Items;
import me.robertlit.wireless.settings.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WirelessGiveCommand implements WirelessCommand {

    private final List<String> materials = Arrays.stream(Material.values())
            .filter(material -> !material.name().contains("LEGACY"))
            .filter(material -> !material.isAir())
            .map(Material::name)
            .collect(Collectors.toList());
    private final List<String> types = Arrays.asList(Constants.TRANSMITTER, Constants.RECEIVER);

    @Override
    public @NotNull String getLabel() {
        return "give";
    }

    @Override
    public int getRequiredArgsLength() {
        return 3;
    }

    @Override
    public @NotNull String getPermission() {
        return "wireless.command.give";
    }

    @Override
    public @NotNull String getUsageMessage() {
        return "/wireless give <player> <transmitter/receiver> <material>";
    }


    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Lang.playerNotOnline);
            return;
        }
        String type = args[1];
        boolean isTransmitter = type.equalsIgnoreCase(Constants.TRANSMITTER);
        if (!(isTransmitter || type.equalsIgnoreCase(Constants.RECEIVER))) {
            sender.sendMessage(Lang.invalidComponentType);
            return;
        }
        Material material = Enums.getIfPresent(Material.class, args[2].toUpperCase()).orNull(); // TODO: never null
        if (material == null || !materials.contains(material.name())) {
            sender.sendMessage(Lang.invalidMaterial);
            return;
        }
        player.getInventory().addItem(Items.createItem(material, isTransmitter ? WirelessTransmitter.class : WirelessReceiver.class));
        sender.sendMessage(Lang.giveSuccessful);
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length <= 1) {
            return null;
        }
        if (args.length == 2) {
            return WirelessCommandExecutor.copyPartialMatches(args[1], types, new ArrayList<>());
        }
        if (args.length == 3) {
            return WirelessCommandExecutor.copyPartialMatches(args[2], materials, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
