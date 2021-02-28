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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a wireless transmitter.
 */
@ApiStatus.NonExtendable
public interface WirelessTransmitter extends WirelessComponent {

    /**
     * Transmits the given signal,
     * all subscribed receivers will receive the signal
     * @param signal the signal to transmit
     */
    void transmit(@NotNull Signal signal);

    /**
     * Gets the last signal transmitted by this transmitter
     * @return the last transmitted signal
     */
    @NotNull
    Signal getLastTransmission();
}
