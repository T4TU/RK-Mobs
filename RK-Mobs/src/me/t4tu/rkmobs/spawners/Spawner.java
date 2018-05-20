package me.t4tu.rkmobs.spawners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import me.t4tu.rkmobs.Mob;

public class Spawner {
	
	private String name;
	private Mob mob;
	private int activationRange;
	private int spawnRange;
	private int spawnDelay;
	private int maxRange;
	private int maxAmount;
	private List<SubSpawner> subSpawners;
	
	public Spawner(String name, Mob mob, int activationRange, int spawnRange, int spawnDelay, int maxRange, int maxAmount, List<Location> locations) {
		this.name = name;
		this.mob = mob;
		this.activationRange = activationRange;
		this.spawnRange = spawnRange;
		this.spawnDelay = spawnDelay;
		this.maxRange = maxRange;
		this.maxAmount = maxAmount;
		subSpawners = new ArrayList<SubSpawner>();
		for (Location location : locations) {
			subSpawners.add(new SubSpawner(this, location));
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Mob getMob() {
		return mob;
	}
	
	public int getActivationRange() {
		return activationRange;
	}
	
	public int getSpawnRange() {
		return spawnRange;
	}
	
	public int getSpawnDelay() {
		return spawnDelay;
	}
	
	public int getMaxRange() {
		return maxRange;
	}
	
	public int getMaxAmount() {
		return maxAmount;
	}
	
	public List<SubSpawner> getSubSpawners() {
		return subSpawners;
	}
}