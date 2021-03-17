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

import me.robertlit.wireless.component.WirelessComponentManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HighlightExecutor extends BukkitRunnable {

    private final WirelessComponentManager manager;
    private final Map<UUID, HighlightTask> tasks = new ConcurrentHashMap<>();
    private final Collection<UUID> toStop = new ArrayList<>();

    public HighlightExecutor(WirelessComponentManager manager) {
        this.manager = manager;
    }

    public void toggle(UUID uuid) {
        if (tasks.remove(uuid) == null) {
            tasks.put(uuid, new HighlightTask(uuid, manager));
        }
    }

    @Override
    public void run() {
        tasks.forEach((uuid, task) -> {
            if (!task.reschedule()) {
                toStop.add(uuid);
                return;
            }
            task.run();
        });
        toStop.forEach(tasks::remove);
        toStop.clear();
    }
}
