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
import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;

public class WirelessComponentUpdateTask extends BukkitRunnable {

    private final WirelessComponentManager manager;
    private final Collection<WirelessComponent> unloadedWorld = new ArrayList<>();
    private final Collection<WirelessComponent> invalid = new ArrayList<>();

    public WirelessComponentUpdateTask(WirelessComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        if (!this.unloadedWorld.isEmpty()) {
            this.unloadedWorld.forEach(component -> this.manager.unregisterComponent(component, false));
            this.unloadedWorld.clear();
        }
        if (!this.invalid.isEmpty()) {
            this.invalid.forEach(WirelessComponent::destroy);
            this.invalid.clear();
        }
        Collection<WirelessComponent> components = this.manager.getComponents();
        if (components.isEmpty()) {
            return;
        }
        for (WirelessComponent component : components) {
            Location location = component.getLocation();
            World world = location.getWorld();
            if (world == null) {
                this.unloadedWorld.add(component);
                continue;
            }
            if (!world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                continue;
            }
            if (!component.isValid()) {
                invalid.add(component); // no concurrent modification
                continue;
            }
            if (component instanceof WirelessTransmitter) {
                Block block = location.getBlock();
                BlockData data = block.getBlockData();
                boolean power1 = data instanceof Powerable && ((Powerable) data).isPowered();
                boolean power2 = data instanceof AnaloguePowerable && ((AnaloguePowerable) data).getPower() > 0;
                boolean power3 = block.isBlockPowered();
                Signal signal = power1 || power2 || power3 ? Signal.ON : Signal.OFF;
                ((WirelessTransmitter) component).transmit(signal); // the impl guarantees no "spamming"
            }
        }
    }
}
