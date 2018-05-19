package me.t4tu.rkmobs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityDamageEvent;

public class MobsListener implements Listener {
	
	private static final List<SpawnReason> reasons = Arrays.asList(SpawnReason.CHUNK_GEN, SpawnReason.NATURAL, SpawnReason.ENDER_PEARL, SpawnReason.DISPENSE_EGG, SpawnReason.SLIME_SPLIT, 
			SpawnReason.LIGHTNING, SpawnReason.JOCKEY, SpawnReason.REINFORCEMENTS, SpawnReason.SPAWNER, SpawnReason.CHUNK_GEN, SpawnReason.SILVERFISH_BLOCK, SpawnReason.MOUNT, SpawnReason.EGG);
	
	@EventHandler
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
	
	@EventHandler
	public void onMobDamage(EntityDamageEvent e) {
		new BukkitRunnable() {
			public void run() {
				if (Mobs.getMobManager().getMob(e.getEntity()) != null) {
					Mob mob = Mobs.getMobManager().getMob(e.getEntity());
					LivingEntity entity = (LivingEntity)e.getEntity();
					entity.setCustomName(mob.getDisplayName() + "§7 " + (int) entity.getHealth() + "♥");
				}
			}
		}.runTaskLater(Mobs.getPlugin(), 1);
	}
}