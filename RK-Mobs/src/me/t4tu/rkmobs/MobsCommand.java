package me.t4tu.rkmobs;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.t4tu.rkcore.utils.CoreUtils;

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
				Player p = (Player)sender;
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
						else if (args[0].equalsIgnoreCase("edit")) {
							if (args.length >= 3) {
								boolean cannotBeBoolean = false;
								String value = "";
								for (int i = 2; i < args.length; i++) {
									value = value + " " + args[i];
								}
								value = value.trim();
								if (value.startsWith("'") && value.endsWith("'")) {
									cannotBeBoolean = true;
									value = value.substring(1, value.length() - 1);
								}
								if (value.equalsIgnoreCase("null")) {
									Mobs.getPlugin().getConfig().set(args[1], null);
									Mobs.getPlugin().saveConfig();
									sender.sendMessage(tc2 + "Asetettiin polun " + tc1 + args[1] + tc2 + " arvoksi " + tc1 + "null" + tc2 + " (null)");
									return true;
								}
								else if (value.equalsIgnoreCase("true") && !cannotBeBoolean) {
									Mobs.getPlugin().getConfig().set(args[1], true);
									Mobs.getPlugin().saveConfig();
									sender.sendMessage(tc2 + "Asetettiin polun " + tc1 + args[1] + tc2 + " arvoksi " + tc1 + "true" + tc2 + " (boolean)");
									return true;
								}
								else if (value.equalsIgnoreCase("false") && !cannotBeBoolean) {
									Mobs.getPlugin().getConfig().set(args[1], false);
									Mobs.getPlugin().saveConfig();
									sender.sendMessage(tc2 + "Asetettiin polun " + tc1 + args[1] + tc2 + " arvoksi " + tc1 + "false" + tc2 + " (boolean)");
									return true;
								}
								try {
									int i = Integer.parseInt(value);
									Mobs.getPlugin().getConfig().set(args[1], i);
									Mobs.getPlugin().saveConfig();
									sender.sendMessage(tc2 + "Asetettiin polun " + tc1 + args[1] + tc2 + " arvoksi " + tc1 + i + tc2 + " (int)");
								}
								catch (NumberFormatException e) {
									try {
										double d = Double.parseDouble(value);
										Mobs.getPlugin().getConfig().set(args[1], d);
										Mobs.getPlugin().saveConfig();
										sender.sendMessage(tc2 + "Asetettiin polun " + tc1 + args[1] + tc2 + " arvoksi " + tc1 + d + tc2 + " (double)");
									}
									catch (NumberFormatException e2) {
										Mobs.getPlugin().getConfig().set(args[1], value);
										Mobs.getPlugin().saveConfig();
										sender.sendMessage(tc2 + "Asetettiin polun " + tc1 + args[1] + tc2 + " arvoksi '" + tc1 + value + tc2 + "' (String)");
									}
								}
							}
							else {
								p.sendMessage(usage + "/mobs edit <polku> <arvo>");
							}
						}
						else {
							p.sendMessage(usage + "/mobs <add/remove/spawn/edit/reload>");
						}
					}
					else {
						p.sendMessage(usage + "/mobs <add/remove/spawn/edit/reload>");
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