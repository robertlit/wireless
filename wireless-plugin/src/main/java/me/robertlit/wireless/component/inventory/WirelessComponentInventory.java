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
package me.robertlit.wireless.component.inventory;

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.inventory.WirelessComponentInventoryWidget;
import me.robertlit.wireless.inventory.CustomInventory;
import me.robertlit.wireless.inventory.InventoryManager;
import me.robertlit.wireless.settings.Items;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WirelessComponentInventory<T extends WirelessComponent> implements CustomInventory {

    private final T component;
    private final Inventory inventory;

    private final Map<String, WirelessComponentInventoryWidget<? super T>> widgets = new LinkedHashMap<>();

    private final InventoryManager manager;
    private final NamespacedKey widgetKey;

    public WirelessComponentInventory(@NotNull String title, @NotNull T component, @NotNull List<WirelessComponentInventoryWidget<? super T>> widgets, @NotNull InventoryManager manager, @NotNull NamespacedKey widgetKey) {
        this.component = component;
        this.manager = manager;
        this.widgetKey = widgetKey;
        List<ItemStack> items = computeWidgets(widgets);
        inventory = Bukkit.createInventory(null, calculateSize(), title);
        setupItems(items, true);
    }

    @NotNull
    private List<ItemStack> computeWidgets(@NotNull Collection<WirelessComponentInventoryWidget<? super T>> widgets) {
        List<ItemStack> items = new ArrayList<>();
        for (WirelessComponentInventoryWidget<? super T> widget : widgets) {
            ItemStack item = this.createWidgetItem(widget);
            if (item != null && !this.widgets.containsKey(widget.getClass().getName())) {
                items.add(item);
                this.widgets.put(widget.getClass().getName(), widget);
            }
        }
        return items; // return the items to avoid iterating through the widgets two times
    }

    private void setupItems(@NotNull Collection<@NotNull ItemStack> items, boolean initial) {
        int size = inventory.getSize();
        if (initial) {
            for (int i = 0; i < size; i++) {
                if (i < 9 || i > size - 10 || i % 9 == 0 || (i + 1) % 9 == 0) {
                    inventory.setItem(i, Items.guiBackground);
                }
            }
        }

        int i = 10;
        for (ItemStack item : items) {
            if (i > size - 11) {
                break;
            }
            if ((i + 1) % 9 == 0) { //Either i - 8 or i + 1
                i++;
            }
            if (i % 9 == 0) {
                i++;
            }
            inventory.setItem(i++, item);
        }
    }

    private int calculateSize() {
        // 7 items in each row. 9 is the size of a row. Subtract 1 because we don't want to add a row if it's exactly seven 7, only if it's more than 7
        int size = 27 + ((widgets.size() - 1) / 7) * 9;
        return Math.min(size, 54);
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
        manager.addOpenInventory(player, this);
    }

    @Override
    public void handleClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return;
        }
        ItemMeta meta = current.getItemMeta();
        if (meta == null) {
            return;
        }
        WirelessComponentInventoryWidget<? super T> widget = widgets.get(meta.getPersistentDataContainer().get(widgetKey, PersistentDataType.STRING));
        if (widget != null) {
            widget.handleClick(component, event);
            setupItems(widgets.values().stream().map(this::createWidgetItem).filter(Objects::nonNull).collect(Collectors.toList()), false);
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        manager.removeOpenInventory(player);
    }

    @Nullable
    private ItemStack createWidgetItem(@NotNull WirelessComponentInventoryWidget<? super T> widget) {
        String identifier = widget.getClass().getName();
        ItemStack item = widget.getDisplayItem(component);
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        meta.getPersistentDataContainer().set(widgetKey, PersistentDataType.STRING, identifier);
        item.setItemMeta(meta);
        return item;
    }
}
