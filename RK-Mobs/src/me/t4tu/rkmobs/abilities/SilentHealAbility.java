package me.t4tu.rkmobs.abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import me.t4tu.rkmobs.Mobs;

public class SilentHealAbility extends Ability {
	
	private int healAmount;

	public SilentHealAbility(int healAmount) {
		super("Parantuminen", AbilityID.SILENT_HEAL);
		this.healAmount = healAmount;
	}
	
	public void init(Entity entity) {
		LivingEntity e = (LivingEntity) entity;
		if (e.getHealth() + healAmount > e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
			e.setHealth(e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		}
		else {
			e.setHealth(e.getHealth() + healAmount);
		}
		Mobs.getMobManager().updateHealth(e);
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