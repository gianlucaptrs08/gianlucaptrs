package de.gianlucaptrs.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.gianlucaptrs.gungame.Main;
import de.gianlucaptrs.methoden.AllMethods;

public class Mapchange_CMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.prefix + "§cDu musst ein Spieler sein!");
		}else {
			Player p = (Player)sender;
			
			if(!p.hasPermission("GunGame.VIP")) {
				p.sendMessage(Main.noperm);
			}else {
				if(AllMethods.isMapSwitchRunning()) {
					p.sendMessage(Main.prefix + "§cEin Mapwechsel Countdown läuft bereits!");
					return true;
				}
				AllMethods.startMapSwitchCountdown(10);
				return true;
			}
		}
		
		return true;
	}
	
	

}
