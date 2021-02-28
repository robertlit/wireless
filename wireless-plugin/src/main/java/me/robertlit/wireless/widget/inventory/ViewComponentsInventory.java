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
package me.robertlit.wireless.widget.inventory;

import com.google.common.collect.Lists;
import me.robertlit.wireless.WirelessPlugin;
import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.inventory.CustomInventory;
import me.robertlit.wireless.inventory.InventoryManager;
import me.robertlit.wireless.settings.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

//S - showing
public class ViewComponentsInventory<S extends WirelessComponent> implements CustomInventory {

    private final List<List<S>> components;
    private final List<Inventory> inventories = new ArrayList<>();

    private final Consumer<S> clickHandler;
    private final Function<S, String> lastLine;
    private final Consumer<Player> goBackHandler;
    private final boolean goBackOnSuccess;
    private final String title;

    private final InventoryManager inventoryManager;
    private final WirelessPlugin plugin;

    public ViewComponentsInventory(@NotNull List<S> components, @NotNull Function<S, String> lastLine, @NotNull Consumer<S> clickHandler, @NotNull Consumer<Player> goBackHandler, boolean goBackOnSuccess, @NotNull String title, @NotNull InventoryManager inventoryManager, @NotNull WirelessPlugin plugin) {
        this.goBackHandler = goBackHandler;
        this.goBackOnSuccess = goBackOnSuccess;
        this.title = title;
        this.inventoryManager = inventoryManager;
        this.components = Lists.partition(components, 45);
        this.clickHandler = clickHandler;
        this.lastLine = lastLine;
        this.plugin = plugin;
        setup(lastLine);
    }

    private void setup(@NotNull Function<S, String> lastLine) {
        int size = components.size();
        for (int i = 0; i < size; i++) {
            Inventory inventory = Bukkit.createInventory(null, 54, title.replace("%current_page%", Integer.toString(i + 1)).replace("%total_pages%", Integer.toString(size)));
            for (int j = 45; j < 54; j++) {
                inventory.setItem(j, Items.guiBackground);
            }
            inventory.setItem(49, Items.goBack);
            if (size > 1 && i != size - 1) {
                inventory.setItem(50, Items.nextPage);
            }
            if (size > 1 && i != 0) {
                inventory.setItem(48, Items.previousPage);
            }
            List<S> currentComponents = components.get(i);
            for (int w = 0; w < currentComponents.size(); w++) {
                S current = currentComponents.get(w);
                inventory.setItem(w, Items.createDisplayItem(current, lastLine.apply(current)));
            }
            inventories.add(inventory);
        }
        if (inventories.isEmpty()) {
            Inventory inventory = Bukkit.createInventory(null, 54, title.replace("%current_page%", Integer.toString(1)).replace("%total_pages%", Integer.toString(1)));
            for (int j = 45; j < 54; j++) {
                inventory.setItem(j, Items.guiBackground);
            }
            inventory.setItem(49, Items.goBack);
            inventories.add(inventory);
        }
    }

    private int currentPage = 0;

    public void open(@NotNull Player player) {
        player.openInventory(inventories.get(currentPage));
        inventoryManager.addOpenInventory(player, this);
    }

    public void next(@NotNull Player player) {
        currentPage++;
        open(player);
    }

    public void previous(@NotNull Player player) {
        currentPage--;
        open(player);
    }

    @Override
    public void handleClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.isSimilar(Items.guiBackground) || item.isSimilar(Items.noLongerValid)) {
            return;
        }
        if (item.isSimilar(Items.nextPage)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> next(player));
        } else if (item.isSimilar(Items.previousPage)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> previous(player));
        } else if (item.isSimilar(Items.goBack)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> goBackHandler.accept(player));
        } else {
            S clicked = components.get(currentPage).get(event.getSlot());
            if (!clicked.isValid()) { // if the component has been removed while inventory is open
                event.setCurrentItem(Items.noLongerValid);
                return;
            }
            clickHandler.accept(clicked);
            if (goBackOnSuccess) {
                goBackHandler.accept(player);
                return;
            }
            event.setCurrentItem(Items.createDisplayItem(clicked, lastLine.apply(clicked))); // update current item
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        inventoryManager.removeOpenInventory(player);
    }
}
