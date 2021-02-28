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
package me.robertlit.wireless.listener;

import me.robertlit.wireless.component.WirelessComponentManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.function.Consumer;

public class CacheControlListener implements Listener {

    private final WirelessComponentManager manager;
//    private final Function<World, CompletableFuture<Void>> loader;
    private final Consumer<World> loader;

    public CacheControlListener(WirelessComponentManager manager, Consumer<World> loader) {
        this.manager = manager;
        this.loader = loader;
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        loader.accept(event.getWorld());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onUnload(WorldUnloadEvent event) {
        manager.unregisterComponentsIn(event.getWorld());
    }
}
