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
package me.robertlit.wireless.highlight;

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.component.WirelessComponentManager;
import me.robertlit.wireless.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighlightTask implements Runnable {

    private final UUID uuid;
    private final WirelessComponentManager manager;

    public HighlightTask(UUID uuid, WirelessComponentManager manager) {
        this.uuid = uuid;
        this.manager = manager;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        for (WirelessComponent component : manager.getComponentsOf(uuid)) {
            if (player.getLocation().distanceSquared(component.getLocation()) <= Settings.highlightsDistance * Settings.highlightsDistance) {
                highlight(component, player, Settings.highlightParticle);
            }
        }
    }

    private void highlight(WirelessComponent component, Player player, Particle particle) {
        Location location = component.getLocation();
        double minX = location.getX();
        double maxX = minX + 1;
        double minY = location.getY();
        double maxY = minY + 1;
        double minZ = location.getZ();
        double maxZ = minZ + 1;
        for (double x = minX; x <= maxX ; x += 0.25) {
            for (double y = minY; y <= maxY; y += 0.25) {
                for (double z = minZ; z <= maxZ; z += 0.25) {
                    boolean a = (x == minX || x == maxX);
                    boolean b = (y == minY || y == maxY);
                    boolean c = (z == minZ || z == maxZ);
                    if (a ? b || c : b && c) {
                        player.spawnParticle(particle, x, y, z, 1);
                    }
                }
            }
        }
    }

    public boolean reschedule() {
        return Bukkit.getPlayer(uuid) != null && !manager.getComponentsOf(uuid).isEmpty();
    }
}
