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
package me.robertlit.wireless.widget;

import me.robertlit.wireless.WirelessPlugin;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.api.component.inventory.WirelessComponentInventoryWidget;
import me.robertlit.wireless.component.WirelessComponentManager;
import me.robertlit.wireless.inventory.InventoryManager;
import me.robertlit.wireless.settings.Items;
import me.robertlit.wireless.settings.Lang;
import me.robertlit.wireless.settings.Settings;
import me.robertlit.wireless.widget.inventory.ViewComponentsInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectTransmitterWidget implements WirelessComponentInventoryWidget<WirelessReceiver> {

    private final WirelessComponentManager wirelessComponentManager;
    private final InventoryManager widgetInventoryManager;

    private final WirelessPlugin plugin;

    public ConnectTransmitterWidget(WirelessComponentManager wirelessComponentManager, InventoryManager widgetInventoryManager, WirelessPlugin plugin) {
        this.wirelessComponentManager = wirelessComponentManager;
        this.widgetInventoryManager = widgetInventoryManager;
        this.plugin = plugin;
    }

    @Override
    public @NotNull ItemStack getDisplayItem(@NotNull WirelessReceiver component) {
        WirelessTransmitter subscription = component.getSubscription();
        if (subscription == null) {
            return Items.noTransmitterConnected;
        }
        ItemStack item = new ItemStack(subscription.getItemType());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.connectedToTransmitter);
            meta.setLore(Arrays.asList(
                    ChatColor.WHITE+"X: " + subscription.getLocation().getBlockX(),
                    ChatColor.WHITE+"Y: " + subscription.getLocation().getBlockY(),
                    ChatColor.WHITE+"Z: " + subscription.getLocation().getBlockZ(),
                    " ",
                    Lang.clickToDisconnect));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void handleClick(@NotNull WirelessReceiver receiver, @NotNull InventoryClickEvent event) {
        if (receiver.getSubscription() == null) {
            Player player = (Player) event.getWhoClicked();
            List<WirelessTransmitter> components = wirelessComponentManager.getComponentsOf(player.getUniqueId())
                    .stream()
                    .filter(component -> {
                        if (!(component instanceof WirelessTransmitter)) {
                            return false;
                        }
                        if (component.getLocation().getWorld() != receiver.getLocation().getWorld()) {
                            return false;
                        }
                        double distance = component.getLocation().distanceSquared(receiver.getLocation());
                        return distance > 1 && component.getLocation().distanceSquared(receiver.getLocation()) <= Settings.maxConnectionDistance * Settings.maxConnectionDistance;
                    })
                    .map(component -> (WirelessTransmitter) component)
                    .sorted(Comparator.comparingDouble(transmitter -> transmitter.getLocation().distanceSquared(receiver.getLocation())))
                    .collect(Collectors.toList());
            plugin.getServer().getScheduler().runTask(plugin, () -> new ViewComponentsInventory<>(components,
                    transmitter -> Lang.clickToConnect,
                    receiver::subscribe,
                    receiver::openInventory, true, Lang.availableTransmitters, widgetInventoryManager, plugin).open(player));
            return;
        }
        receiver.unsubscribe();
    }
}
