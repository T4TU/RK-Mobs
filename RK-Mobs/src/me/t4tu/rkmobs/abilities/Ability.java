package me.t4tu.rkmobs.abilities;

import org.bukkit.entity.Entity;

public abstract class Ability {
	
	protected String name;
	protected AbilityID id;
	
	public Ability(String name, AbilityID id) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public AbilityID getID() {
		return id;
	}
	
	public abstract void init(Entity entity);
	
	public abstract boolean canBePerformed(Entity entity);

}