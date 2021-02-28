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

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.event.WirelessComponentCreateEvent;
import me.robertlit.wireless.component.WirelessComponentManager;
import me.robertlit.wireless.component.WirelessReceiverImpl;
import me.robertlit.wireless.component.WirelessTransmitterImpl;
import me.robertlit.wireless.inventory.InventoryManager;
import me.robertlit.wireless.settings.Constants;
import me.robertlit.wireless.settings.Items;
import me.robertlit.wireless.settings.Lang;
import me.robertlit.wireless.settings.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class WirelessComponentListener implements Listener {
    
    private final WirelessComponentManager manager;
    private final InventoryManager inventoryManager;
    private final NamespacedKey key;

    public WirelessComponentListener(WirelessComponentManager manager, InventoryManager inventoryManager, NamespacedKey key) {
        this.manager = manager;
        this.inventoryManager = inventoryManager;
        this.key = key;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        this.invalidateComponent(block); // Should be done by task - just to make sure

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String type = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (type == null) {
            return;
        }
        Player player = event.getPlayer();
        UUID owner = player.getUniqueId();
        if (manager.getComponentsOf(owner).size() >= Settings.maxPlayerComponents) {
            event.setCancelled(true);
            player.sendMessage(Lang.tooManyComponents);
            return;
        }
        WirelessComponent component;
        if (type.equals(Constants.RECEIVER)) {
            component = new WirelessReceiverImpl(manager, inventoryManager, manager.createId(), block.getLocation(), owner, item.getType(), block.getType());
        } else if (type.equals(Constants.TRANSMITTER)) {
            component = new WirelessTransmitterImpl(manager, inventoryManager, manager.createId(), block.getLocation(), owner, item.getType(), block.getType());
        } else {
            return;
        }
        manager.registerComponent(component, true);
        Bukkit.getPluginManager().callEvent(new WirelessComponentCreateEvent(component));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(BlockDropItemEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        WirelessComponent component = manager.getComponentAt(location);
        if (component == null) {
            return;
        }
        for (Item item : event.getItems()) {
            if (item.getItemStack().getType() == component.getItemType()) {
                item.setItemStack(Items.createItem(component));
                break;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        WirelessComponent component = manager.getComponentAt(block.getLocation());
        if (component == null) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !player.isSneaking()) {
            return;
        }
        if (!component.canUse(event.getPlayer())) {
            player.sendMessage(Lang.disallowed);
            return;
        }
        component.openInventory(player);
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        this.cancelPistonMovement(event, event.getBlock(), event.getBlocks(), Sound.BLOCK_PISTON_EXTEND);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        this.cancelPistonMovement(event, event.getBlock(), event.getBlocks(), Sound.BLOCK_PISTON_CONTRACT);
    }

    // Original - https://github.com/2008Choco/LockSecurity/blob/master/LockSecurity-Plugin/src/main/java/wtf/choco/locksecurity/listener/LockedBlockProtectionListener.java#L227
    private void cancelPistonMovement(Cancellable event, Block piston, List<Block> blocks, Sound sound) {
        for (Block block : blocks) {
            if (manager.getComponentAt(block.getLocation()) != null) {
                World world = piston.getWorld();
                Location pistonLocation = piston.getLocation();

                world.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1.2, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
                world.playSound(pistonLocation, Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.25F);
                world.playSound(pistonLocation, sound, 1.0F, 1.75F);

                event.setCancelled(true);
                break;
            }
        }
    }

    // PROTECTION

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        WirelessComponent component = manager.getComponentAt(event.getBlock().getLocation());
        if (component == null) {
            return;
        }
        if (Settings.protectBreak && !component.canUse(event.getPlayer())) {
            event.getPlayer().sendMessage(Lang.disallowed);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        if (Settings.protectExplosion) {
            event.blockList().removeIf(block -> manager.getComponentAt(block.getLocation()) != null);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (Settings.protectExplosion) {
            event.blockList().removeIf(block -> manager.getComponentAt(block.getLocation()) != null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        if (Settings.protectBurn && manager.getComponentAt(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        if (Settings.protectBreak && manager.getComponentAt(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    private void invalidateComponent(@NotNull Block block) {
        WirelessComponent component = manager.getComponentAt(block.getLocation());
        if (component != null) {
            component.destroy();
        }
    }

// Leaving these here...

//    private final BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST};
//    @EventHandler
//    public void onDropItem(BlockDropItemEvent event) {
//        Block block = event.getBlock();
//        List<Item> items = event.getItems();
//        List<Item> itemsCopy = new ArrayList<>(items);
//        for (BlockFace face : faces) {
//            WirelessComponent component = this.manager.getComponentAt(block.getRelative(face).getLocation());
//            if (component == null) {
//                continue;
//            }
//            for (Item item : itemsCopy) {
//                if (item.getItemStack().getType() == component.getItemType()) {
//                    items.remove(item);
//                    block.getWorld().dropItemNaturally(item.getLocation(), Items.createItem(component));
//                    break;
//                }
//            }
//        }
//        WirelessComponent component = manager.getComponentAt(location);
//        if (component != null) {
//            block.getWorld().dropItemNaturally(location, Items.createItem(component));
//            event.getItems().removeIf(item -> item.getItemStack().getType() == component.getItemType());
//            for (Item item : event.getItems()) {
//                if (item.getItemStack().getType() == component.getItemType()) {
//                    item.setItemStack(Items.createItem(component));
//                    break;
//                }
//            }
//    }

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onPhysics(BlockPhysicsEvent event) {
//        Block block = event.getBlock();
//        WirelessComponent component = manager.getComponentAt(block.getLocation());
//        if (component instanceof WirelessTransmitter) {
//            BlockData data = block.getBlockData();
//            boolean power1 = data instanceof Powerable && ((Powerable) data).isPowered();
//            boolean power2 = data instanceof AnaloguePowerable && ((AnaloguePowerable) data).getPower() > 0;
//            boolean power3 = block.isBlockPowered();
//            Signal signal = power1 || power2 || power3 ? Signal.ON : Signal.OFF;
//            ((WirelessTransmitter) component).transmit(signal);
//        }
//    }
}
