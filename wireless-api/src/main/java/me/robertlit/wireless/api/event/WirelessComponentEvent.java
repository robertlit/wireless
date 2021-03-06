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
package me.robertlit.wireless.api.event;

import me.robertlit.wireless.api.component.WirelessComponent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event involving a WirelessComponent.
 */
public abstract class WirelessComponentEvent extends Event {

    private final WirelessComponent component;

    public WirelessComponentEvent(WirelessComponent component) {
        this.component = component;
    }

    /**
     * Gets the WirelessComponent involved in this event.
     * @return The WirelessComponent involved in this event
     */
    @NotNull
    public WirelessComponent getComponent() {
        return component;
    }
}
