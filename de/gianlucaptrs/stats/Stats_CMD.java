package de.gianlucaptrs.stats;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.gianlucaptrs.gungame.Main;

public class Stats_CMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.prefix + "§cDu musst ein Spieler sein!");
		}else {
			Player p = (Player)sender;
			if(args.length == 0) {
				Stats s = Main.instance.stats.get(p.getUniqueId());
				p.sendMessage("§7----[§eDeine GunGame Stats§7]-----");
				p.sendMessage(Main.prefix + "§eKills: §c" + s.getkills());
				p.sendMessage(Main.prefix + "§eTode: §c" + s.getDeaths());
				p.sendMessage(Main.prefix + "§eK/D: §c" + String.format(Locale.US, "%.2f", s.getKD()));
				p.sendMessage(Main.prefix + "§eLevel-Rekord: §c" + s.getLevelRecord());
			}else if(args.length == 1) {
				if(!p.hasPermission("Gungame.Admin")) {
					p.sendMessage(Main.noperm);
				}else {
					Player target = Bukkit.getPlayer(args[0]);
					Stats t = Main.instance.stats.get(target.getUniqueId());
					p.sendMessage("§7----[§c" + target.getName() + " §eGunGame Stats§7]-----");
					p.sendMessage(Main.prefix + "§eKills: §c" + t.getkills());
					p.sendMessage(Main.prefix + "§eTode: §c" + t.getDeaths());
					p.sendMessage(Main.prefix + "§eK/D: §c" + String.format(Locale.US, "%.2f", t.getKD()));
					p.sendMessage(Main.prefix + "§eLevel-Rekord: §c" + t.getLevelRecord());
				}
			}
		}
		return true;
	}
	
	

}
