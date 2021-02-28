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
package me.robertlit.wireless.data.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WirelessComponentInfo {

    private final int id;
    private final Location location;
    private final UUID owner;
    private final Material itemMaterial;
    private final Material blockMaterial;
    private final String type;
//    private final Signal lastTransmission;
    private final Integer subscription;

    public WirelessComponentInfo(int id, @NotNull Location location, @NotNull UUID owner, @NotNull Material itemMaterial, @NotNull Material blockMaterial, @NotNull String type, @Nullable Integer subscription) {
        this.id = id;
        this.location = location;
        this.owner = owner;
        this.itemMaterial = itemMaterial;
        this.blockMaterial = blockMaterial;
        this.type = type;
//        this.lastTransmission = lastTransmission;
        this.subscription = subscription;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @NotNull
    public Material getItemMaterial() {
        return itemMaterial;
    }

    @NotNull
    public Material getBlockMaterial() {
        return blockMaterial;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @Nullable
    public Integer getSubscription() {
        return subscription;
    }
}
