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
package me.robertlit.wireless;

import me.robertlit.wireless.api.WirelessAPI;
import me.robertlit.wireless.api.WirelessAPIImpl;
import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.command.WirelessCommandExecutor;
import me.robertlit.wireless.command.WirelessGiveCommand;
import me.robertlit.wireless.component.WirelessComponentManager;
import me.robertlit.wireless.component.WirelessComponentUpdateTask;
import me.robertlit.wireless.component.WirelessReceiverImpl;
import me.robertlit.wireless.component.WirelessTransmitterImpl;
import me.robertlit.wireless.inventory.InventoryManager;
import me.robertlit.wireless.data.Database;
import me.robertlit.wireless.data.SQLite;
import me.robertlit.wireless.data.object.WirelessComponentInfo;
import me.robertlit.wireless.listener.CacheControlListener;
import me.robertlit.wireless.listener.InventoryListener;
import me.robertlit.wireless.listener.WirelessComponentListener;
import me.robertlit.wireless.settings.ConfigLoader;
import me.robertlit.wireless.settings.Constants;
import me.robertlit.wireless.settings.Items;
import me.robertlit.wireless.widget.ConnectReceiversWidget;
import me.robertlit.wireless.widget.ConnectTransmitterWidget;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class WirelessPlugin extends JavaPlugin {

    private WirelessComponentManager manager;
    private InventoryManager inventoryManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Database database;

    @Override
    public void onEnable() {
        super.saveDefaultConfig();
        new ConfigLoader().load(super.getConfig());

        PluginManager pluginManager = Bukkit.getPluginManager();

        try {
            File databaseFile = new File(super.getDataFolder(), "data.db");
            databaseFile.createNewFile();
            this.database = new SQLite(databaseFile);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            getLogger().severe("Could not create database. Disabling...");
            pluginManager.disablePlugin(this);
            return;

        }

        this.manager = new WirelessComponentManager(executor, database);
        this.inventoryManager = new InventoryManager(new NamespacedKey(this, Constants.WIDGET));

        Bukkit.getWorlds().forEach(this::loadComponents);

        NamespacedKey componentKey = new NamespacedKey(this, Constants.COMPONENT);
        Items.setKey(componentKey);

        inventoryManager.addReceiverWidget(new ConnectTransmitterWidget(manager, inventoryManager, this));
        inventoryManager.addTransmitterWidget(new ConnectReceiversWidget(manager, inventoryManager, this));

        pluginManager.registerEvents(new WirelessComponentListener(manager, inventoryManager, componentKey), this);
        pluginManager.registerEvents(new InventoryListener(inventoryManager), this);
        pluginManager.registerEvents(new CacheControlListener(this.manager, this::loadComponents), this);

        new WirelessComponentUpdateTask(this.manager).runTaskTimer(this, 0, 1);

        WirelessCommandExecutor commandExecutor = new WirelessCommandExecutor();
        commandExecutor.addCommand(new WirelessGiveCommand());
        PluginCommand command = super.getCommand("wireless");
        if (command != null) {
            command.setExecutor(commandExecutor);
        }

        Bukkit.getServicesManager().register(WirelessAPI.class, new WirelessAPIImpl(manager, inventoryManager), this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        inventoryManager.close();
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            getLogger().severe("Interrupted while saving data.");
        }
    }

    private void loadComponents(@NotNull World world) {
        Map<Integer, WirelessComponent> byId = new HashMap<>();
        Set<WirelessComponentInfo> infoSet = database.loadComponents(world);
        infoSet.forEach(info -> {
            WirelessComponent component;
            if (info.getType().equals(Constants.TRANSMITTER)) {
                component = new WirelessTransmitterImpl(this.manager, this.inventoryManager, info.getId(), info.getLocation(), info.getOwner(), info.getItemMaterial(), info.getBlockMaterial());
            } else {
                component = new WirelessReceiverImpl(this.manager, this.inventoryManager, info.getId(), info.getLocation(), info.getOwner(), info.getItemMaterial(), info.getBlockMaterial());
            }
            manager.registerComponent(component, false);
            byId.put(component.getId(), component);
        });
        infoSet.forEach(info -> {
            WirelessComponent component = byId.get(info.getId());
            if (component instanceof WirelessReceiver) {
                Integer subscriptionId = info.getSubscription();
                if (subscriptionId != null) {
                    WirelessComponent subscription = byId.get(subscriptionId);
                    if (subscription instanceof WirelessTransmitter) {
                        ((WirelessReceiverImpl) component).subscribe((WirelessTransmitter) subscription, false);
                    }
                }
            }
        });
    }
}
