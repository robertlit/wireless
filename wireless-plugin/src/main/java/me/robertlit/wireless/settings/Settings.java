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

import org.bukkit.Particle;

public class Settings {

    private Settings() {}

    public static int maxPlayerComponents;
    public static int maxConnectionDistance;

    public static boolean protectBreak;
    public static boolean protectExplosion;
    public static boolean protectBurn;

    public static double highlightsDistance;
    public static Particle highlightParticle;
}
