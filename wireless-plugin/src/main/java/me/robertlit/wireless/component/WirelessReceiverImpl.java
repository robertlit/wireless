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
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.inventory.InventoryManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class WirelessReceiverImpl extends AbstractWirelessComponent implements WirelessReceiver {

    private WirelessTransmitter subscription;
    private final boolean isSpecialBlockData;

    public WirelessReceiverImpl(@NotNull WirelessComponentManager manager, @NotNull InventoryManager inventoryManager, int id, @NotNull Location location, @NotNull UUID owner, @NotNull Material type, Material blockType) {
        super(manager, inventoryManager, id, location, owner, type, blockType);
        BlockData data = blockType.createBlockData();
        this.isSpecialBlockData = data instanceof Powerable
                || data instanceof AnaloguePowerable
                || data instanceof Dispenser
                || data instanceof Openable
                || data instanceof Hopper
                || data instanceof Lightable
                || data instanceof Piston;
    }

    @Override
    public void receive(@NotNull Signal signal) {
        BlockData data = location.getBlock().getBlockData();
        switch (Objects.requireNonNull(signal, "signal can not be null")) {
            case OFF:
                if (data instanceof Dispenser) {
                    ((Dispenser) data).setTriggered(false);
                } else if (data instanceof Openable) {
                    ((Openable) data).setOpen(false);
                } else if (data instanceof Hopper) {
                    ((Hopper) data).setEnabled(false);
                } else if (data instanceof Lightable) {
                    ((Lightable) data).setLit(false);
                } else if (data instanceof Piston) {
                    ((Piston) data).setExtended(false);
                } else if (data instanceof Powerable) {
                    ((Powerable) data).setPowered(false);
                } else if (data instanceof AnaloguePowerable) {
                    ((AnaloguePowerable) data).setPower(0);
                } else {
                    location.getBlock().setType(blockType);
                    break;
                }
                location.getBlock().setBlockData(data);
                break;
            case ON:
                if (data instanceof Dispenser) {
                    ((Dispenser) data).setTriggered(true);
                } else if (data instanceof Openable) {
                    ((Openable) data).setOpen(true);
                } else if (data instanceof Hopper) {
                    ((Hopper) data).setEnabled(true);
                } else if (data instanceof Lightable) {
                    ((Lightable) data).setLit(true);
                } else if (data instanceof Piston) {
                    ((Piston) data).setExtended(true);
                } else if (data instanceof Powerable) {
                    ((Powerable) data).setPowered(true);
                } else if (data instanceof AnaloguePowerable) {
                    AnaloguePowerable analoguePowerable = (AnaloguePowerable) data;
                    analoguePowerable.setPower(analoguePowerable.getMaximumPower());
                } else {
                    location.getBlock().setType(Material.REDSTONE_BLOCK);
                    break;
                }
                location.getBlock().setBlockData(data);
                break;
        }
    }

    @Override
    public void subscribe(@NotNull WirelessTransmitter subscription) {
        Objects.requireNonNull(subscription, "subscription can not be null");
        if (this.subscription != null) {
            return;
        }
        if (subscription.getLocation().getWorld() != this.location.getWorld()) {
            return;
        }
        this.subscribe(subscription, true);
    }

    public void subscribe(@NotNull WirelessTransmitter subscription, boolean save) {
        this.subscription = subscription;
        manager.addSubscription(subscription, this, save);
        receive(subscription.getLastTransmission());
    }

    @Override
    public WirelessTransmitter getSubscription() {
        return subscription;
    }

    @Override
    public void unsubscribe() {
        this.unsubscribe(true);
    }

    private void unsubscribe(boolean receiveOff) {
        if (subscription != null) {
            manager.removeSubscription(subscription, this);
            subscription = null;
            if (receiveOff) {
                this.receive(Signal.OFF);
            }
        }
    }

    @Override
    public boolean isValid() {
        if (destroyed) {
            return false;
        }
        Block block = location.getBlock();
        if (isSpecialBlockData || subscription == null || subscription.getLastTransmission() == Signal.OFF) {
            return block.getType() == blockType;
        }
        return block.getType() == Material.REDSTONE_BLOCK;
    }

    @Override
    public void openInventory(@NotNull Player player) {
        Objects.requireNonNull(player, "player can not be null");
        inventoryManager.createReceiverInventory(this).open(player);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.unsubscribe(false);
    }
}
