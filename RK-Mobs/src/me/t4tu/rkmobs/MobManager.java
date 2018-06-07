package me.t4tu.rkmobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.t4tu.rkcore.utils.CoreUtils;
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
		if (entity instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) entity;
			if (e.getType() != mob.getType()) {
				Location location = e.getLocation();
				e.remove();
				e = (LivingEntity) location.getWorld().spawnEntity(location, mob.getType());
			}
			e.setCustomName(mob.getDisplayName() + "§7 " + (int) mob.getHealth() + "❤");
			e.setCustomNameVisible(false);
			e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getHealth());
			e.setHealth(mob.getHealth());
			ItemStack temp = CoreUtils.getItem(Material.WOOD_BUTTON, "§cx", null, 1);
			if (CoreUtils.isNotAir(mob.getHelmet()) || !mob.isCancelVanillaArmor()) {
				e.getEquipment().setHelmet(mob.getHelmet());
			}
			else {
				e.getEquipment().setHelmet(temp);
			}
			if (CoreUtils.isNotAir(mob.getChestplate()) || !mob.isCancelVanillaArmor()) {
				e.getEquipment().setChestplate(mob.getChestplate());
			}
			else {
				e.getEquipment().setChestplate(temp);
			}
			if (CoreUtils.isNotAir(mob.getLeggings()) || !mob.isCancelVanillaArmor()) {
				e.getEquipment().setLeggings(mob.getLeggings());
			}
			else {
				e.getEquipment().setLeggings(temp);
			}
			if (CoreUtils.isNotAir(mob.getBoots()) || !mob.isCancelVanillaArmor()) {
				e.getEquipment().setBoots(mob.getBoots());
			}
			else {
				e.getEquipment().setBoots(temp);
			}
			e.getEquipment().setItemInMainHand(mob.getHand());
			e.getEquipment().setHelmetDropChance(mob.getHelmetDropChance());
			e.getEquipment().setChestplateDropChance(mob.getChestplateDropChance());
			e.getEquipment().setLeggingsDropChance(mob.getLeggingsDropChance());
			e.getEquipment().setBootsDropChance(mob.getBootsDropChance());
			e.getEquipment().setItemInMainHandDropChance(mob.getHandDropChance());
			if (mob.getSpeed() != -1) {
				e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(mob.getSpeed());
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
			e.setSilent(mob.isSilent());
			e.setRemoveWhenFarAway(mob.isRemovedWhenFarAway());
			if (mob.getPotionEffects() != null && !mob.getPotionEffects().isEmpty()) {
				e.addPotionEffects(mob.getPotionEffects());
			}
		}
		else {
			Bukkit.getConsoleSender().sendMessage("Ei voitu spawnata mobia '" + mob.getName() + "', ei ole LivingEntity");
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
									int healAmount = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".abilities." + a + ".heal-amount");
									abilities.add(new HealAbility(healAmount));
								}
								if (a.equalsIgnoreCase("silent-heal")) {
									int healAmount = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".abilities." + a + ".heal-amount");
									abilities.add(new SilentHealAbility(healAmount));
								}
							}
						}
						Mob mob = new Mob(name, displayName, health, spawnChance, type, replaceType, helmet, chestplate, leggings, boots, hand, abilities);
						mob.setBaby(Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".baby", false));
						mob.setSilent(Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".silent", false));
						mob.setRemoveWhenFarAway(Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".remove-when-far-away", true));
						mob.setAlwaysDropFullDurability(Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".always-drop-full-durability", false));
						mob.setCancelVanillaArmor(Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".cancel-vanilla-armor", false));
						mob.setHelmetDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".helmet-drop-chance", 0));
						mob.setChestplateDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".chestplate-drop-chance", 0));
						mob.setLeggingsDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".leggings-drop-chance", 0));
						mob.setBootsDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".boots-drop-chance", 0));
						mob.setHandDropChance((float) Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".hand-drop-chance", 0));
						mob.setSpeed(Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".speed", -1));
						mob.setAttackDamage(Mobs.getPlugin().getConfig().getDouble("mobs." + s + ".attack-damage", -1));
						if (Mobs.getPlugin().getConfig().contains("mobs." + s + ".particle-effect")) {
							String e = Mobs.getPlugin().getConfig().getString("mobs." + s + ".particle-effect");
							for (ParticleEffectID effect : ParticleEffectID.values()) {
								if (effect.toString().equalsIgnoreCase(e)) {
									mob.setParticleEffect(effect.newHandler());
								}
							}
						}
						if (Mobs.getPlugin().getConfig().getConfigurationSection("mobs." + s + ".potion-effects") != null) {
							List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
							for (String e : Mobs.getPlugin().getConfig().getConfigurationSection("mobs." + s + ".potion-effects").getKeys(false)) {
								PotionEffectType potionEffectType = PotionEffectType.getByName(e.toUpperCase());
								int level = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".potion-effects." + e + ".level", 1) - 1;
								int duration = Mobs.getPlugin().getConfig().getInt("mobs." + s + ".potion-effects." + e + ".duration", 10000) * 20;
								boolean ambient = Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".potion-effects." + e + ".ambient", false);
								boolean particles = Mobs.getPlugin().getConfig().getBoolean("mobs." + s + ".potion-effects." + e + ".particles", false);
								PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, level, ambient, particles);
								potionEffects.add(potionEffect);
							}
							mob.setPotionEffects(potionEffects);
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