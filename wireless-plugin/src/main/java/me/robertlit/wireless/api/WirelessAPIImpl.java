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
package me.robertlit.wireless.api;

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.api.component.inventory.WirelessComponentInventoryWidget;
import me.robertlit.wireless.component.WirelessComponentManager;
import me.robertlit.wireless.component.WirelessReceiverImpl;
import me.robertlit.wireless.component.WirelessTransmitterImpl;
import me.robertlit.wireless.inventory.InventoryManager;
import me.robertlit.wireless.settings.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class WirelessAPIImpl implements WirelessAPI {

    private final WirelessComponentManager manager;
    private final InventoryManager inventoryManager;

    public WirelessAPIImpl(@NotNull WirelessComponentManager manager, @NotNull InventoryManager inventoryManager) {
        this.manager = manager;
        this.inventoryManager = inventoryManager;
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull @UnmodifiableView Collection<WirelessComponent> getComponentsOf(@NotNull UUID owner) {
        return Collections.unmodifiableCollection(manager.getComponentsOf(Objects.requireNonNull(owner, "owner can not be null")));
    }

    @Override
    public @Nullable WirelessComponent getComponentAt(@NotNull Location location) {
        return manager.getComponentAt(Objects.requireNonNull(location, "location can not be null").getBlock().getLocation());
    }

    @Override
    @Contract(value = "-> new", pure = true)
    public @NotNull @UnmodifiableView Collection<WirelessComponent> getAllComponents() {
        return Collections.unmodifiableCollection(manager.getComponents());
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull @UnmodifiableView Collection<WirelessReceiver> getSubscribersOf(@NotNull WirelessTransmitter transmitter) {
        return Collections.unmodifiableCollection(manager.getSubscribersOf(Objects.requireNonNull(transmitter, "transmitter can't be null")));
    }

    @Override
    public boolean registerTransmitterWidget(@NotNull WirelessComponentInventoryWidget<? super WirelessTransmitter> widget) {
        return inventoryManager.addTransmitterWidget(Objects.requireNonNull(widget, "widget can not be null"));
    }

    @Override
    public boolean registerReceiverWidget(@NotNull WirelessComponentInventoryWidget<? super WirelessReceiver> widget) {
        return inventoryManager.addReceiverWidget(Objects.requireNonNull(widget, "widget can not be null"));
    }

    @Override
    public @Nullable WirelessTransmitter createTransmitter(@NotNull Location location, @NotNull UUID owner, @NotNull Material itemMaterial) {
        if (invalidLocation(location)) {
            return null;
        }
        WirelessTransmitter transmitter = new WirelessTransmitterImpl(manager, inventoryManager, manager.createId(), location, Objects.requireNonNull(owner, "owner can not be null"), itemMaterial, location.getBlock().getType());
        manager.registerComponent(transmitter, true);
        return transmitter;
    }

    @Override
    public @Nullable WirelessReceiver createReceiver(@NotNull Location location, @NotNull UUID owner, @NotNull Material itemMaterial) {
        if (invalidLocation(location)) {
            return null;
        }
        WirelessReceiver receiver = new WirelessReceiverImpl(manager, inventoryManager, manager.createId(), location, Objects.requireNonNull(owner, "owner can not be null"), itemMaterial, location.getBlock().getType());
        manager.registerComponent(receiver, true);
        return receiver;
    }

    private boolean invalidLocation(Location location) {
        return location == null || location.getWorld() == null || location.getBlock().getType().isAir() || manager.getComponentAt(location) != null;
    }

    @Override
    public @NotNull ItemStack createItem(@NotNull Material material, @NotNull Class<? extends WirelessComponent> clazz) {
        return Items.createItem(material, clazz);
    }
}
