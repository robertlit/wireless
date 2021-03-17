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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.*;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents the API
 * <code>
 * Bukkit.getServicesManager().load(WirelessAPI.class);
 * </code>
 */
@SuppressWarnings("unused")
@ApiStatus.NonExtendable
public interface WirelessAPI {

    /**
     * Gets the components owned by the player with the given UUID
     * @param owner the UUID of the owner
     * @return the components owned by the player
     */
    @NotNull
    @UnmodifiableView
    @Contract(value = "_ -> new", pure = true)
    Collection<WirelessComponent> getComponentsOf(@NotNull UUID owner);

    /**
     * Gets the component located at the given location
     * @param location the location
     * @return the component located at the given location
     */
    @Nullable
    WirelessComponent getComponentAt(@NotNull Location location);

    @NotNull
    @UnmodifiableView
    @Contract(value = "-> new", pure = true)
    Collection<WirelessComponent> getAllComponents();

    /**
     * Gets the subscribers of the the given transmitter
     * @param transmitter the transmitter
     * @return the receivers subscribed to the given transmitter
     */
    @NotNull
    @UnmodifiableView
    @Contract(value = "_ -> new", pure = true)
    Collection<WirelessReceiver> getSubscribersOf(@NotNull WirelessTransmitter transmitter);

    /**
     * Registers a widget to be displayed in control inventories of wireless transmitters
     * @param widget the widget
     * @return whether or not the registration was successful, the maximum amount of widgets is 28
     */
    boolean registerTransmitterWidget(@NotNull WirelessComponentInventoryWidget<? super WirelessTransmitter> widget);

    /**
     * Registers a widget to be displayed in control inventories of wireless receivers
     * @param widget the widget
     * @return whether or not the registration was successful, the maximum amount of widgets is 28
     */
    boolean registerReceiverWidget(@NotNull WirelessComponentInventoryWidget<? super WirelessReceiver> widget);

    /**
     * Creates a new transmitter
     * <p>
     * Note: This method will not place a block, callers must ensure that
     * a block already exists at the given location
     * </p>
     * @param location the location of the transmitter
     * @param owner the location of the transmitter
     * @param itemMaterial the material of this transmitter's item
     * @return the created receiver or null if one of the following is true:
     * <ol>
     * <li>location is null</li>
     * <li>location's world is null</li>
     * <li>the block at the location is air</li>
     * <li>a component already exists at the location</li>
     * </ol>
     */
    @Nullable
    WirelessTransmitter createTransmitter(@NotNull Location location, @NotNull UUID owner, @NotNull Material itemMaterial);

    /**
     * Creates a new receiver
     * <p>
     * Note: This method will not place a block, callers must ensure that
     * a block already exists at the given location
     * </p>
     * @param location the location of the receiver
     * @param owner the location of the receiver
     * @param itemMaterial the material of this receiver's item
     * @return the created receiver or null if one of the following is true:
     * <ol>
     * <li>location is null</li>
     * <li>location's world is null</li>
     * <li>the block at the location is air</li>
     * <li>a component already exists at the location</li>
     * </ol>
     */
    @Nullable
    WirelessReceiver createReceiver(@NotNull Location location, @NotNull UUID owner, @NotNull Material itemMaterial);

    /**
     * Creates an item that may be placed to create a component
     * @param material the type of the item
     * @param clazz the type of the component, must be a sub-interface of WirelessComponent
     * @return the created item
     * @throws IllegalArgumentException if class is not of type WirelessTransmitter nor WirelessReceiver
     */
    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    ItemStack createItem(@NotNull Material material, @NotNull Class<? extends WirelessComponent> clazz);

    /**
     * Toggles whether components are highlighted to the given player
     * @param player the player to toggle highlight to
     */
    void toggleHighlight(@NotNull Player player);
}
