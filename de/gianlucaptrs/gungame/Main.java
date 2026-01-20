package de.gianlucaptrs.gungame;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.gianlucaptrs.cmd.Mapchange_CMD;
import de.gianlucaptrs.cmd.gg_CMD;
import de.gianlucaptrs.events.gg_LIS;
import de.gianlucaptrs.events.gg_killLIS;
import de.gianlucaptrs.methoden.AllMethods;
import de.gianlucaptrs.events.GunGameBoard;
import de.gianlucaptrs.stats.Resetstats_CMD;
import de.gianlucaptrs.stats.StatsManager;
import de.gianlucaptrs.stats.Stats_CMD;

public class Main extends JavaPlugin{
	
	public static String prefix = "§7[§eGunGame§7] ";
	public static String noperm = prefix + "§cDarauf hast du keine Berechtigung!";
	public static String geilertyp = prefix + "§eGunGame §7entwickelt für §9Lotus §7von §cGianlucaptrs";
	
	public static Main instance;
	public StatsManager stats;
	
	public void onEnable() {
		instance = this;
		AllMethods.board = new GunGameBoard(this);
		AllMethods.board.start();
		getDataFolder().mkdirs();
		File cfgFile = new File(getDataFolder(), "config.yml");
		if (!cfgFile.exists()) {
		    try {
		        cfgFile.createNewFile();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		saveDefaultConfig(); 
		
		getConfig().addDefault("pvpsettings.fast_attack", false);
		getConfig().addDefault("pvpsettings.attack_speed", 16.0);
		getConfig().addDefault("stats.mysql.enabled", false);
		getConfig().addDefault("stats.mysql.host", "127.0.0.1");
		getConfig().addDefault("stats.mysql.port", 3306);
		getConfig().addDefault("stats.mysql.database", "gungame");
		getConfig().addDefault("stats.mysql.user", "root");
		getConfig().addDefault("stats.mysql.password", "");
		getConfig().addDefault("stats.mysql.table", "gungame_stats");
		getConfig().addDefault("stats.mysql.useSSL", false);

		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		Bukkit.getConsoleSender().sendMessage(prefix + "§aDas Skript wurde geladen!");
		
		AllMethods.mapRotation = getConfig().getStringList("gungame.maps");

		if (AllMethods.mapRotation == null || AllMethods.mapRotation.isEmpty()) {
		    getLogger().warning("gungame.maps ist leer! Fallback: erste Map aus spawns.yml wird genutzt.");

		    File f = new File(getDataFolder(), "spawns.yml");
		    YamlConfiguration sp = YamlConfiguration.loadConfiguration(f);

		    if (sp.getConfigurationSection("maps") != null) {
		    	AllMethods.mapRotation = new java.util.ArrayList<>(sp.getConfigurationSection("maps").getKeys(false));

		        if (!AllMethods.mapRotation.isEmpty()) {
		            java.util.Collections.sort(AllMethods.mapRotation, String.CASE_INSENSITIVE_ORDER);

		            AllMethods.currentMap = AllMethods.mapRotation.get(0).toLowerCase();

		            getLogger().info("Fallback-Rotation aus spawns.yml: " + AllMethods.mapRotation);
		        } else {
		            getLogger().warning("spawns.yml enthält keine Maps unter 'maps:'");
		        }
		    } else {
		        getLogger().warning("spawns.yml hat keinen Abschnitt 'maps:'");
		    }
		
		}
		
		boolean mysql = getConfig().getBoolean("stats.mysql.enabled", false);

		if (mysql) {
		    stats = new StatsManager(new de.gianlucaptrs.stats.MySQLStatsStorage(
		            this,
		            getConfig().getString("stats.mysql.host"),
		            getConfig().getInt("stats.mysql.port", 3306),
		            getConfig().getString("stats.mysql.database"),
		            getConfig().getString("stats.mysql.user"),
		            getConfig().getString("stats.mysql.password"),
		            getConfig().getBoolean("stats.mysql.useSSL", false),
		            getConfig().getString("stats.mysql.table", "gungame_stats")));
		} else {
		    stats = new StatsManager(new de.gianlucaptrs.stats.YamlStatsStorage(this));
		}
		AllMethods.applyWorldSettings();
		AllMethods.startMapChanger();
		
		
		
		getCommand("gungame").setExecutor(new gg_CMD());
		getCommand("mapchange").setExecutor(new Mapchange_CMD());
		getCommand("stats").setExecutor(new Stats_CMD());
		getCommand("resetstats").setExecutor(new Resetstats_CMD());
		
		getServer().getPluginManager().registerEvents(new gg_LIS(), this);
		getServer().getPluginManager().registerEvents(new gg_killLIS(), this);
	}
	
	public void onDisable() {
		if (AllMethods.board != null) AllMethods.board.stop();
		for (Player p : Bukkit.getOnlinePlayers()) {
	        if (stats != null) stats.unload(p);
	    }
	    if (stats != null) stats.shutdown();
		Bukkit.getConsoleSender().sendMessage(prefix + "§cDas Skript wurde gestoppt!");
	}

}
