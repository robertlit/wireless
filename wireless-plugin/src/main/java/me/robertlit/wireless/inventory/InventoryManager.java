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
package me.robertlit.wireless.inventory;

import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.api.component.inventory.WirelessComponentInventoryWidget;
import me.robertlit.wireless.component.inventory.WirelessComponentInventory;
import me.robertlit.wireless.settings.Lang;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryManager {

    private final Map<Player, CustomInventory> openInventories = new HashMap<>();
    private final List<WirelessComponentInventoryWidget<? super WirelessTransmitter>> transmitterWidgets = new ArrayList<>();
    private final List<WirelessComponentInventoryWidget<? super WirelessReceiver>> receiverWidgets = new ArrayList<>();
    private final NamespacedKey widgetKey;

    public InventoryManager(@NotNull NamespacedKey widgetKey) {
        this.widgetKey = widgetKey;
    }

    public void addOpenInventory(@NotNull Player player, @NotNull CustomInventory inventory) {
        openInventories.put(player, inventory);
    }

    public void removeOpenInventory(@NotNull Player player) {
        openInventories.remove(player);
    }

    @Nullable
    public CustomInventory getOpenInventory(@NotNull Player player) {
        return openInventories.get(player);
    }

    public boolean addReceiverWidget(@NotNull WirelessComponentInventoryWidget<? super WirelessReceiver> widget) {
        return receiverWidgets.size() < 28 && receiverWidgets.add(widget);
    }

    public boolean addTransmitterWidget(@NotNull WirelessComponentInventoryWidget<? super WirelessTransmitter> widget) {
        return transmitterWidgets.size() < 28 && transmitterWidgets.add(widget);
    }

    public WirelessComponentInventory<WirelessTransmitter> createTransmitterInventory(@NotNull WirelessTransmitter transmitter) {
        return new WirelessComponentInventory<>(Lang.wirelessTransmitterInventoryTitle, transmitter, transmitterWidgets, this, widgetKey);
    }

    public WirelessComponentInventory<WirelessReceiver> createReceiverInventory(@NotNull WirelessReceiver receiver) {
        return new WirelessComponentInventory<>(Lang.wirelessReceiverInventoryTitle, receiver, receiverWidgets, this, widgetKey);
    }

    public void close() {
        for (Player player : new HashSet<>(openInventories.keySet())) {
            player.closeInventory();
        }
    }
}
