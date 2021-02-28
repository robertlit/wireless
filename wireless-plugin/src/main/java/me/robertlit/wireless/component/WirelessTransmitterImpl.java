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
package me.robertlit.wireless.component;

import me.robertlit.wireless.api.component.Signal;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.inventory.InventoryManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class WirelessTransmitterImpl extends AbstractWirelessComponent implements WirelessTransmitter {

    private Signal lastTransmission = Signal.OFF;

    public WirelessTransmitterImpl(@NotNull WirelessComponentManager manager, @NotNull InventoryManager inventoryManager, int id, @NotNull Location location, @NotNull UUID owner, @NotNull Material type, Material blockType) {
        super(manager, inventoryManager, id, location, owner, type, blockType);
    }

    @Override
    public void transmit(@NotNull Signal signal) {
        if (Objects.requireNonNull(signal, "signal can not be null") != lastTransmission) {
            manager.publish(this, signal);
            lastTransmission = signal;
        }
    }

    @Override
    public boolean isValid() {
        return !destroyed && location.getBlock().getType() == blockType;
    }

    @Override
    public void destroy() {
        super.destroy();
        manager.unsubscribeAllOf(this);
    }

    @Override
    @NotNull
    public Signal getLastTransmission() {
        return lastTransmission;
    }

    @Override
    public void openInventory(@NotNull Player player) {
        Objects.requireNonNull(player, "player can not be null");
        inventoryManager.createTransmitterInventory(this).open(player);
    }
}
