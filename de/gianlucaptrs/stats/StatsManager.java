package de.gianlucaptrs.stats;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class StatsManager {
	
	 private static StatsStorage storage;
	    private final static Map<UUID, Stats> cache = new ConcurrentHashMap<>();

	    @SuppressWarnings("static-access")
		public StatsManager(StatsStorage storage) {
	        this.storage = storage;
	    }

	    public void load(Player p) {
	        storage.load(p.getUniqueId(), stats -> cache.put(p.getUniqueId(), stats));
	    }

	    public void unload(Player p) {
	        Stats stats = cache.remove(p.getUniqueId());
	        if (stats != null) storage.save(p.getUniqueId(), stats);
	    }

	    @SuppressWarnings("unused")
		public void addkill(UUID uuid) {
	        Stats s = cache.computeIfAbsent(uuid, u -> new Stats(0, 0, 0));
	        s.addkill();
	        storage.save(uuid, s);
	    }

	    @SuppressWarnings("unused")
		public void addDeath(UUID uuid) {
	        Stats s = cache.computeIfAbsent(uuid, u -> new Stats(0, 0, 0));
	        s.addDeath();
	        storage.save(uuid, s);
	    }

	    public Stats get(UUID uuid) {
	        return cache.getOrDefault(uuid, new Stats(0, 0, 0));
	    }
	    
	    public static void resetAll() {
	        cache.clear();
	        storage.resetAll();
	    }
	    
	    public void updateLevelRecord(UUID uuid, int level) {
	        Stats s = cache.get(uuid);
	        if (s == null) return;

	        s.updateLevelRecord(level);
	        storage.save(uuid, s);
	    }

	    public void shutdown() {
	        storage.shutdown();
	    }
	}