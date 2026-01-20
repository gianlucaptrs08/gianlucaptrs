package de.gianlucaptrs.stats;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.gianlucaptrs.gungame.Main;
import de.gianlucaptrs.methoden.AllMethods;

public class Resetstats_CMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.prefix + "§cDu musst ein Spieler sein!");
		}else {
			Player p = (Player)sender;
			if(!p.hasPermission("gungame.Admin")) {
				p.sendMessage(Main.noperm);
			}else {
				if(args.length == 0) {
					StatsManager.resetAll();
					 Bukkit.getOnlinePlayers().forEach(pl -> {
				            AllMethods.level.put(pl.getUniqueId(), 1);
				            Main.instance.stats.load(pl);
				        });

				        Bukkit.broadcastMessage(Main.prefix + "§cAlle GunGame-Stats wurden zurückgesetzt!");
				        return true;
				}
			}
		}
		
		return true;
	}
	
	

}
