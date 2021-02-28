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
package me.robertlit.wireless.api.component;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a wireless component.
 */
@ApiStatus.NonExtendable
public interface WirelessComponent {

    /**
     * Gets the location of this component.
     * @return the location of this component
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    Location getLocation();

    /**
     * Gets the UUID of the owner.
     * @return the UUID of the owner
     */
    @NotNull
    UUID getOwner();

    /**
     * Checks if this component is valid
     * @return whether or not the component is valid
     */
    boolean isValid();

    /**
     * Called when a component is destroyed.
     * <p>
     * Calling this method will unregister the component, as if it was destroyed.
     * Plugins should generally not call this method.
     * </p>
     */
    void destroy();

    /**
     * Checks if a player can use this component
     * @param player the player
     * @return whether or not the player can use this component
     */
     boolean canUse(@NotNull Player player);

    /**
     * Gets the type of this component's item
     * @return the type of this component's item
     */
    @NotNull
    Material getItemType();

    /**
     * Gets the type of this component's block
     * @return the type of this component's block
     */
    @NotNull
    Material getBlockType();

    /**
     * Gets the unique id of this component
     * @return the unique id of this component
     */
    int getId();

    /**
     * Opens the control inventory of this component to the specified player
     * @param player the player that shall have the control inventory opened
     */
    void openInventory(@NotNull Player player);
}
