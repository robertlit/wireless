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
import me.robertlit.wireless.api.component.WirelessTransmitter;
import me.robertlit.wireless.data.object.WirelessComponentInfo;
import me.robertlit.wireless.settings.Constants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SQLite extends Database {

    public SQLite(File databaseFile) throws SQLException {
        super("sqlite", databaseFile.getAbsolutePath());
    }

    private static final String CREATE_COMPONENTS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS components (" +
            "id INT NOT NULL PRIMARY KEY," +
            "x INT NOT NULL," +
            "y INT NOT NULL," +
            "z INT NOT NULL," +
            "worldId BLOB NOT NULL," +
            "owner BLOB NOT NULL," +
            "itemMaterial TEXT NOT NULL," +
            "blockMaterial TEXT NOT NULL," +
            "type TEXT NOT NULL," +
            "UNIQUE(x, y, z, worldId)" +
            ");";

    private static final String CREATE_SUBSCRIPTIONS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS subscriptions (" +
            "receiver INT NOT NULL UNIQUE," +
            "transmitter INT NOT NULL" +
            ");";

    @Override
    protected void setup() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.addBatch(CREATE_COMPONENTS_TABLE_QUERY);
            statement.addBatch(CREATE_SUBSCRIPTIONS_TABLE_QUERY);
            statement.executeBatch();
        }
    }

    private static final String SAVE_COMPONENT_QUERY = "INSERT OR REPLACE INTO components VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    @Override
    public void saveComponent(@NotNull WirelessComponent component) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(SAVE_COMPONENT_QUERY)) {
            statement.setInt(1, component.getId());
            Location location = component.getLocation();
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.setBytes(5, super.convertIdToBytes(location.getWorld().getUID()));
            statement.setBytes(6, super.convertIdToBytes(component.getOwner()));
            statement.setString(7, component.getItemType().name());
            statement.setString(8, component.getBlockType().name());
            statement.setString(9, component instanceof WirelessTransmitter ? Constants.TRANSMITTER : Constants.RECEIVER);
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();

        }
    }

    private static final String SAVE_SUBSCRIPTION_QUERY = "INSERT OR REPLACE INTO subscriptions VALUES (?, ?)";

    @Override
    public void saveSubscription(@NotNull WirelessReceiver receiver) {
        if (receiver.getSubscription() != null) {
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(SAVE_SUBSCRIPTION_QUERY)) {
                statement.setInt(1, receiver.getId());
                statement.setInt(2, receiver.getSubscription().getId());
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static final String DELETE_COMPONENT_QUERY = "DELETE FROM components WHERE id = ?;";

    @Override
    public void deleteComponent(@NotNull WirelessComponent component) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_COMPONENT_QUERY)) {
            statement.setInt(1, component.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static final String DELETE_SUBSCRIPTION_QUERY = "DELETE FROM subscriptions WHERE receiver = ?;";

    @Override
    public void deleteSubscription(@NotNull WirelessReceiver receiver) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_SUBSCRIPTION_QUERY)) {
            statement.setInt(1, receiver.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static final String SELECT_COMPONENTS_QUERY = "SELECT id, x, y, z, owner, itemMaterial, blockMaterial, type FROM components WHERE worldId = ?;";
//    private static final String SELECT_LAST_TRANSMISSION_QUERY = "SELECT transmission FROM last_transmission WHERE id = ?";
    private static final String SELECT_SUBSCRIPTION_QUERY = "SELECT transmitter FROM subscriptions WHERE receiver = ?;";

    @Override
    public @NotNull Set<WirelessComponentInfo> loadComponents(@NotNull World world) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement1 = connection.prepareStatement(SELECT_COMPONENTS_QUERY)) {
                statement1.setBytes(1, super.convertIdToBytes(world.getUID()));
                Set<WirelessComponentInfo> components = new HashSet<>();
                ResultSet result1 = statement1.executeQuery();
                while (result1.next()) {
                    int id = result1.getInt("id");
                    int x = result1.getInt("x");
                    int y = result1.getInt("y");
                    int z = result1.getInt("z");
                    UUID owner = super.convertBytesToId(result1.getBytes("owner"));
                    Material itemMaterial = Material.valueOf(result1.getString("itemMaterial"));
                    Material blockMaterial = Material.valueOf(result1.getString("blockMaterial"));
                    String type = result1.getString("type");
                    Integer subscription = null;
                    if (type.equals(Constants.RECEIVER)) {
                        try (PreparedStatement statement2 = connection.prepareStatement(SELECT_SUBSCRIPTION_QUERY)) {
                            statement2.setInt(1, id);
                            ResultSet result2 = statement2.executeQuery();
                            if (result2.next()) {
                                int rawSubscription = result2.getInt(Constants.TRANSMITTER);
                                if (!result2.wasNull()) {
                                    subscription = rawSubscription;
                                }
                            }
                        }
                    }
                    components.add(new WirelessComponentInfo(id, new Location(world, x, y, z), owner, itemMaterial, blockMaterial, type, subscription));
                }
                return components;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Collections.emptySet();
        }
    }

    @Override
    public int loadNextId() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("SELECT MAX(id) FROM components;");
            if (result.next()) {
                int id = result.getInt("MAX(id)");
                if (!result.wasNull()) {
                    return id + 1;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Integer.MIN_VALUE;
    }
}
