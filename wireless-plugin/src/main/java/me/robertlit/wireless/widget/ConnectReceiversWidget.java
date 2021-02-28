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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectReceiversWidget implements WirelessComponentInventoryWidget<WirelessTransmitter> {

    private final WirelessComponentManager wirelessComponentManager;
    private final InventoryManager widgetInventoryManager;

    private final WirelessPlugin plugin;

    public ConnectReceiversWidget(@NotNull WirelessComponentManager wirelessComponentManager, @NotNull InventoryManager widgetInventoryManager, @NotNull WirelessPlugin plugin) {
        this.wirelessComponentManager = wirelessComponentManager;
        this.widgetInventoryManager = widgetInventoryManager;
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public ItemStack getDisplayItem(@NotNull WirelessTransmitter component) {
        return Items.connectReceivers;
    }

    @Override
    public void handleClick(@NotNull WirelessTransmitter transmitter, @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        List<WirelessReceiver> components = wirelessComponentManager.getComponentsOf(player.getUniqueId())
                .stream()
                .filter(component -> {
                    if (!(component instanceof WirelessReceiver)) {
                        return false;
                    }
                    if (component.getLocation().getWorld() != transmitter.getLocation().getWorld()) {
                        return false;
                    }
                    double distance = component.getLocation().distanceSquared(transmitter.getLocation());
                    if (distance <= 1 || distance > Settings.maxConnectionDistance * Settings.maxConnectionDistance) {
                        return false;
                    }
                    WirelessReceiver receiver = (WirelessReceiver) component;
                    return receiver.getSubscription() == null || receiver.getSubscription() == transmitter;
                })
                .map(component -> (WirelessReceiver) component)
                .sorted(Comparator.comparingDouble(receiver -> receiver.getLocation().distanceSquared(transmitter.getLocation())))
                .collect(Collectors.toList());
        plugin.getServer().getScheduler().runTask(plugin, () -> new ViewComponentsInventory<>(components,
                receiver -> receiver.getSubscription() != null ? Lang.clickToDisconnect : Lang.clickToConnect,
                receiver -> {
                    if (receiver.getSubscription() == null) {
                        receiver.subscribe(transmitter);
                    } else {
                        receiver.unsubscribe();
                    }
                }, transmitter::openInventory, false, Lang.availableReceivers, widgetInventoryManager, plugin).open(player));
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "wireless_connect_receivers";
    }
}
