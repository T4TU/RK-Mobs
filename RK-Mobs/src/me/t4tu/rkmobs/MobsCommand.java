package me.t4tu.rkmobs;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.t4tu.rkcore.utils.CoreUtils;
import me.t4tu.rkmobs.abilities.Ability;

public class MobsCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String tc1 = CoreUtils.getHighlightColor();
		String tc2 = CoreUtils.getBaseColor();
		String tc3 = CoreUtils.getErrorBaseColor();
		
		String usage = CoreUtils.getUsageString();
		String noPermission = CoreUtils.getNoPermissionString();
		String playersOnly = CoreUtils.getPlayersOnlyString();
		
		if (cmd.getName().equalsIgnoreCase("mobs")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (CoreUtils.hasRank(p, "ylläpitäjä")) {
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("add")) {
							if (args.length >= 7) {
								try {
									String name = args[1];
									String displayName = args[2];
									int health = Integer.parseInt(args[3]);
									int spawnChance = Integer.parseInt(args[4]);
									String type = args[5].toUpperCase();
									String replaceType = args[6].toUpperCase();
									Mobs.getPlugin().getConfig().set("mobs." + name + ".displayname", displayName);
									Mobs.getPlugin().getConfig().set("mobs." + name + ".health", health);
									Mobs.getPlugin().getConfig().set("mobs." + name + ".spawnchance", spawnChance);
									Mobs.getPlugin().getConfig().set("mobs." + name + ".type", type);
									Mobs.getPlugin().getConfig().set("mobs." + name + ".replacetype", replaceType);
									if (p.getInventory().getHelmet() != null) {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".helmet", p.getInventory().getHelmet());
									}
									else {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".helmet", new ItemStack(Material.AIR));
									}
									if (p.getInventory().getChestplate() != null) {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".chestplate", p.getInventory().getChestplate());
									}
									else {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".chestplate", new ItemStack(Material.AIR));
									}
									if (p.getInventory().getLeggings() != null) {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".leggings", p.getInventory().getLeggings());
									}
									else {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".leggings", new ItemStack(Material.AIR));
									}
									if (p.getInventory().getBoots() != null) {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".boots", p.getInventory().getBoots());
									}
									else {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".boots", new ItemStack(Material.AIR));
									}
									if (p.getInventory().getItemInMainHand() != null) {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".hand", p.getInventory().getItemInMainHand());
									}
									else {
										Mobs.getPlugin().getConfig().set("mobs." + name + ".hand", new ItemStack(Material.AIR));
									}
									Mobs.getPlugin().saveConfig();
									Mobs.getMobManager().loadMobsFromConfig();
									p.sendMessage(tc2 + "Lisättiin uusi mobi " + tc1 + name + tc2 + "!");
								}
								catch (Exception ex) {
									p.sendMessage(tc3 + "Virheelliset argumentit!");
								}
							}
							else {
								p.sendMessage(usage + "/mobs add <nimi> <näytettävä nimi> <elämät> <spawnaamisen todennäköisyys> <tyyppi> <korvattava tyyppi>");
							}
						}
						else if (args[0].equalsIgnoreCase("remove")) {
							if (args.length >= 2) {
								if (Mobs.getPlugin().getConfig().contains("mobs." + args[1])) {
									Mobs.getPlugin().getConfig().set("mobs." + args[1], null);
									Mobs.getPlugin().saveConfig();
									Mobs.getMobManager().loadMobsFromConfig();
									p.sendMessage(tc2 + "Poistettiin mobi " + tc1 + args[1] + tc2 + "!");
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty mobia antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/mobs remove <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("list")) {
							p.sendMessage("");
							p.sendMessage(tc2 + "§m----------" + tc1 + " Mobit " + tc2 + "§m----------");
							p.sendMessage("");
							if (!Mobs.getMobManager().getMobs().isEmpty()) {
								for (Mob mob : Mobs.getMobManager().getMobs()) {
									p.sendMessage(tc2 + " - " + tc1 + mob.getName() + tc2 + " (" + mob.getDisplayName() + tc2 + ")");
								}
							}
							else {
								p.sendMessage(tc3 + " Ei mobeja!");
							}
							p.sendMessage("");
						}
						else if (args[0].equalsIgnoreCase("info")) {
							if (args.length >= 2) {
								Mob mob = Mobs.getMobManager().getMob(args[1]);
								if (mob != null) {
									p.sendMessage("");
									p.sendMessage(tc2 + "§m----------" + tc1 + " Mobi " + tc2 + "§m----------");
									p.sendMessage("");
									p.sendMessage(tc1 + " Nimi: " + tc2 + mob.getName());
									p.sendMessage(tc1 + " Nametag: " + tc2 + mob.getDisplayName());
									p.sendMessage(tc1 + " Elämät: " + tc2 + mob.getHealth());
									p.sendMessage(tc1 + " Spawnaamisen todennäköisyys: " + tc2 + mob.getSpawnChance());
									p.sendMessage(tc1 + " Tyyppi: " + tc2 + mob.getType().toString());
									p.sendMessage(tc1 + " Korvattava tyyppi: " + tc2 + mob.getReplaceType().toString());
									p.sendMessage(tc1 + " Kädessä oleva esine: " + tc2 + mob.getHand().getType().toString() + " (" + mob.getHandDropChance() + ")");
									p.sendMessage(tc1 + " Päähine: " + tc2 + mob.getHelmet().getType().toString() + " (" + mob.getHelmetDropChance() + ")");
									p.sendMessage(tc1 + " Rintapanssari: " + tc2 + mob.getChestplate().getType().toString() + " (" + mob.getChestplateDropChance() + ")");
									p.sendMessage(tc1 + " Housut: " + tc2 + mob.getLeggings().getType().toString() + " (" + mob.getLeggingsDropChance() + ")");
									p.sendMessage(tc1 + " Jalkineet: " + tc2 + mob.getBoots().getType().toString() + " (" + mob.getBootsDropChance() + ")");
									p.sendMessage(tc1 + " Vauva: " + tc2 + mob.isBaby());
									p.sendMessage(tc1 + " Hiljainen: " + tc2 + mob.isSilent());
									p.sendMessage(tc1 + " Itsestään despawnaava: " + tc2 + mob.isRemovedWhenFarAway());
									p.sendMessage(tc1 + " Tiputa vain ehjiä esineitä: " + tc2 + mob.isAlwaysDropFullDurability());
									p.sendMessage(tc1 + " Estä Vanilla-armorit: " + tc2 + mob.isCancelVanillaArmor());
									p.sendMessage(tc1 + " Nopeus: " + tc2 + mob.getSpeed());
									p.sendMessage(tc1 + " Hyökkäyksen voimakkuus: " + tc2 + mob.getAttackDamage());
									if (mob.getParticleEffect() != null) {
										p.sendMessage(tc1 + " Partikkeliefekti: " + tc2 + mob.getParticleEffect().getName() + " (" + mob.getParticleEffect().getID().toString() + ")");
									}
									else {
										p.sendMessage(tc1 + " Partikkeliefekti: " + tc3 + "Ei partikkeliefektiä");
									}
									if (mob.getPotionEffects() != null && !mob.getPotionEffects().isEmpty()) {
										p.sendMessage(tc1 + " Efektit:");
										for (PotionEffect potionEffect : mob.getPotionEffects()) {
											p.sendMessage(tc2 + "  - " + tc1 + potionEffect.getType().getName() + tc2 + " (Taso: " + (potionEffect.getAmplifier() + 1) + 
													", kesto: " + (potionEffect.getDuration() / 20) + "s, ambient: " + potionEffect.isAmbient() + ", partikkelit: " + potionEffect.hasParticles() + ")");
										}
									}
									else {
										p.sendMessage(tc1 + " Efektit: " + tc3 + "Ei efektejä");
									}
									if (!mob.getAbilities().isEmpty()) {
										p.sendMessage(tc1 + " Taidot:");
										for (Ability ability : mob.getAbilities()) {
											p.sendMessage(tc2 + "  - " + tc1 + ability.getName() + tc2 + " (" + ability.getID().toString() + ")");
										}
									}
									else {
										p.sendMessage(tc1 + " Taidot: " + tc3 + "Ei taitoja");
									}
									p.sendMessage("");
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty mobia antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/mobs info <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("spawn")) {
							if (args.length >= 2) {
								if (Mobs.getMobManager().getMob(args[1]) != null) {
									Mob mob = Mobs.getMobManager().getMob(args[1]);
									Entity entity = p.getWorld().spawnEntity(p.getLocation(), mob.getReplaceType());
									Mobs.getMobManager().spawnMob(mob, entity);
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty mobia antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/mobs spawn <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("reload")) {
							Mobs.getPlugin().reloadConfig();
							Mobs.getMobManager().loadMobsFromConfig();
							p.sendMessage(tc2 + "Uudelleenladattiin kaikki mobit!");
						}
						else {
							p.sendMessage(usage + "/mobs <add/remove/list/info/spawn/reload>");
						}
					}
					else {
						p.sendMessage(usage + "/mobs <add/remove/list/info/spawn/reload>");
					}
				}
				else {
					p.sendMessage(noPermission);
				}
			}
			else {
				sender.sendMessage(tc3 + playersOnly);
			}
		}
		return true;
	}
}