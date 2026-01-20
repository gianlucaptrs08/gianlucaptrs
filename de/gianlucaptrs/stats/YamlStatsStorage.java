package de.gianlucaptrs.stats;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YamlStatsStorage implements StatsStorage{
	
	private final JavaPlugin plugin;
	private final File file;
	private YamlConfiguration cfg;
	
	public YamlStatsStorage(JavaPlugin plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "stats.yml");
		if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
		if(!file.exists()) {
			try {
				file.createNewFile();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.cfg = YamlConfiguration.loadConfiguration(file);
	}
	private String base(UUID uuid) {
		return "players." + uuid.toString();
	}
	
	@Override
	public void load(UUID uuid, Consumer<Stats> callback) {
		int kills = cfg.getInt(base(uuid) + ".kills", 0);
		int deaths = cfg.getInt(base(uuid) + ".deaths", 0);
		int record = cfg.getInt(base(uuid) + ".level_record", 0);
		Bukkit.getScheduler().runTask(plugin, () -> callback.accept(new Stats(kills, deaths, record)));
	}

	@Override
	public void save(UUID uuid, Stats stats) {
		cfg.set(base(uuid) + ".kills", stats.getkills());
		cfg.set(base(uuid) + ".deaths", stats.getDeaths());
		cfg.set(base(uuid) + ".level_record", stats.getLevelRecord());
		try {
			cfg.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void addkill(UUID uuid) {
        int kills = cfg.getInt(base(uuid) + ".kills", 0) + 1;
        cfg.set(base(uuid) + ".kills", kills);
        try { cfg.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void addDeath(UUID uuid) {
        int deaths = cfg.getInt(base(uuid) + ".deaths", 0) + 1;
        cfg.set(base(uuid) + ".deaths", deaths);
        try { cfg.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void shutdown() {  
    	}
    
    @Override
    public void resetAll() {
        cfg.set("players", null);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
