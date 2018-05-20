package me.t4tu.rkmobs.particles;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.material.MaterialData;

public class BloodParticleEffect extends ParticleEffect {
	
	private HashMap<Integer, Integer> timers = new HashMap<Integer, Integer>();
	
	public BloodParticleEffect() {
		super("Haavoittunut", ParticleEffectID.BLOOD);
	}
	
	public void tick(Entity entity) {
		if (!timers.containsKey(entity.getEntityId())) {
			timers.put(entity.getEntityId(), 1);
		}
		if (timers.get(entity.getEntityId()) >= 10) {
			int r = new Random().nextInt(3) + 1;
			MaterialData materialData = new MaterialData(Material.REDSTONE_BLOCK);
			entity.getWorld().spawnParticle(Particle.BLOCK_DUST, entity.getLocation().getX(), entity.getLocation().getY() + 1.25, entity.getLocation().getZ(), 
					r, 0.1, 0.2, 0.1, 0.05f, materialData);
			timers.remove(entity.getEntityId());
		}
		else {
			timers.put(entity.getEntityId(), timers.get(entity.getEntityId()) + 1);
		}
	}
}