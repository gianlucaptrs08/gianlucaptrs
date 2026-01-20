package de.gianlucaptrs.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MySQLStatsStorage implements StatsStorage{
	
	 private final JavaPlugin plugin;
	    private final String url;
	    private final String user;
	    private final String pass;
	    private final String table;

	    public MySQLStatsStorage(JavaPlugin plugin, String host, int port, String db, String user, String pass, boolean useSSL, String table) {
	        this.plugin = plugin;
	        this.user = user;
	        this.pass = pass;
	        this.table = table;

	        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db
	                + "?useSSL=" + useSSL
	                + "&autoReconnect=true"
	                + "&characterEncoding=utf8";
	        init();
	    }

	    private Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(url, user, pass);
	    }

	    private void init() {
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	            try (Connection con = getConnection();
	                 Statement st = con.createStatement()) {

	                st.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " ("
	                        + "uuid VARCHAR(36) PRIMARY KEY,"
	                        + "kills INT NOT NULL DEFAULT 0,"
	                        + "deaths INT NOT NULL DEFAULT 0"
	                        + ");");

	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        });
	    }

	    @Override
	    public void load(UUID uuid, Consumer<Stats> callback) {
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	            Stats stats = new Stats(0, 0, 0);

	            try (Connection con = getConnection()) {
	                
	                try (PreparedStatement ins = con.prepareStatement(
	                        "INSERT IGNORE INTO " + table + " (uuid,kills,deaths) VALUES (?,?,?)")) {
	                    ins.setString(1, uuid.toString());
	                    ins.setInt(2, 0);
	                    ins.setInt(3, 0);
	                    ins.executeUpdate();
	                }

	                try (PreparedStatement ps = con.prepareStatement(
	                        "SELECT kills,deaths FROM " + table + " WHERE uuid=?")) {
	                    ps.setString(1, uuid.toString());
	                    try (ResultSet rs = ps.executeQuery()) {
	                        if (rs.next()) {
	                            stats = new Stats(rs.getInt("kills"), rs.getInt("deaths"), 0);
	                        }
	                    }
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }

	            Stats finalStats = stats;
	            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalStats));
	        });
	    }

	    @Override
	    public void save(UUID uuid, Stats stats) {
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	            try (Connection con = getConnection();
	                 PreparedStatement ps = con.prepareStatement(
	                         "INSERT INTO " + table + " (uuid,kills,deaths) VALUES (?,?,?) "
	                                 + "ON DUPLICATE KEY UPDATE kills=?, deaths=?")) {

	                ps.setString(1, uuid.toString());
	                ps.setInt(2, stats.getkills());
	                ps.setInt(3, stats.getDeaths());
	                ps.setInt(4, stats.getkills());
	                ps.setInt(5, stats.getDeaths());
	                ps.executeUpdate();

	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        });
	    }

	    @Override
	    public void addkill(UUID uuid) {
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	            try (Connection con = getConnection();
	                 PreparedStatement ps = con.prepareStatement(
	                         "INSERT INTO " + table + " (uuid,kills,deaths) VALUES (?,1,0) "
	                                 + "ON DUPLICATE KEY UPDATE kills = kills + 1")) {
	                ps.setString(1, uuid.toString());
	                ps.executeUpdate();
	            } catch (SQLException e) { e.printStackTrace(); }
	        });
	    }

	    @Override
	    public void addDeath(UUID uuid) {
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	            try (Connection con = getConnection();
	                 PreparedStatement ps = con.prepareStatement(
	                         "INSERT INTO " + table + " (uuid,kills,deaths) VALUES (?,0,1) "
	                                 + "ON DUPLICATE KEY UPDATE deaths = deaths + 1")) {
	                ps.setString(1, uuid.toString());
	                ps.executeUpdate();
	            } catch (SQLException e) { e.printStackTrace(); }
	        });
	    }
	    
	    @Override
	    public void resetAll() {
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	            try (Connection con = getConnection();
	                 Statement st = con.createStatement()) {

	                st.executeUpdate("TRUNCATE TABLE " + table);

	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        });
	    }

	    @Override
	    public void shutdown() { /* connections are per-call */ }
	}
