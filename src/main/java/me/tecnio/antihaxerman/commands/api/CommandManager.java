/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.commands.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements Listener {
	
	public static void setup(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(new CommandManager(), plugin);
	}
	
    private static final List<CommandAdapter> adapters = new ArrayList<>();

    public static void register(CommandAdapter adapter) {
        adapters.add(adapter);
    }

    public static void unregister(CommandAdapter adapter) {
        adapters.remove(adapter);
    }

    public static List<CommandAdapter> getAdapters() {
        return adapters;
    }

    public static boolean handleCommand(Player player, UserInput userInput) {
        for (CommandAdapter adapter : adapters) {
            if (adapter.onCommand(player, userInput))
                return true;
        }
        return false;
    }
	
	@EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().contains(" ")) {
            String[] command = e.getMessage().split(" ");
            String name = command[0].replace("/", "");
            String[] args = new String[command.length - 1];
            for (int i = 0; i < command.length - 1; i++) {
                args[i] = command[i + 1];
            }
            if (handleCommand(e.getPlayer(), new UserInput() {
                @Override
                public String label() {
                    return name;
                }

                @Override
                public String[] args() {
                    return args;
                }
            })) e.setCancelled(true);
        } else {
            String name = e.getMessage().replace("/", "");
            if (handleCommand(e.getPlayer(), new UserInput() {
                @Override
                public String label() {
                    return name;
                }

                @Override
                public String[] args() {
                    return new String[0];
                }
            })) e.setCancelled(true);
        }
    }
}
