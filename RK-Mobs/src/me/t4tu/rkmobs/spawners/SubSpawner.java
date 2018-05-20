package me.t4tu.rkmobs.spawners;

import org.bukkit.Location;

public class SubSpawner {
	
	private Spawner parent;
	private Location location;
	private int lastSpawn;
	
	public SubSpawner(Spawner parent, Location location) {
		this.parent = parent;
		this.location = location;
		lastSpawn = 0;
	}
	
	public Spawner getParent() {
		return parent;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public int getLastSpawn() {
		return lastSpawn;
	}
	
	public void setLastSpawn(int lastSpawn) {
		this.lastSpawn = lastSpawn;
	}
}