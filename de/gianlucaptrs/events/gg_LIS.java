package de.gianlucaptrs.events;

import java.io.File;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.gianlucaptrs.cmd.gg_CMD;
import de.gianlucaptrs.gungame.Main;
import de.gianlucaptrs.methoden.AllMethods;

public class gg_LIS implements Listener{
	
	public Location getMapSpawn(String mapName) {
	    File spawnsFile = new File(Main.instance.getDataFolder(), "spawns.yml");
	    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(spawnsFile);

	    String path = "maps." + mapName.toLowerCase() + ".spawn";
	    if(!cfg.contains(path + ".world")) return null;

	    String world = cfg.getString(path + ".world");
	    double x = cfg.getDouble(path + ".x");
	    double y = cfg.getDouble(path + ".y");
	    double z = cfg.getDouble(path + ".z");
	    float yaw = (float) cfg.getDouble(path + ".yaw");
	    float pitch = (float) cfg.getDouble(path + ".pitch");

	    return new Location(org.bukkit.Bukkit.getWorld(world), x, y, z, yaw, pitch);
		
	}
	@SuppressWarnings("unchecked")
	public void applyKit(Player p, int level) {
	    File kitsFile = new File(Main.instance.getDataFolder(), "kits.yml");
	    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(kitsFile);

	    String path = "kits." + level;

	    if(!cfg.contains(path + ".contents")) {
	        p.sendMessage(Main.prefix + "§cFür Level " + level + " ist kein Kit gesetzt!");
	        return;
	    }

	    java.util.List<org.bukkit.inventory.ItemStack> contentsList =
	            (java.util.List<org.bukkit.inventory.ItemStack>) cfg.get(path + ".contents");
	    java.util.List<org.bukkit.inventory.ItemStack> armorList =
	            (java.util.List<org.bukkit.inventory.ItemStack>) cfg.get(path + ".armor");

	    org.bukkit.inventory.ItemStack offhand = cfg.getItemStack(path + ".offhand");

	    org.bukkit.inventory.ItemStack[] contents = contentsList.toArray(new org.bukkit.inventory.ItemStack[0]);
	    org.bukkit.inventory.ItemStack[] armor = armorList.toArray(new org.bukkit.inventory.ItemStack[0]);

	    p.getInventory().clear();
	    p.getInventory().setContents(contents);
	    p.getInventory().setArmorContents(armor);

	    if(offhand != null) {
	        p.getInventory().setItemInOffHand(offhand);
	    }
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(gg_CMD.build.contains(p.getName())) {
			e.setCancelled(false);
		}else {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(gg_CMD.build.contains(p.getName())) {
			e.setCancelled(false);
		}else {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if(gg_CMD.build.contains(p.getName())) {
			e.setCancelled(false);
		}else {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(Main.prefix + "§a" + e.getPlayer().getName() + " §7hat GunGame betreten!");
		Player p = e.getPlayer();
		p.getInventory().clear();
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setGameMode(GameMode.SURVIVAL);
		
		Main.instance.stats.load(e.getPlayer());
		AllMethods.resetPlayerForGame(p);
		AllMethods.board.create(p);
		AllMethods.applayPvPSettings(p);

	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Main.instance.stats.unload(e.getPlayer());
		AllMethods.board.remove(p);
		e.setQuitMessage(Main.prefix + "§c" + e.getPlayer().getName() + " §7hat GunGame verlassen!");
	}
}
