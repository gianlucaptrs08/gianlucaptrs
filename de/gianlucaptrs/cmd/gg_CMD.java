package de.gianlucaptrs.cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.gianlucaptrs.gungame.Main;

public class gg_CMD implements CommandExecutor{

	public File spawns = new File(Main.instance.getDataFolder(), "spawns.yml");
	public File kits = new File(Main.instance.getDataFolder(), "kits.yml");
	
	public static ArrayList<String> build = new ArrayList<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.prefix + "§cDu musst ein Spieler sein!");
		}else {
			Player p = (Player)sender;
			if(!p.hasPermission("Gungame.Admin")) {
				p.sendMessage(Main.geilertyp);
			}else {
				if(args.length == 0) {
					p.sendMessage("§7-----[§eGunGame§8-§eBefehle§7]-----");
					p.sendMessage("§e/gungame safezone <mapname> <1-4> §8» §7Safezonen festlegen");
					p.sendMessage("§e/gungame setkit <1-20> §8» §7Kit für jede Stufe festlegen");
					p.sendMessage("§e/gungame setspawn <mapname> §8» §7Spawn für individuelle Maps");
					p.sendMessage("§e/gungame setweather <sunny | rain | thunder> §8» §7Wetter festlegen global");
					p.sendMessage("§e/gungame settime <day | night> §8» §7Tageszeit festlegen global");
					p.sendMessage("§e/gungame build §8» §aAktiviert§7/§cDeaktiviert §7den Bau-Modus");
					p.sendMessage("§e/mapchange §8» §7Map innerhalb von 10 Sekunden wechseln");
					p.sendMessage("§e/stats <name> §8» §7Statistiken anzeigen");
					p.sendMessage("§e/resetstats §8» §7Statistiken aller Spieler zurücksetzen");
				}else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("setspawn")) {
						p.sendMessage(Main.prefix + "§7Verwende: §e/gungame <setspawn> <mapname>");
					}else if(args[0].equalsIgnoreCase("setkit")) {
						p.sendMessage(Main.prefix + "§7Verwende §e/gungame setkit <1-20>");
					}else if(args[0].equalsIgnoreCase("safezone")) {
						p.sendMessage(Main.prefix + "§7Verwende §e/gungame safezone <mapname> <position>");
					}else if(args[0].equalsIgnoreCase("setweather")) {
						p.sendMessage(Main.prefix + "§7Verwende §e/gungame setweather <sunny | rain | thunder>");
					}else if(args[0].equalsIgnoreCase("settime")) {
						p.sendMessage(Main.prefix + "§cVerwende: §e/gungame settime <day | night>");
					}else if(args[0].equalsIgnoreCase("build")) {
						if(build.contains(p.getName())) {
							build.remove(p.getName());
							p.sendMessage(Main.prefix + "§7Der Bau-Modus wurde §cdeaktiviert!");
						}else {
							build.add(p.getName());
							p.sendMessage(Main.prefix + "§7Der Bau-Modus wurde §aaktiviert!");
						}
					}
				}else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("setspawn")) {
						YamlConfiguration spawnsCfg = YamlConfiguration.loadConfiguration(spawns);
						String mapName = args[1].toLowerCase();
						Location loc = p.getLocation();
						String path = "maps." + mapName + ".spawn";
			            spawnsCfg.set(path + ".world", loc.getWorld().getName());
			            spawnsCfg.set(path + ".x", loc.getX());
			            spawnsCfg.set(path + ".y", loc.getY());
			            spawnsCfg.set(path + ".z", loc.getZ());
			            spawnsCfg.set(path + ".yaw", loc.getYaw());
			            spawnsCfg.set(path + ".pitch", loc.getPitch());
			            try {
							spawnsCfg.save(spawns);
						} catch (IOException e) {
							p.sendMessage(Main.prefix + "§cFehler beim Speichern der spawns.yml!");
							e.printStackTrace();
						}
			            p.sendMessage(Main.prefix + "§aSpawn für Map §e" + mapName + " §agesetzt!");
					}else if(args[0].equalsIgnoreCase("setkit")) {
						int level;
						try {
					        level = Integer.parseInt(args[1]);
					    } catch (NumberFormatException ex) {
					        p.sendMessage(Main.prefix + "§cBitte gib eine Zahl von 1 bis 20 an!");
					        return true;
					    }
						if(level < 1 || level > 20) {
					        p.sendMessage(Main.prefix + "§cLevel muss zwischen 1 und 20 sein!");
					        return true;
					    }
						YamlConfiguration kitscfg = YamlConfiguration.loadConfiguration(kits);
						String path = "kits." + level;
					    kitscfg.set(path + ".contents", p.getInventory().getContents());
					    kitscfg.set(path + ".armor", p.getInventory().getArmorContents());
					    kitscfg.set(path + ".offhand", p.getInventory().getItemInOffHand());
					    try {
					        kitscfg.save(kits);
					        p.sendMessage(Main.prefix + "§aKit für Level §e" + level + " §agespeichert!");
					    } catch (IOException e) {
					        p.sendMessage(Main.prefix + "§cBeim Speichern des Kits ist ein Fehler aufgetreten");
					        e.printStackTrace();
					    }
					}else if(args[0].equalsIgnoreCase("setweather")) {
						String type = args[1].toLowerCase();
						World w = p.getWorld();
						Main.instance.getConfig().set("gungame.world", w.getName());
						if(type.equals("sunny")) {
							w.setStorm(false);
							w.setThundering(false);
							Main.instance.getConfig().set("gungame.weather", "sunny");
						}else if(type.equals("rain")) {
							w.setStorm(true);
							w.setThundering(false);
							Main.instance.getConfig().set("gungame.weather", "rain");
						}else if(type.equals("thunder")) {
							w.setStorm(true);
							w.setThundering(true);
							Main.instance.getConfig().set("gungame.weather", "thunder");
						}else {
							p.sendMessage(Main.prefix + "§7Verwende §e/gungame setweather <sunny | rain | thunder>");
						}
						Main.instance.saveConfig();
						p.sendMessage(Main.prefix + "§aDas Wetter wurde gespeichert!");
						return true;
					}else if(args[0].equalsIgnoreCase("settime")) {
						World w = p.getWorld();
						long ticks;
						if(args[1].equalsIgnoreCase("day")) {
							ticks = 1000L;
						}else if(args[1].equalsIgnoreCase("night")) {
							ticks = 18000L;
						}else {
							p.sendMessage(Main.prefix + "§cVerwende: §e/gungame settime <day | night>");
					        return true;
						}
						w.setTime(ticks);
					    w.setGameRule(GameRule.ADVANCE_TIME, false);

					    Main.instance.getConfig().set("gungame.world", w.getName());
					    Main.instance.getConfig().set("gungame.time", ticks);
					    Main.instance.getConfig().set("gungame.daylightcycle", false);
					    Main.instance.saveConfig();

					    p.sendMessage(Main.prefix + "§aZeit gespeichert -> §e" + ticks);
					    return true;
					}
				}else if(args.length == 3) {
					if(args[0].equalsIgnoreCase("safezone")) {
					    String mapName = args[1].toLowerCase();

					    int radius;
					    try {
					        radius = Integer.parseInt(args[2]);
					    } catch (NumberFormatException e) {
					        p.sendMessage(Main.prefix + "§cRadius muss eine Zahl sein!");
					        return true;
					    }

					    YamlConfiguration spawnsCfg = YamlConfiguration.loadConfiguration(spawns);
					    Location loc = p.getLocation();

					    String path = "maps." + mapName + ".safezone";
					    spawnsCfg.set(path + ".world", loc.getWorld().getName());
					    spawnsCfg.set(path + ".x", loc.getX());
					    spawnsCfg.set(path + ".y", loc.getY());
					    spawnsCfg.set(path + ".z", loc.getZ());
					    spawnsCfg.set(path + ".radius", radius);

					    try {
					        spawnsCfg.save(spawns);
					        p.sendMessage(Main.prefix + "§aSafezone gesetzt (Radius: §e" + radius + "§a)");
					    } catch (IOException e) {
					        e.printStackTrace();
					    }
					}
				}
			}
		}
		
		return true;
	}

}
