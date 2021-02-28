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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import me.robertlit.wireless.api.component.Signal;
import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.data.Database;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutorService;

public final class WirelessComponentManager {

    private final ExecutorService executor;
    private final Database database;

    private final ListMultimap<UUID, WirelessComponent> byOwnerUuid = ArrayListMultimap.create(); // TODO is arraylist the right choice?
    private final Map<Location, WirelessComponent> byLocation = new HashMap<>();
//    private final Map<Integer, WirelessComponent> byId = new HashMap<>();
    private final SetMultimap<WirelessTransmitter, WirelessReceiver> subscriptions = HashMultimap.create();

    private int nextId;

    public WirelessComponentManager(@NotNull ExecutorService executor, @NotNull Database database) {
        this.executor = executor;
        this.database = database;
        this.nextId = database.loadNextId();
    }

    public void registerComponent(@NotNull WirelessComponent component, boolean save) {
        byOwnerUuid.put(component.getOwner(), component);
        byLocation.put(component.getLocation(), component);
//        byId.put(component.getId(), component);
        if (save) {
            saveComponent(component);
        }
    }

    public void unregisterComponent(@NotNull WirelessComponent component, boolean delete) {
        byOwnerUuid.remove(component.getOwner(), component);
        byLocation.remove(component.getLocation());
//        byId.remove(component.getId());
        if (delete) {
            deleteComponent(component);
        }
    }

    @NotNull
    public List<WirelessComponent> getComponentsOf(@NotNull UUID uuid) {
        return byOwnerUuid.get(uuid);
    }

    @Nullable
    public WirelessComponent getComponentAt(@NotNull Location location) {
        return byLocation.get(location);
    }

    void addSubscription(@NotNull WirelessTransmitter transmitter, @NotNull WirelessReceiver receiver, boolean save) {
        subscriptions.put(transmitter, receiver);
        if (save) {
            saveSubscription(receiver);
        }
    }

    void removeSubscription(@NotNull WirelessTransmitter transmitter, @NotNull WirelessReceiver receiver) {
        subscriptions.remove(transmitter, receiver);
        deleteSubscription(receiver);
    }

    void unsubscribeAllOf(@NotNull WirelessTransmitter transmitter) {
        new ArrayList<>(getSubscribersOf(transmitter)).forEach(WirelessReceiver::unsubscribe);
        subscriptions.removeAll(transmitter);
    }

    @NotNull
    public Set<WirelessReceiver> getSubscribersOf(@NotNull WirelessTransmitter transmitter) {
        return subscriptions.get(transmitter);
    }

    void publish(@NotNull WirelessTransmitter transmitter, @NotNull Signal signal) {
        getSubscribersOf(transmitter).forEach(receiver -> receiver.receive(signal));
    }

    @NotNull
    public Collection<WirelessComponent> getComponents() {
        return byLocation.values();
    }

    private void saveComponent(@NotNull WirelessComponent component) {
        executor.execute(() -> database.saveComponent(component));
    }

    private void saveSubscription(@NotNull WirelessReceiver receiver) {
        executor.execute(() -> database.saveSubscription(receiver));
    }

    private void deleteComponent(@NotNull WirelessComponent component) {
        executor.execute(() -> database.deleteComponent(component));
    }

    private void deleteSubscription(@NotNull WirelessReceiver receiver) {
        executor.execute(() -> database.deleteSubscription(receiver));
    }

    public int createId() {
        return nextId++;
    }

    public void unregisterComponentsIn(@NotNull World world) {
        new ArrayList<>(getComponents()).forEach(component -> {
            if (component.getLocation().getWorld() == world) {
                this.unregisterComponent(component, false);
            }
        });
    }
}
