package de.gianlucaptrs.events;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.gianlucaptrs.gungame.Main;
import de.gianlucaptrs.methoden.AllMethods;
import de.gianlucaptrs.stats.Stats;

public class GunGameBoard {

    private final Main plugin;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();
    private int taskId = -1;

    public GunGameBoard(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (taskId != -1) return;

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                update(p);
            }
        }, 20L, 20L);
    }

    public void stop() {
        if (taskId == -1) return;
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }

    @SuppressWarnings("deprecation")
	public void create(Player p) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = sb.registerNewObjective("gg", "dummy", "§e§lGUNGAME");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        addLine(sb, obj, "§7", 11, "line9");
        addLine(sb, obj, "§7Karte: §a", 10, "map");
        addLine(sb, obj, "§8", 9, "line8");
        addLine(sb, obj, "§7Level: ", 8, "level");
        addLine(sb, obj, "§7Kills: §e", 7, "kills");
        addLine(sb, obj, "§7Tode: §e", 6, "deaths");
        addLine(sb, obj, "§7K/D: §e", 5, "kd");
        addLine(sb, obj, "§8 ", 4, "line2");
        addLine(sb, obj, "§7Rekord: ", 3, "record");
        addLine(sb, obj, "§7Mapwechsel in: §c", 2, "time");
        addLine(sb, obj, "§8 ", 1, "line1");
        addLine(sb, obj, "§cLotusCommunity", 0, "line0");

        boards.put(p.getUniqueId(), sb);
        p.setScoreboard(sb);

        update(p);
    }

    public void remove(Player p) {
        boards.remove(p.getUniqueId());
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void update(Player p) {
        Scoreboard sb = boards.get(p.getUniqueId());
        if (sb == null) return;

        setSuffix(sb, "map", "§a" + AllMethods.getCurrentMapName());

        if (AllMethods.isMapSwitchRunning() && AllMethods.getCountdownSecondsLeft() >= 0) {
            int sec = AllMethods.getCountdownSecondsLeft();
            setSuffix(sb, "time", "§c00:" + String.format("%02d", sec));
        } else {
            int m = Math.max(0, AllMethods.getRemainingMinutes());
            int s = Math.max(0, AllMethods.getSecondInMinute());
            setSuffix(sb, "time", "§e" + String.format("%02d:%02d", m, s));
        }

        Stats st = plugin.stats.get(p.getUniqueId());
        int kills = st.getkills();
        int deaths = st.getDeaths();
        int lvl = AllMethods.level.getOrDefault(p.getUniqueId(), 1);
        int record = st != null ? st.getLevelRecord() : lvl;
        double kd = st.getKD();

        setSuffix(sb, "level", "§b" + lvl);
        setSuffix(sb, "record", "§d" + record);
        setSuffix(sb, "kills", "§a" + kills);
        setSuffix(sb, "deaths", "§c" + deaths);
        setSuffix(sb, "kd", "§e" + String.format(Locale.US, "%.2f", kd));
    }


    private void addLine(Scoreboard sb, Objective obj, String entry, int score, String teamName) {
        Team team = sb.registerNewTeam(teamName);
        team.addEntry(entry);
        obj.getScore(entry).setScore(score);
    }

    private void setSuffix(Scoreboard sb, String teamName, String suffix) {
        Team t = sb.getTeam(teamName);
        if (t != null) t.setSuffix(suffix);
    }
    
    public void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            update(p);
        }
    }
}