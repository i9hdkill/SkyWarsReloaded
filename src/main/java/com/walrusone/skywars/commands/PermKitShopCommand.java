package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.PermKitMenu;
import com.walrusone.skywars.utilities.Messaging;

public class PermKitShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchasePermanentKitsEnabled");
		if (enabled) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				if (SkyWarsReloaded.perms.has(player, "swr.permkits")) {
					hasPerm = true;
				}
			} 
			if (hasPerm) {
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
						if (!gPlayer.inGame()) {
								new PermKitMenu(gPlayer);
						} else {
							sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "USAGE: /swr permkitshop");
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Permanent Kit Shop is disabled!");
		}
		
    	return true;
    }

}
