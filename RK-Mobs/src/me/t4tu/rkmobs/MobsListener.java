package me.t4tu.rkmobs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.t4tu.rkcore.utils.CoreUtils;

public class MobsListener implements Listener {
	
	private static final List<SpawnReason> reasons = Arrays.asList(SpawnReason.CHUNK_GEN, SpawnReason.NATURAL, SpawnReason.ENDER_PEARL, SpawnReason.DISPENSE_EGG, SpawnReason.SLIME_SPLIT, 
			SpawnReason.LIGHTNING, SpawnReason.JOCKEY, SpawnReason.REINFORCEMENTS, SpawnReason.SPAWNER, SpawnReason.SILVERFISH_BLOCK, SpawnReason.MOUNT, SpawnReason.EGG);
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (reasons.contains(e.getSpawnReason())) {
			List<Mob> mobs = Mobs.getMobManager().getMobsByReplaceType(e.getEntityType());
			if (mobs.size() > 0) {
				Mob mob = Mobs.getMobManager().getMobToSpawn(mobs);
				if (mob != null) {
					new BukkitRunnable() {
						public void run() {
							Mobs.getMobManager().spawnMob(mob, e.getEntity());
						}
					}.runTaskLater(Mobs.getPlugin(), 2);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMobDeath(EntityDeathEvent e) {
		Mob mob = Mobs.getMobManager().getMob(e.getEntity());
		if (mob != null) {
			for (ItemStack item : e.getDrops()) {
				if (mob.isAlwaysDropFullDurability()) {
					item.setDurability((short) 0);
				}
				if (CoreUtils.getDisplayName(item).equals("§cx") && item.getType() == Material.OAK_BUTTON) {
					item.setAmount(0);
				}
			}
		}
	}
	
	@EventHandler
	public void onMobDamage(EntityDamageEvent e) {
		new BukkitRunnable() {
			public void run() {
				if (Mobs.getMobManager().getMob(e.getEntity()) != null) {
					Mob mob = Mobs.getMobManager().getMob(e.getEntity());
					LivingEntity entity = (LivingEntity) e.getEntity();
					entity.setCustomName(mob.getDisplayName() + "§7 " + (int) entity.getHealth() + "♥");
				}
			}
		}.runTaskLater(Mobs.getPlugin(), 1);
	}
	
	@EventHandler
	public void onMobRegeinHealth(EntityRegainHealthEvent e) {
		new BukkitRunnable() {
			public void run() {
				if (Mobs.getMobManager().getMob(e.getEntity()) != null) {
					Mob mob = Mobs.getMobManager().getMob(e.getEntity());
					LivingEntity entity = (LivingEntity) e.getEntity();
					entity.setCustomName(mob.getDisplayName() + "§7 " + (int) entity.getHealth() + "♥");
				}
			}
		}.runTaskLater(Mobs.getPlugin(), 1);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Mob mob = Mobs.getMobManager().getMob(e.getRightClicked());
		if (mob != null && mob.getType() == EntityType.VILLAGER) {
			e.setCancelled(true);
		}
	}
}