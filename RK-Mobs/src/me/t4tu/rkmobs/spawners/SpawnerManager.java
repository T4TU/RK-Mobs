package me.t4tu.rkmobs.spawners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.t4tu.rkmobs.Mob;
import me.t4tu.rkmobs.Mobs;

public class SpawnerManager {
	
	private List<Spawner> spawners = new ArrayList<Spawner>();
	
	public List<Spawner> getSpawners() {
		return spawners;
	}
	
	public Spawner getSpawner(String name) {
		for (Spawner spawner : spawners) {
			if (spawner.getName().equalsIgnoreCase(name)) {
				return spawner;
			}
		}
		return null;
	}
	
	public void tickSpawners() {
		List<Location> playerLocations = new ArrayList<Location>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			playerLocations.add(player.getLocation());
		}
		if (!playerLocations.isEmpty()) {
			for (Spawner spawner : spawners) {
				for (SubSpawner subSpawner : spawner.getSubSpawners()) {
					subSpawner.setLastSpawn(subSpawner.getLastSpawn() + 1);
					Location location = subSpawner.getLocation();
					for (Location playerLocation : playerLocations) {
						if (location.getWorld() == playerLocation.getWorld() && location.distance(playerLocation) <= spawner.getActivationRange()) {
							spawn(subSpawner);
						}
					}
				}
			}
		}
	}
	
	public void loadSpawnersFromConfig() {
		spawners.clear();
		if (Mobs.getPlugin().getConfig().getConfigurationSection("spawners") != null) {
			for (String s : Mobs.getPlugin().getConfig().getConfigurationSection("spawners").getKeys(false)) {
				try {
					if (!Mobs.getPlugin().getConfig().getBoolean("spawners." + s + ".disabled")) {
						String name = s;
						String mobName = Mobs.getPlugin().getConfig().getString("spawners." + s + ".mob");
						Mob mob = Mobs.getMobManager().getMob(mobName);
						if (mob == null) {
							Bukkit.getConsoleSender().sendMessage("Ei löydetty mobia spawnerille '" + s + "'");
							continue;
						}
						int activationRange = Mobs.getPlugin().getConfig().getInt("spawners." + s + ".activation-range");
						int spawnRange = Mobs.getPlugin().getConfig().getInt("spawners." + s + ".spawn-range");
						int spawnDelay = Mobs.getPlugin().getConfig().getInt("spawners." + s + ".spawn-delay");
						int maxRange = Mobs.getPlugin().getConfig().getInt("spawners." + s + ".max-range");
						int maxAmount = Mobs.getPlugin().getConfig().getInt("spawners." + s + ".max-amount");
						List<Location> locations = new ArrayList<Location>();
						List<String> locationStrings = Mobs.getPlugin().getConfig().getStringList("spawners." + s + ".locations");
						for (String string : locationStrings) {
							String[] l = string.split("§");
							Location location = new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]));
							locations.add(location);
						}
						Spawner spawner = new Spawner(name, mob, activationRange, spawnRange, spawnDelay, maxRange, maxAmount, locations);
						spawners.add(spawner);
					}
				}
				catch (Exception e) {
					Bukkit.getConsoleSender().sendMessage("Virhe ladattaessa spawneria '" + s + "'");
				}
			}
		}
	}
	
	private void spawn(SubSpawner subSpawner) {
		Spawner spawner = subSpawner.getParent();
		Location location = subSpawner.getLocation();
		if (subSpawner.getLastSpawn() >= spawner.getSpawnDelay()) {
			int i = 0;
			for (Entity entity : location.getWorld().getNearbyEntities(location, spawner.getMaxRange(), spawner.getMaxRange(), spawner.getMaxRange())) {
				Mob mob = Mobs.getMobManager().getMob(entity);
				if (mob != null && mob.equals(spawner.getMob())) {
					i++;
				}
			}
			if (i < spawner.getMaxAmount()) {
				Random r = new Random();
				if (spawner.getSpawnDelay() != 0) {
					i = spawner.getMaxAmount() - 1;
				}
				for (int c = 0; c < spawner.getMaxAmount() - i; c++) {
					int x = r.nextInt(spawner.getSpawnRange() * 2 + 1) - spawner.getSpawnRange();
					int z = r.nextInt(spawner.getSpawnRange() * 2 + 1) - spawner.getSpawnRange();
					int y = location.getWorld().getHighestBlockYAt(location.getBlockX() + x, location.getBlockZ() + z);
					Entity replaceEntity = location.getWorld().spawnEntity(new Location(location.getWorld(), location.getBlockX() + x, y, location.getBlockZ() + z), spawner.getMob().getType());
					Mobs.getMobManager().spawnMob(spawner.getMob(), replaceEntity);
				}
				subSpawner.setLastSpawn(0);
			}
		}
	}
}