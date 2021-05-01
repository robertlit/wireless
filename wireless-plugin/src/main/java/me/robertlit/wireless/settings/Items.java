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

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class Items {

    private Items() {}

    private static NamespacedKey key;

    public static void setKey(@NotNull NamespacedKey key) {
        Items.key = key;
    }

    public static ItemStack guiBackground;
    public static ItemStack noTransmitterConnected;
    public static ItemStack nextPage;
    public static ItemStack previousPage;
    public static ItemStack goBack;
    public static ItemStack connectReceivers;
    public static ItemStack noLongerValid;

    public static void setGuiBackground(@NotNull Material material) {
        guiBackground = new ItemStack(material);
        ItemMeta meta = guiBackground.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            guiBackground.setItemMeta(meta);
        }
    }

    public static void setNoTransmitterConnected(@NotNull Material material) {
        noTransmitterConnected = new ItemStack(material);
        ItemMeta meta = noTransmitterConnected.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.noTransmitterConnected);
            meta.setLore(Arrays.asList(" ", Lang.clickToConnect));
            noTransmitterConnected.setItemMeta(meta);
        }
    }

    public static void setNextPage(@NotNull Material material) {
        nextPage = new ItemStack(material);
        ItemMeta meta = nextPage.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.nextPage);
            nextPage.setItemMeta(meta);
        }
    }

    public static void setPreviousPage(@NotNull Material material) {
        previousPage = new ItemStack(material);
        ItemMeta meta = previousPage.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.previousPage);
            previousPage.setItemMeta(meta);
        }
    }

    public static void setGoBack(@NotNull Material material) {
        goBack = new ItemStack(material);
        ItemMeta meta = goBack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.goBack);
            goBack.setItemMeta(meta);
        }
    }

    public static void setConnectReceivers(@NotNull Material material) {
        connectReceivers = new ItemStack(material);
        ItemMeta meta = connectReceivers.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.viewReceivers);
            meta.setLore(Arrays.asList("", Lang.connectAndDisconnectReceivers));
            connectReceivers.setItemMeta(meta);
        }
    }

    public static void setNoLongerValid(@NotNull Material material) {
        noLongerValid = new ItemStack(material);
        ItemMeta meta = noLongerValid.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.noLongerValid);
            noLongerValid.setItemMeta(meta);
        }
    }

    @NotNull
    public static ItemStack createDisplayItem(@NotNull WirelessComponent component, @Nullable String line) {
        ItemStack item = new ItemStack(component.getItemType());
        ItemMeta meta = item.getItemMeta();
        String name = component instanceof WirelessTransmitter ? Lang.wirelessTransmitterName : Lang.wirelessReceiverName;
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(
                    ChatColor.WHITE+"X: " + component.getLocation().getBlockX(),
                    ChatColor.WHITE+"Y: " + component.getLocation().getBlockY(),
                    ChatColor.WHITE+"Z: " + component.getLocation().getBlockZ(),
                    " ",
                    line));
            item.setItemMeta(meta);
        }
        return item;
    }

    @NotNull
    public static ItemStack createItem(@NotNull WirelessComponent component) {
        return createItem(component.getItemType(), component.getClass());
    }

    @NotNull
    public static ItemStack createItem(@NotNull Material material, @NotNull Class<? extends WirelessComponent> clazz) {
        boolean isTransmitter = WirelessTransmitter.class.isAssignableFrom(Objects.requireNonNull(clazz));
        if (!(isTransmitter || WirelessReceiver.class.isAssignableFrom(clazz))) {
            throw new IllegalArgumentException("Class must be off type WirelessTransmitter or WirelessReceiver");
        }
        ItemStack item = new ItemStack(Objects.requireNonNull(material));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(isTransmitter ? Lang.wirelessTransmitterName : Lang.wirelessReceiverName);
            meta.setLore(isTransmitter ? Lang.wirelessTransmitterLore : Lang.wirelessReceiverLore);
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING , isTransmitter ? Constants.TRANSMITTER : Constants.RECEIVER);
            item.setItemMeta(meta);
        }
        return item;
    }
}
