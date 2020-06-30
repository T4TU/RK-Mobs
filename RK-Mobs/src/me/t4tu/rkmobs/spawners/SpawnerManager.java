package me.t4tu.rkmobs.spawners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
						int maxHeight = Mobs.getPlugin().getConfig().getInt("spawners." + s + ".max-height");
						List<Location> locations = new ArrayList<Location>();
						List<String> locationStrings = Mobs.getPlugin().getConfig().getStringList("spawners." + s + ".locations");
						for (String string : locationStrings) {
							String[] l = string.split("§");
							Location location = new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]));
							locations.add(location);
						}
						Spawner spawner = new Spawner(name, mob, activationRange, spawnRange, spawnDelay, maxRange, maxAmount, maxHeight, locations);
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
					Location l = getSpawnLocation(subSpawner, r);
					if (l != null) {
						LivingEntity replaceEntity = (LivingEntity) location.getWorld().spawnEntity(l, spawner.getMob().getType());
						location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l.getX(), l.getY() + replaceEntity.getEyeHeight() / 2, l.getZ(), 30, 0, 0.5, 0, 0.05);
						Mobs.getMobManager().spawnMob(spawner.getMob(), replaceEntity);
					}
				}
				subSpawner.setLastSpawn(0);
			}
		}
	}
	
	private Location getSpawnLocation(SubSpawner subSpawner, Random r) {
		Spawner spawner = subSpawner.getParent();
		Location location = subSpawner.getLocation();
		if (spawner.getMaxHeight() >= 0) {
			for (int t = 0; t < 10; t++) {
				int x = r.nextInt(spawner.getSpawnRange() * 2 + 1) - spawner.getSpawnRange();
				int z = r.nextInt(spawner.getSpawnRange() * 2 + 1) - spawner.getSpawnRange();
				for (int y = -2; y < spawner.getMaxHeight(); y++) {
					Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
					Block relative = block.getRelative(BlockFace.UP);
					if (block != null && block.getType() != null && !block.getType().isSolid() && relative != null && relative.getType() != null && !relative.getType().isSolid()) {
						return block.getLocation().clone().add(0.5, 0, 0.5);
					}
				}
			}
		}
		return null;
	}
}