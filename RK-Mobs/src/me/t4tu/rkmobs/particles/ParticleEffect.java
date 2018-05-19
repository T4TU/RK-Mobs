package me.t4tu.rkmobs.particles;

import org.bukkit.entity.Entity;

public abstract class ParticleEffect {
	
	protected String name;
	protected ParticleEffectID id;
	
	public ParticleEffect(String name, ParticleEffectID id) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public ParticleEffectID getID() {
		return id;
	}
	
	public abstract void tick(Entity entity);

}