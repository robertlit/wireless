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

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.event.WirelessComponentDestroyEvent;
import me.robertlit.wireless.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/* package */ abstract class AbstractWirelessComponent implements WirelessComponent {

    protected final WirelessComponentManager manager;
    protected final InventoryManager inventoryManager;
    private final int id;
    protected final Location location;
    protected final UUID owner;
    private final Material type;
    protected final Material blockType;
    protected boolean destroyed = false;

    protected AbstractWirelessComponent(@NotNull WirelessComponentManager manager, @NotNull InventoryManager inventoryManager, int id, @NotNull Location location, @NotNull UUID owner, @NotNull Material type, Material blockType) {
        this.manager = manager;
        this.inventoryManager = inventoryManager;
        this.id = id;
        this.location = location;
        this.owner = owner;
        this.type = type;
        this.blockType = blockType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    @NotNull
    @Contract(value = "-> new", pure = true)
    public Location getLocation() {
        return location.clone();
    }

    @Override
    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @Override
    @NotNull
    public Material getItemType() {
        return type;
    }

    @Override
    public @NotNull Material getBlockType() {
        return blockType;
    }

    @Override
    public boolean canUse(@NotNull Player player) {
        return owner.equals(Objects.requireNonNull(player, "player can not be null").getUniqueId()) || player.hasPermission("wireless.bypass");
    }

    @MustBeInvokedByOverriders
    @Override
    public void destroy() {
        manager.unregisterComponent(this, true);
        Bukkit.getPluginManager().callEvent(new WirelessComponentDestroyEvent(this));
        this.destroyed = true;
    }
}
