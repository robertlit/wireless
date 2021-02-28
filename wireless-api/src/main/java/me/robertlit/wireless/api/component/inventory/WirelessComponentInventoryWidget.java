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
package me.robertlit.wireless.api.component.inventory;

import me.robertlit.wireless.api.component.WirelessComponent;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a widget that can be added to a {@link WirelessComponent}'s control inventory.
 */
@ApiStatus.OverrideOnly
public interface WirelessComponentInventoryWidget<T extends WirelessComponent> {

    /**
     * This method should be overridden and return the item that should be displayed
     * in the control inventory of the given component.
     * <p>
     * This method is called every time the inventory is opened or this widget is clicked,
     * meaning that it supports different items based on the given component's current state.
     * </p>
     * @param component the component in whose control inventory this display item is currently acquired
     * @return the item to display in the given component's control inventory
     */
    @NotNull
    ItemStack getDisplayItem(@NotNull T component);

    /**
     * This method should be overridden and handle a click on this widgets display item.
     * <p>
     * <b>Opening external inventories should be delayed by a tick, using</b>
     * {@link org.bukkit.scheduler.BukkitScheduler#runTask(Plugin, Runnable)}
     * </p>
     * <p>
     * This method being called means that the clicked item is this widget's display item,
     * there is no need to check for that.
     * </p>
     * <p>
     * The method {@link InventoryClickEvent#setCurrentItem(ItemStack)} is not supported,
     * and will be overridden by the item returned from {@link #getDisplayItem(WirelessComponent)}.
     * </p>
     * <p>
     * Using the methods {@link org.bukkit.event.inventory.InventoryInteractEvent#setCancelled(boolean)}}
     * and {@link org.bukkit.event.inventory.InventoryInteractEvent#setResult(Event.Result)} has
     * no effect, the event will be cancelled regardless.
     * </p>
     * @param component the component in whose control inventory this widget's display item was clicked
     * @param event the event causing this method to be called
     */
    void handleClick(@NotNull T component, @NotNull InventoryClickEvent event);

    /**
     * This method should be overridden and return a constant unique identifier for this widget.
     * <p>
     * The recommended format is (plugin name)_(widget name) in lower case.
     * </p>
     * @return a constant unique identifier for this widget
     */
    @NotNull
    String getIdentifier();
}
