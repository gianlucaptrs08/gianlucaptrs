package de.gianlucaptrs.methoden;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.gianlucaptrs.events.GunGameBoard;
import de.gianlucaptrs.gungame.Main;

public class AllMethods {
	
public static Map<UUID, Integer> level = new HashMap<>();

private final static HashMap<UUID, Long> msgCooldown = new HashMap<>();
	
	public static GunGameBoard board;

	private static int remainingMinutes;
	private static boolean mapSwitchRunning = false;
	private static int countdownTaskId = -1;
	private static int secondInMinute = 0;
	private static int countdownSecondsLeft = -1;
	
	public static int getRemainingMinutes() { return remainingMinutes; }
	public static int getSecondInMinute() { return secondInMinute; }
	public static boolean isMapSwitchRunning() { return mapSwitchRunning; }
	public static int getCountdownSecondsLeft() { return countdownSecondsLeft; }
	public static String getCurrentMapName() { return currentMap == null ? "?" : currentMap; }
	
	public static List<String> mapRotation;
	private static int mapIndex = 0;
	public static String currentMap;
	
	public static void nextMap() {
		if(mapRotation == null || mapRotation.isEmpty()) return;
		mapIndex++;
		if(mapIndex >= mapRotation.size()) mapIndex = 0;
		currentMap = mapRotation.get(mapIndex).toLowerCase();
		Bukkit.broadcastMessage(Main.prefix + "§aMap-Wechsel! Neue Map: §e" + currentMap);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			resetPlayerForGame(all);
		}
		applyWorldSettings();
		Bukkit.broadcastMessage(Main.prefix + "§7Nächster Map-Wechsel in §c" + remainingMinutes + " §7Minuten!");
	}
	
	@SuppressWarnings("unused")
	public static void startMapChanger() {
	    int minutes = Main.instance.getConfig().getInt("gungame.mapchange_minutes", 30);

	    remainingMinutes = minutes;
	    secondInMinute = 0;

	    Bukkit.broadcastMessage(Main.prefix + "§7Nächster Mapwechsel in §c" + minutes + " §7Minuten!");

	    Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
	        if (mapSwitchRunning) return;

	    
	        if (remainingMinutes <= 0 && secondInMinute <= 0) {
	            startMapSwitchCountdown(10);
	            return;
	        }

	      
	        if (secondInMinute <= 0) {
	            if (remainingMinutes > 0) {
	                remainingMinutes--;
	                secondInMinute = 59;

	             
	                if (remainingMinutes <= 15 && remainingMinutes > 0 && remainingMinutes % 5 == 0) {
	                    Bukkit.broadcastMessage(Main.prefix + "§7Mapwechsel in §c" + remainingMinutes + " §7Minuten!");
	                }
	            }
	        } else {
	            secondInMinute--;
	        }

	    }, 20L, 20L); 
	}
	
	public static void startMapSwitchCountdown(int seconds) {
	    if (mapSwitchRunning) return;
	    if (mapRotation == null || mapRotation.isEmpty()) return;
	    mapSwitchRunning = true;
	    countdownSecondsLeft = seconds;
	    
	    countdownTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
	        int timeLeft = seconds;

	        @Override
	        public void run() {
	        	countdownSecondsLeft = timeLeft;
	        	
	        	if (board != null) board.updateAll();
	            if (timeLeft == 10 || (timeLeft <= 5 && timeLeft >= 1)) {
	                Bukkit.broadcastMessage(Main.prefix + "§7Mapwechsel in §c" + timeLeft + " §7Sekunden!");
	            }

	            if (timeLeft <= 0) {
	                Bukkit.getScheduler().cancelTask(countdownTaskId);
	                countdownTaskId = -1;
	                mapSwitchRunning = false;
	                countdownSecondsLeft = -1;
	                nextMap();
	                return;
	            }

	            timeLeft--;
	        }
	    }, 0L, 20L);
	}
	
	
	
	public static void resetPlayerForGame(Player p) {
		if(currentMap == null) {
			p.sendMessage(Main.prefix + "§cKeine Map gesetzt!");
			return;
		}
		level.put(p.getUniqueId(), 1);
		p.setHealth(20);
		p.setFoodLevel(20);
		Location loc = getRandomSpawn(currentMap);
		if(loc == null) loc = getMapSpawn(currentMap);
		if(loc != null) p.teleport(loc);
		applyKit(p, 1);
	}
	
	public static Location getMapSpawn(String mapName) {
		File f = new File(Main.instance.getDataFolder(), "spawns.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        String path = "maps." + mapName.toLowerCase() + ".spawn";
        if (!cfg.contains(path + ".world")) return null;

        String world = cfg.getString(path + ".world");
        World w = Bukkit.getWorld(world);
        if (w == null) return null;

        double x = cfg.getDouble(path + ".x");
        double y = cfg.getDouble(path + ".y");
        double z = cfg.getDouble(path + ".z");
        float yaw = (float) cfg.getDouble(path + ".yaw");
        float pitch = (float) cfg.getDouble(path + ".pitch");

        return new Location(w, x, y, z, yaw, pitch);
	}
	
	public static Location getRandomSpawn(String mapName) {
        File f = new File(Main.instance.getDataFolder(), "spawns.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        List<Location> list = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String path = "maps." + mapName.toLowerCase() + ".spawns." + i;
            if (!cfg.contains(path + ".world")) continue;

            String world = cfg.getString(path + ".world");
            World w = Bukkit.getWorld(world);
            if (w == null) continue;

            double x = cfg.getDouble(path + ".x");
            double y = cfg.getDouble(path + ".y");
            double z = cfg.getDouble(path + ".z");
            float yaw = (float) cfg.getDouble(path + ".yaw");
            float pitch = (float) cfg.getDouble(path + ".pitch");

            list.add(new Location(w, x, y, z, yaw, pitch));
        }

        if (list.isEmpty()) return null;
        return list.get(new Random().nextInt(list.size()));
        
    }
	
	 public static void applyWorldSettings() {
	        String worldName = Main.instance.getConfig().getString("gungame.world");
	        if (worldName == null) return;

	        World w = Bukkit.getWorld(worldName);
	        if (w == null) return;

	        if (Main.instance.getConfig().contains("gungame.time")) {
	            w.setTime(Main.instance.getConfig().getLong("gungame.time"));
	        }

	        if (Main.instance.getConfig().contains("gungame.daylightcycle")) {
	            w.setGameRule(GameRule.ADVANCE_TIME, Main.instance.getConfig().getBoolean("gungame.daylightcycle"));
	        }

	        String weather = Main.instance.getConfig().getString("gungame.weather", "sunny");
	        if (weather.equalsIgnoreCase("sunny")) {
	            w.setStorm(false);
	            w.setThundering(false);
	        } else if (weather.equalsIgnoreCase("rain")) {
	            w.setStorm(true);
	            w.setThundering(false);
	        } else if (weather.equalsIgnoreCase("thunder")) {
	            w.setStorm(true);
	            w.setThundering(true);
	        }
	    }
	
	@SuppressWarnings("unchecked")
	public static void applyKit(Player p, int lvl) {
	    File kitsFile = new File(Main.instance.getDataFolder(), "kits.yml");
	    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(kitsFile);

	    String path = "kits." + lvl;
	    if(!cfg.contains(path + ".contents")) {
	        p.sendMessage(Main.prefix + "§cFür Level " + lvl + " ist kein Kit gesetzt!");
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
	    if(offhand != null) p.getInventory().setItemInOffHand(offhand);
	}
	
	public static void applayPvPSettings(Player p) {
	    if (Main.instance.getConfig().getBoolean("pvpsettings.fast_attack", true)) {
	        p.setNoDamageTicks(0);
	    }

	    if (Main.instance.getConfig().getBoolean("pvpsettings.attack_speed", true)) {
	        double speed = Main.instance.getConfig().getDouble("pvpsettings.attack_speed", 16.0);
	        if (p.getAttribute(Attribute.ATTACK_SPEED) != null) {
	            p.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(speed);
	        }
	    }
	}

	
	@SuppressWarnings("unused")
	public static boolean isInSafezone(Location loc) {
	    if (loc == null || loc.getWorld() == null) return false;
	    if (currentMap == null) return false;

	    File f = new File(Main.instance.getDataFolder(), "spawns.yml");
	    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

	    String path = "maps." + currentMap.toLowerCase() + ".safezone";
	    if (!cfg.contains(path + ".world")) return false;

	    String worldName = cfg.getString(path + ".world");
	    if (worldName == null) return false;
	    if (!loc.getWorld().getName().equalsIgnoreCase(worldName)) return false;

	    double x = cfg.getDouble(path + ".x");
	    double y = cfg.getDouble(path + ".y");
	    double z = cfg.getDouble(path + ".z");
	    double radius = cfg.getDouble(path + ".radius", 0);

	    if (radius <= 0) return false;

	    double dx = loc.getX() - x;
	    double dz = loc.getZ() - z;

	    return (dx * dx + dz * dz) <= (radius * radius);
	}
	
	public static void warn(Player p) {
        long now = System.currentTimeMillis();
        long last = msgCooldown.getOrDefault(p.getUniqueId(), 0L);
        if (now - last < 1000) return; 
        msgCooldown.put(p.getUniqueId(), now);

        p.sendMessage(Main.prefix + "§cPvP ist in der Safezone verboten!");
    }
}
