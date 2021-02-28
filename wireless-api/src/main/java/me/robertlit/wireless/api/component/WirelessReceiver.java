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
import org.jetbrains.annotations.Nullable;

/**
 * Represents a wireless receiver.
 */
@ApiStatus.NonExtendable
public interface WirelessReceiver extends WirelessComponent {

    /**
     * Receives a signal
     * @param signal the signal to receive
     */
    void receive(@NotNull Signal signal);

    /**
     * Subscribes to a transmitter,
     * meaning that this receiver will receive signals transmitted by the given transmitter,
     * until unsubscribed
     * @param subscription the transmitter to subscribe to
     */
    void subscribe(@NotNull WirelessTransmitter subscription);

    /**
     * Gets the transmitter that this receiver is subscribed to
     * @return the transmitter that this receiver is subscribed to
     */
    @Nullable
    WirelessTransmitter getSubscription();

    /**
     * Unsubscribes from the current subscription
     */
    void unsubscribe();
}
