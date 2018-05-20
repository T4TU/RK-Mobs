package me.t4tu.rkmobs.abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.t4tu.rkmobs.Mobs;

public class HealAbility extends Ability {
	
	private int healAmount;

	public HealAbility(int healAmount) {
		super("Parannusloitsu", AbilityID.HEAL);
		this.healAmount = healAmount;
	}
	
	public void init(Entity entity) {
		new BukkitRunnable() {
			LivingEntity e = (LivingEntity) entity;
			int i = 0;
			public void run() {
				if (entity.isDead()) {
					this.cancel();
				}
				if (i == 10) {
					e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100, false, false));
					e.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
				}
				if (i == 20) {
					e.getWorld().playSound(e.getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 8, 1);
					if (e.getHealth() + healAmount > e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
						e.setHealth(e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
					}
					else {
						e.setHealth(e.getHealth() + healAmount);
					}
					Mobs.getMobManager().updateHealth(e);
				}
				if (i >= 20 && i <= 60 && i % 5 == 0) {
					Location l = e.getLocation();
					for (double i = 0 ; i < Math.PI * 2 ; i = i + Math.PI / 6) {
						e.getWorld().spawnParticle(Particle.CRIT_MAGIC, l.getX() + 0.4 * Math.cos(i), l.getY() + 0.4, l.getZ() + 0.4 * Math.sin(i), 1, 0, 0, 0, 0);
						e.getWorld().spawnParticle(Particle.CRIT_MAGIC, l.getX() + 0.4 * Math.cos(i), l.getY() + 0.8, l.getZ() + 0.4 * Math.sin(i), 1, 0, 0, 0, 0);
						e.getWorld().spawnParticle(Particle.CRIT_MAGIC, l.getX() + 0.4 * Math.cos(i), l.getY() + 1.3, l.getZ() + 0.4 * Math.sin(i), 1, 0, 0, 0, 0);
					}
				}
				if (i >= 60) {
					e.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
					this.cancel();
				}
				i++;
			}
		}.runTaskTimer(Mobs.getPlugin(), 0, 1);
	}
	
	public boolean canBePerformed(Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) entity;
			if (e.getHealth() < e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
				return true;
			}
		}
		return false;
	}
}