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
package me.robertlit.wireless.settings;

import com.google.common.base.Enums;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigLoader {

    public void load(FileConfiguration config) {
        Settings.maxPlayerComponents = config.getInt("max-player-components", 100);
        Settings.maxConnectionDistance = config.getInt("max-connection-distance", 2000);
        Settings.protectBreak = config.getBoolean("protect-break", true);
        Settings.protectExplosion = config.getBoolean("protect-explosion", true);
        Settings.protectBurn = config.getBoolean("protect-burn", true);
        Settings.highlightsDistance = config.getDouble("highlight-distance", 50);
        Settings.highlightParticle = getEnum(config, "highlight-particle", Particle.class, Particle.VILLAGER_HAPPY);

        ConfigurationSection lang = config.getConfigurationSection("lang");
        Objects.requireNonNull(lang, "You have forgotten to specify language information");
        Lang.wirelessTransmitterName = getColored(lang, "wireless-transmitter-name", "&9&lWireless Transmitter");
        Lang.wirelessTransmitterLore = lang.getStringList("wireless-transmitter-lore")
                .stream()
                .map(this::color)
                .collect(Collectors.toList());
        Lang.wirelessReceiverName = getColored(lang, "wireless-receiver-name", "&9&lWireless Receiver");
        Lang.wirelessReceiverLore = lang.getStringList("wireless-receiver-lore")
                .stream()
                .map(this::color)
                .collect(Collectors.toList());
        Lang.wirelessTransmitterInventoryTitle = getColored(lang, "wireless-transmitter-inventory-title", "&9&lControl Wireless Transmitter");
        Lang.wirelessReceiverInventoryTitle = getColored(lang, "wireless-receiver-inventory-title", "&9&lControl Wireless Receiver");
        Lang.noTransmitterConnected = getColored(lang, "no-transmitter-connected", "&c&lNo transmitter connected");
        Lang.connectedToTransmitter = getColored(lang, "connected-to-transmitter", "&9&lConnected to transmitter");
        Lang.clickToDisconnect = getColored(lang, "click-to-disconnect", "&c&lCLICK TO DISCONNECT");
        Lang.clickToConnect = getColored(lang, "click-to-connect", "&c&lCLICK TO CONNECT");
        Lang.availableTransmitters = getColored(lang, "available-transmitters","&9&lAvailable Transmitters %current_page% / %total_pages%");
        Lang.availableReceivers = getColored(lang, "available-receivers:", "&9&lAvailable Receivers %current_page% / %total_pages%");
        Lang.nextPage = getColored(lang, "next-page", "&aNext page");
        Lang.previousPage = getColored(lang, "previous-page", "&aPrevious page");
        Lang.goBack = getColored(lang, "go-back", "&cGo back");
        Lang.viewReceivers = getColored(lang, "view-receivers", "&9&lView receivers");
        Lang.connectAndDisconnectReceivers = getColored(lang, "connect-and-disconnect-receivers", "&a&lCLICK TO CONNECT AND &c&lDISCONNECT &a&lRECEIVERS");
        Lang.disallowed = getColored(lang, "disallowed", "&cYou can't do that!");
        Lang.playerNotOnline = getColored(lang, "player-not-online", "&cThe specified player is not online");
        Lang.invalidComponentType = getColored(lang, "invalid-component-type", "&cThe component type should be either transmitter or receiver");
        Lang.invalidMaterial = getColored(lang, "invalid-material", "&cPlease specify a valid material");
        Lang.giveSuccessful = getColored(lang, "give-successful", "&aSuccessfully given an item");
        Lang.availableSubcommands = getColored(lang, "available-subcommands", "&2Available subcommands:");
        Lang.tooManyComponents = getColored(lang, "too-many-components", "&cYou have reached the maximum amount of components");
        Lang.noLongerValid = getColored(lang, "no-longer-valid", "&cNO LONGER VALID");
        Lang.highlightToggle = getColored(lang, "highlight-toggle", "&2Toggled highlight");

        ConfigurationSection items = config.getConfigurationSection("items");
        Objects.requireNonNull(items, "You have forgotten to specify items information");
        Items.setGuiBackground(material(items, "gui-background", Material.GRAY_STAINED_GLASS_PANE));
        Items.setNoTransmitterConnected(material(items, "no-transmitter-connected", Material.BARRIER));
        Items.setNextPage(material(items, "next-page", Material.ARROW));
        Items.setPreviousPage(material(items, "previous-page", Material.ARROW));
        Items.setGoBack(material(items, "go-back", Material.BARRIER));
        Items.setConnectReceivers(material(items, "connect-receivers", Material.IRON_BLOCK));
        Items.setNoLongerValid(material(items, "no-longer-valid", Material.BARRIER));
    }

    private String getColored(ConfigurationSection section, String path, String def) {
        return color(section.getString(path, def));
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private <E extends Enum<E>> E getEnum(ConfigurationSection section, String path, Class<E> clazz, E def) {
        return Enums.getIfPresent(clazz, section.getString(path, "")).or(def);
    }

    private Material material(ConfigurationSection section, String path, Material def) {
        return getEnum(section, path, Material.class, def);
    }
}
