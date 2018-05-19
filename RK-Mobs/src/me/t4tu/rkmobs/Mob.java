package me.t4tu.rkmobs;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.t4tu.rkmobs.abilities.Ability;
import me.t4tu.rkmobs.particles.ParticleEffect;

public class Mob {
	
	private String name;
	private String displayName;
	private int health;
	private int spawnChance;
	private EntityType type;
	private EntityType replaceType;
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack hand;
	private List<Ability> abilities;
	private boolean baby = false;
	private float helmetDropChance = 0;
	private float chestplateDropChance = 0;
	private float leggingsDropChance = 0;
	private float bootsDropChance = 0;
	private float handDropChance = 0;
	private double speed = -1;
	private double attackSpeed = -1;
	private double attackDamage = -1;
	private ParticleEffect particleEffect = null;
	
	public Mob(String name, String displayName, int health, int spawnChance, EntityType type, EntityType replaceType, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand, List<Ability> abilities) {
		this.name = name;
		this.displayName = displayName;
		this.health = health;
		this.spawnChance = spawnChance;
		this.type = type;
		this.replaceType = replaceType;
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.hand = hand;
		this.abilities = abilities;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getSpawnChance() {
		return spawnChance;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public EntityType getReplaceType() {
		return replaceType;
	}
	
	public ItemStack getHelmet() {
		return helmet;
	}
	
	public ItemStack getChestplate() {
		return chestplate;
	}
	
	public ItemStack getLeggings() {
		return leggings;
	}
	
	public ItemStack getBoots() {
		return boots;
	}
	
	public ItemStack getHand() {
		return hand;
	}
	
	public List<Ability> getAbilities() {
		return abilities;
	}
	
	public boolean isBaby() {
		return baby;
	}
	
	public float getHelmetDropChance() {
		return helmetDropChance;
	}
	
	public float getChestplateDropChance() {
		return chestplateDropChance;
	}
	
	public float getLeggingsDropChance() {
		return leggingsDropChance;
	}
	
	public float getBootsDropChance() {
		return bootsDropChance;
	}
	
	public float getHandDropChance() {
		return handDropChance;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public double getAttackSpeed() {
		return attackSpeed;
	}
	
	public double getAttackDamage() {
		return attackDamage;
	}
	
	public ParticleEffect getParticleEffect() {
		return particleEffect;
	}
	
	public void setBaby(boolean baby) {
		this.baby = baby;
	}
	
	public void setHelmetDropChance(float helmetDropChance) {
		this.helmetDropChance = helmetDropChance;
	}
	
	public void setChestplateDropChance(float chestplateDropChance) {
		this.chestplateDropChance = chestplateDropChance;
	}
	
	public void setLeggingsDropChance(float leggingsDropChance) {
		this.leggingsDropChance = leggingsDropChance;
	}
	
	public void setBootsDropChance(float bootsDropChance) {
		this.bootsDropChance = bootsDropChance;
	}
	
	public void setHandDropChance(float handDropChance) {
		this.handDropChance = handDropChance;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void setAttackSpeed(double attackSpeed) {
		this.attackSpeed = attackSpeed;
	}
	
	public void setAttackDamage(double attackDamage) {
		this.attackDamage = attackDamage;
	}
	
	public void setParticleEffect(ParticleEffect particleEffect) {
		this.particleEffect = particleEffect;
	}
}