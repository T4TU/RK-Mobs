package me.t4tu.rkmobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.t4tu.rkmobs.abilities.Ability;
import me.t4tu.rkmobs.abilities.HealAbility;
import me.t4tu.rkmobs.abilities.SilentHealAbility;
import me.t4tu.rkmobs.particles.ParticleEffectID;
import net.md_5.bungee.api.ChatColor;

public class MobManager {
	
	private List<Mob> mobs = new ArrayList<Mob>();
	private List<Integer> alreadyTickedAbilities = new ArrayList<Integer>();
	private List<Integer> alreadyTickedParticleEffects = new ArrayList<Integer>();
	private List<Integer> abilityCooldown = new ArrayList<Integer>();
	private List<Integer> particleEffectCooldown = new ArrayList<Integer>();
	
	public List<Mob> getMobs() {
		return mobs;
	}
	
	public Mob getMob(Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) entity;
			if (e.getCustomName() != null) {
				for (Mob mob : mobs) {
					if (e.getType() == mob.getType()) {
						if (e.getCustomName().split("§7 ")[0].equals(mob.getDisplayName())) {
							if (e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() == mob.getHealth()) {
								return mob;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public Mob getMob(String mob) {
		for (Mob m : mobs) {
			if (m.getName().equalsIgnoreCase(mob)) {
				return m;
			}
		}
		return null;
	}
	
	public List<Mob> getMobsByReplaceType(EntityType type) {
		List<Mob> mobsByType = new ArrayList<Mob>();
		for (Mob mob : mobs) {
			if (mob.getReplaceType() == type) {
				mobsByType.add(mob);
			}
		}
		return mobsByType;
	}
	
	public void tickAbilities() {
		alreadyTickedAbilities.clear();
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Entity entity : player.getNearbyEntities(16, 16, 16)) {
				if (!alreadyTickedAbilities.contains(entity.getEntityId())) {
					if (!abilityCooldown.contains(entity.getEntityId())) {
						Mob mob = getMob(entity);
						if (mob != null) {
							
							// Tick
							
							Ability ability = getAbilityToBeExecuted(mob.getAbilities(), entity);
							
							if (ability != null) {
								ability.init(entity);
							}
							
							//
							
							alreadyTickedAbilities.add(entity.getEntityId());
						}
					}
				}
			}
		}
	}
	
	public void tickParticleEffects() {
		alreadyTickedParticleEffects.clear();
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Entity entity : player.getNearbyEntities(16, 16, 16)) {
				if (!alreadyTickedParticleEffects.contains(entity.getEntityId())) {
					if (!particleEffectCooldown.contains(entity.getEntityId())) {
						Mob mob = getMob(entity);
						if (mob != null) {
							
							// Tick
							
							if (mob.getParticleEffect() != null) {
								mob.getParticleEffect().tick(entity);
							}
							
							//
							
							alreadyTickedParticleEffects.add(entity.getEntityId());
						}
					}
				}
			}
		}
	}
	
	public void tickNametags() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				updateNametag(entity);
			}
		}
	}
	
	public void updateNametag(Entity entity) {
		if (getMob(entity) != null) {
			boolean b = true;
			for (Player player : entity.getWorld().getPlayers()) {
				if (player.getLocation().distance(entity.getLocation()) <= 16) {
					entity.setCustomNameVisible(true);
					b = false;
				}
			}
			if (b) {
				entity.setCustomNameVisible(false);
			}
		}
	}
	
	public void updateHealth(Entity entity) {
		Mob mob = getMob(entity);
		if (mob != null) {
			LivingEntity e = (LivingEntity)entity;
			e.setCustomName(mob.getDisplayName() + "§7 " + (int) e.getHealth() + "❤");
		}
	}
	
	public Ability getAbilityToBeExecuted(List<Ability> abilities, Entity entity) {
		List<Ability> performableAbilities = new ArrayList<Ability>();
		for (Ability ability : abilities) {
			if (ability.canBePerformed(entity)) {
				performableAbilities.add(ability);
			}
		}
		if (performableAbilities.size() > 0) {
			int i = new Random().nextInt(performableAbilities.size());
			return performableAbilities.get(i);
		}
		return null;
	}
	
	public Mob getMobToSpawn(List<Mob> mobs) {
		List<Mob> mobsSpawnChances = new ArrayList<Mob>();
		for (Mob mob : mobs) {
			for (int i = 0 ; i < mob.getSpawnChance() ; i++) {
				mobsSpawnChances.add(mob);
			}
		}
		if (mobsSpawnChances.size() > 0) {
			int i = new Random().nextInt(mobsSpawnChances.size());
			return mobsSpawnChances.get(i);
		}
		return null;
	}
	
	public void spawnMob(Mob mob, Entity entity) {
		LivingEntity e = (LivingEntity) entity;
		if (e.getType() != mob.getType()) {
			Location location = e.getLocation();
			e.remove();
			e = (LivingEntity) location.getWorld().spawnEntity(location, mob.getType());
		}
		e.setCustomName(mob.getDisplayName() + "§7 " + (int) mob.getHealth() + "❤");
		e.setCustomNameVisible(true);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getHealth());
		e.setHealth(mob.getHealth());
		e.getEquipment().setHelmet(mob.getHelmet());
		e.getEquipment().setChestplate(mob.getChestplate());
		e.getEquipment().setLeggings(mob.getLeggings());
		e.getEquipment().setBoots(mob.getBoots());
		e.getEquipment().setItemInMainHand(mob.getHand());
		e.getEquipment().setHelmetDropChance(mob.getHelmetDropChance());
		e.getEquipment().setChestplateDropChance(mob.getChestplateDropChance());
		e.getEquipment().setLeggingsDropChance(mob.getLeggingsDropChance());
		e.getEquipment().setBootsDropChance(mob.getBootsDropChance());
		e.getEquipment().setItemInMainHandDropChance(mob.getHandDropChance());
		if (mob.getSpeed() != -1) {
			e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(mob.getSpeed());
		}
		if (mob.getAttackSpeed() != -1) {
			e.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(mob.getAttackSpeed());
		}
		if (mob.getAttackDamage() != -1) {
			e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(mob.getAttackDamage());
		}
		if (e instanceof Ageable) {
			Ageable ageable = (Ageable) e;
			if (mob.isBaby()) {
				ageable.setBaby();
			}
			else {
				ageable.setAdult();
			}
		}
	}
	
	public void loadMobsFromConfig() {
		mobs.clear();
		if (Mobs.getPlugin().getConfig().getConfigurationSection("mobs") != null) {
			for (String s : Mobs.getPlugin().getConfig().getConfigurationSection("mobs").getKeys(false)) {
				try {
					if (!Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".disabled")) {
						String name = s;
						String displayName = ChatColor.translateAlternateColorCodes('&', Mobs.getPlugin().getConfig().getString("mobs." + s + ".displayname").replace("_", " "));
						int health = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".health");
						int spawnChance = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".spawnchance");
						EntityType type = EntityType.valueOf(Mobs.getPlugin().getConfig().getString("mobs." + s + ".type").toUpperCase());
						EntityType replaceType = EntityType.valueOf(Mobs.getPlugin().getConfig().getString("mobs." + s + ".replacetype").toUpperCase());
						ItemStack helmet = (ItemStack) Mobs.getPlugin().getConfig().get("mobs." + s + ".helmet");
						ItemStack chestplate = (ItemStack) Mobs.getPlugin().getConfig().get("mobs." + s + ".chestplate");
						ItemStack leggings = (ItemStack) Mobs.getPlugin().getConfig().get("mobs." + s + ".leggings");
						ItemStack boots = (ItemStack) Mobs.getPlugin().getConfig().get("mobs." + s + ".boots");
						ItemStack hand = (ItemStack) Mobs.getPlugin().getConfig().get("mobs." + s + ".hand");
						List<Ability> abilities = new ArrayList<Ability>();
						if (Mobs.getPlugin().getConfig().getConfigurationSection("mobs." + s + ".abilities") != null) {
							for (String a : Mobs.getPlugin().getConfig().getConfigurationSection("mobs." + s + ".abilities").getKeys(false)) {
								if (a.equalsIgnoreCase("heal")) {
									int healAmount = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".abilities." + a + ".healamount");
									abilities.add(new HealAbility(healAmount));
								}
								if (a.equalsIgnoreCase("silent-heal")) {
									int healAmount = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".abilities." + a + ".healamount");
									abilities.add(new SilentHealAbility(healAmount));
								}
							}
						}
						Mob mob = new Mob(name, displayName, health, spawnChance, type, replaceType, helmet, chestplate, leggings, boots, hand, abilities);
						if (Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".baby")) {
							mob.setBaby(true);
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".helmetdropchance")) {
							mob.setHelmetDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".helmetdropchance"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".chestplatedropchance")) {
							mob.setChestplateDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".chestplatedropchance"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".leggingsdropchance")) {
							mob.setLeggingsDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".leggingsdropchance"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".bootsdropchance")) {
							mob.setBootsDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".bootsdropchance"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".handdropchance")) {
							mob.setHandDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".handdropchance"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".speed")) {
							mob.setSpeed(Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".speed"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".attackspeed")) {
							mob.setAttackSpeed(Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".attackspeed"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".attackdamage")) {
							mob.setAttackDamage(Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".attackdamage"));
						}
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".particleeffect")) {
							String e = Mobs.getPlugin().getConfig().getString("mobs." + s + ".particleeffect");
							for (ParticleEffectID effect : ParticleEffectID.values()) {
								if (effect.toString().equalsIgnoreCase(e)) {
									mob.setParticleEffect(effect.newHandler());
								}
							}
						}
						mobs.add(mob);
					}
				}
				catch (Exception e) {
					Bukkit.getConsoleSender().sendMessage("Virhe ladattaessa mobia '" + s + "'");
				}
			}
		}
	}
}