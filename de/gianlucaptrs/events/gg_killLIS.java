package de.gianlucaptrs.events;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.gianlucaptrs.gungame.Main;
import de.gianlucaptrs.methoden.AllMethods;

public class gg_killLIS implements Listener{
	
	@EventHandler
	public void onFallDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		
		if(e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		if(Main.instance.getConfig().getBoolean("pvpsettings.attack_speed", true)) {
			p.setNoDamageTicks(0);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWaterDeath(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ()){
			return;
		}
		Material toType = e.getTo().getBlock().getType();
		Material fromType = e.getFrom().getBlock().getType();
		boolean toWater = (toType == Material.WATER);
		boolean fromWater = (fromType == Material.WATER);
		if(toWater && !fromWater) {
			AllMethods.resetPlayerForGame(p);
			p.sendTitle("§4§lx", "§cDu bist wieder Level §e1");	
			Main.instance.stats.addDeath(p.getUniqueId());
			Random r = new Random();
			int case1 = r.nextInt(3);
			switch (case1) {
			case 0 :
				Bukkit.broadcastMessage(Main.prefix + "§e" + p.getName() + " §7hat zu tief ins Glas geschaut!");
				break;
			case 1 :
				Bukkit.broadcastMessage(Main.prefix + "§e" + p.getName() + " §7ist gestolpert und gefallen!");
		        break;
			case 2 :
				Bukkit.broadcastMessage(Main.prefix + "§e" + p.getName() + " §7hat die Orientierung verloren!");
		        break;
			}
		}
		
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Player k = p.getKiller();
		if(k == null) return;
		
		int current = AllMethods.level.getOrDefault(k.getUniqueId(), 1);
		if (current >= 20) {
            return;
        }
		int next = current + 1;
		AllMethods.level.put(k.getUniqueId(), next);
		AllMethods.applyKit(k, next);
        Main.instance.stats.updateLevelRecord(k.getUniqueId(), next);
        k.sendMessage(Main.prefix + "§aLevel up! §7Du bist jetzt Level §e" + next + "§7.");
        p.sendMessage(Main.prefix + "§7Du wurdest von §c" + k.getName() + " §7getötet!");
        k.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 2, false, false));
        k.playSound(k.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        Bukkit.broadcastMessage(Main.prefix + "§7Der Spieler §c" + p.getName() + " §7wurde von §a" + k.getName() + " §7getötet!");
        
        Main.instance.stats.addDeath(p.getUniqueId());
        
        if(k != null) {
        	Main.instance.stats.addkill(k.getUniqueId());
        }
        
        if(next == 30) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l30 §5§lerreicht!");
		}else if(next == 40) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l40 §5§lerreicht!");
		}else if(next == 50) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l50 §5§lerreicht!");
		}else if(next == 60) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l60 §5§lerreicht!");
		}else if(next == 70) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l70 §5§lerreicht!");
		}else if(next == 80) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l80 §5§lerreicht!");
		}else if(next == 90) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §e§l90 §5§lerreicht!");
		}else if(next == 100) {
			Bukkit.broadcastMessage(Main.prefix + "§5Der Spieler §e" + k.getName() + " §5hat §5§lLEVEL §6§l100 §5§lerreicht!");
		}
	}
	@EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        Entity damager = e.getDamager();

        if (damager instanceof Player p) {
            attacker = p;
        } else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null) return;

        boolean attackerIn = AllMethods.isInSafezone(attacker.getLocation());
        boolean victimIn = AllMethods.isInSafezone(victim.getLocation());

        if (attackerIn || victimIn) {
            e.setCancelled(true);
            AllMethods.warn(attacker);
        }
    }
}