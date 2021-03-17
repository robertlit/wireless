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
package me.robertlit.wireless.settings;

import java.util.List;

public final class Lang {

    private Lang() {}

    public static String wirelessTransmitterName; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Wireless Transmitter";
    public static List<String> wirelessTransmitterLore; // = Collections.emptyList();
    public static String wirelessReceiverName; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Wireless Receiver";
    public static List<String> wirelessReceiverLore; // = Collections.emptyList();
    public static String wirelessTransmitterInventoryTitle; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Control Wireless Transmitter";
    public static String wirelessReceiverInventoryTitle; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Control Wireless Receiver";
    public static String noTransmitterConnected ; //= ChatColor.RED + "" + ChatColor.BOLD + "No transmitter connected";
    public static String connectedToTransmitter; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Connected to transmitter";
    public static String clickToDisconnect; // = ChatColor.RED + "" + ChatColor.BOLD + "CLICK TO DISCONNECT";
    public static String clickToConnect; // = ChatColor.GREEN + "" + ChatColor.BOLD + "CLICK TO CONNECT";
    public static String availableTransmitters; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Available Transmitters %current_page% / %total_pages%";
    public static String availableReceivers; // = ChatColor.BLUE + "" + ChatColor.BOLD + "Available Receivers %current_page% / %total_pages%";
    public static String nextPage; // = ChatColor.GREEN + "Next page";
    public static String previousPage; // = ChatColor.GREEN + "Previous page";
    public static String goBack; // = ChatColor.RED + "Go back";
    public static String viewReceivers; // = ChatColor.BLUE + "" + ChatColor.BOLD + "View receivers";
    public static String connectAndDisconnectReceivers; // = ChatColor.GREEN + "" + ChatColor.BOLD + "CLICK TO CONNECT AND " + ChatColor.RED + "" + ChatColor.BOLD +  "DISCONNECT" + ChatColor.GREEN + "" + ChatColor.BOLD +  " RECEIVERS";
    public static String disallowed; // = ChatColor.RED + "You can't do that!";
    public static String playerNotOnline; // = ChatColor.RED + "The specified player is not online";
    public static String invalidComponentType; // = ChatColor.RED + "The component type should be either " + Constants.TRANSMITTER + " or " + Constants.RECEIVER;
    public static String invalidMaterial; // = ChatColor.RED + "Please specify a valid material";
    public static String giveSuccessful; // = ChatColor.GREEN + "Successfully given an item";
    public static String availableSubcommands; // = ChatColor.DARK_GREEN + "Available subcommands:";
    public static String tooManyComponents;
    public static String noLongerValid;
    public static String highlightToggle;
}
