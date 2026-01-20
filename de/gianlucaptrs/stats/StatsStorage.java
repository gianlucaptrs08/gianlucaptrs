package de.gianlucaptrs.stats;

import java.util.UUID;
import java.util.function.Consumer;

public interface StatsStorage {
	
	void load(UUID uuid, Consumer<Stats> callback);
	void save(UUID uuid, Stats stats);
	void addkill(UUID uuid);
	void addDeath(UUID uuid);
	void shutdown();
	void resetAll();
}
