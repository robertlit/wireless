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
package me.robertlit.wireless.data;

import me.robertlit.wireless.api.component.WirelessComponent;
import me.robertlit.wireless.api.component.WirelessReceiver;
import me.robertlit.wireless.data.object.WirelessComponentInfo;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public abstract class Database {

    private final String type;
    private final String path;

    protected Database(String type, String path) throws SQLException {
        this.type = type;
        this.path = path;
        setup();
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:%s://%s", type, path));
    }

    protected byte[] convertIdToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    protected UUID convertBytesToId(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.put(bytes);
        buffer.flip();
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    protected abstract void setup() throws SQLException;

    public abstract void saveComponent(@NotNull WirelessComponent component);

    public abstract void saveSubscription(@NotNull WirelessReceiver receiver);

    public abstract void deleteComponent(@NotNull WirelessComponent component);

    public abstract void deleteSubscription(@NotNull WirelessReceiver receiver);

    @NotNull
    public abstract Set<WirelessComponentInfo> loadComponents(@NotNull World world);

    public abstract int loadNextId();
}
