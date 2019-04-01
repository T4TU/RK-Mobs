package me.t4tu.rkmobs.spawners;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.t4tu.rkcore.utils.CoreUtils;
import me.t4tu.rkmobs.Mobs;

public class SpawnerCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String tc1 = CoreUtils.getHighlightColor();
		String tc2 = CoreUtils.getBaseColor();
		String tc3 = CoreUtils.getErrorBaseColor();
		
		String usage = CoreUtils.getUsageString();
		String noPermission = CoreUtils.getNoPermissionString();
		String playersOnly = CoreUtils.getPlayersOnlyString();
		
		if (cmd.getName().equalsIgnoreCase("spawner")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (CoreUtils.hasRank(p, "ylläpitäjä")) {
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("add")) {
							if (args.length >= 9) {
								try {
									String name = args[1];
									String mob = args[2];
									int activationRange = Integer.parseInt(args[3]);
									int spawnRange = Integer.parseInt(args[4]);
									int spawnDelay = Integer.parseInt(args[5]);
									int maxRange = Integer.parseInt(args[6]);
									int maxAmount = Integer.parseInt(args[7]);
									int maxHeight = Integer.parseInt(args[8]);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".mob", mob);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".activation-range", activationRange);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".spawn-range", spawnRange);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".spawn-delay", spawnDelay);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".max-range", maxRange);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".max-amount", maxAmount);
									Mobs.getPlugin().getConfig().set("spawners." + name + ".max-height", maxHeight);
									Mobs.getPlugin().saveConfig();
									Mobs.getSpawnerManager().loadSpawnersFromConfig();
									p.sendMessage(tc2 + "Lisättiin uusi spawneri " + tc1 + name + tc2 + "!");
								}
								catch (NumberFormatException e) {
									p.sendMessage(tc3 + "Virheelliset argumentit!");
								}
							}
							else {
								p.sendMessage(usage + "/spawner add <nimi> <mobi> <activationRange> <spawnRange> <spawnDelay> <maxRange> <maxAmount> <maxHeight>");
							}
						}
						else if (args[0].equalsIgnoreCase("remove")) {
							if (args.length >= 2) {
								if (Mobs.getPlugin().getConfig().contains("spawners." + args[1])) {
									Mobs.getPlugin().getConfig().set("spawners." + args[1], null);
									Mobs.getPlugin().saveConfig();
									Mobs.getSpawnerManager().loadSpawnersFromConfig();
									p.sendMessage(tc2 + "Poistettiin spawneri " + tc1 + args[1] + tc2 + "!");
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty spawneria antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/spawner remove <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("list")) {
							p.sendMessage("");
							p.sendMessage(tc2 + "§m----------" + tc1 + " Spawnerit " + tc2 + "§m----------");
							p.sendMessage("");
							if (!Mobs.getSpawnerManager().getSpawners().isEmpty()) {
								for (Spawner spawner : Mobs.getSpawnerManager().getSpawners()) {
									p.sendMessage(tc2 + " - " + tc1 + spawner.getName() + tc2 + " (" + spawner.getSubSpawners().size() + ")");
								}
							}
							else {
								p.sendMessage(tc3 + " Ei spawnereita!");
							}
							p.sendMessage("");
						}
						else if (args[0].equalsIgnoreCase("info")) {
							if (args.length >= 2) {
								Spawner spawner = Mobs.getSpawnerManager().getSpawner(args[1]);
								if (spawner != null) {
									p.sendMessage("");
									p.sendMessage(tc2 + "§m----------" + tc1 + " Spawneri " + tc2 + "§m----------");
									p.sendMessage("");
									p.sendMessage(tc1 + " Nimi: " + tc2 + spawner.getName());
									p.sendMessage(tc1 + " Mobi: " + tc2 + spawner.getMob().getName() + " (" + spawner.getMob().getDisplayName() + tc2 + ")");
									p.sendMessage(tc1 + " activationRange: " + tc2 + spawner.getActivationRange());
									p.sendMessage(tc1 + " spawnRange: " + tc2 + spawner.getSpawnRange());
									p.sendMessage(tc1 + " spawnDelay: " + tc2 + spawner.getSpawnDelay());
									p.sendMessage(tc1 + " maxRange: " + tc2 + spawner.getMaxRange());
									p.sendMessage(tc1 + " maxAmount: " + tc2 + spawner.getMaxAmount());
									p.sendMessage(tc1 + " maxHeight: " + tc2 + spawner.getMaxHeight());
									p.sendMessage("");
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty spawneria antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/spawner info <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("listloc")) {
							if (args.length >= 2) {
								Spawner spawner = Mobs.getSpawnerManager().getSpawner(args[1]);
								if (spawner != null) {
									p.sendMessage("");
									p.sendMessage(tc2 + "§m----------" + tc1 + " Spawneri " + tc2 + "§m----------");
									p.sendMessage("");
									p.sendMessage(tc1 + " Nimi: " + tc2 + spawner.getName());
									p.sendMessage(tc1 + " Sijainnit: " + tc2 + "(" + spawner.getSubSpawners().size() + tc2 + ")");
									p.sendMessage("");
									if (!spawner.getSubSpawners().isEmpty()) {
										for (SubSpawner subSpawner : spawner.getSubSpawners()) {
											p.sendMessage(tc2 + "  - " + tc1 + subSpawner.getLocation().getWorld().getName() + tc2 + ", " + tc1 + subSpawner.getLocation().getBlockX() + tc2 + ", " + 
													tc1 + subSpawner.getLocation().getBlockY() + tc2 + ", " + tc1 + subSpawner.getLocation().getBlockZ());
										}
									}
									else {
										p.sendMessage(tc3 + "  Ei sijainteja!");
									}
									p.sendMessage("");
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty spawneria antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/spawner listloc <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("addloc")) {
							if (args.length >= 2) {
								if (Mobs.getPlugin().getConfig().contains("spawners." + args[1])) {
									List<String> locations = Mobs.getPlugin().getConfig().getStringList("spawners." + args[1] + ".locations");
									String location = p.getWorld().getName() + "§" + p.getLocation().getBlockX() + "§" + p.getLocation().getBlockY() + "§" + p.getLocation().getBlockZ();
									locations.add(location);
									Mobs.getPlugin().getConfig().set("spawners." + args[1] + ".locations", locations);
									Mobs.getPlugin().saveConfig();
									Mobs.getSpawnerManager().loadSpawnersFromConfig();
									p.sendMessage(tc2 + "Lisättiin sijainti spawneriin " + tc1 + args[1] + tc2 + "!");
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty spawneria antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/spawner addloc <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("removeloc")) {
							if (args.length >= 2) {
								if (Mobs.getPlugin().getConfig().contains("spawners." + args[1])) {
									List<String> locations = Mobs.getPlugin().getConfig().getStringList("spawners." + args[1] + ".locations");
									String location = p.getWorld().getName() + "§" + p.getLocation().getBlockX() + "§" + p.getLocation().getBlockY() + "§" + p.getLocation().getBlockZ();
									if (locations.contains(location)) {
										locations.remove(location);
										Mobs.getPlugin().getConfig().set("spawners." + args[1] + ".locations", locations);
										Mobs.getPlugin().saveConfig();
										Mobs.getSpawnerManager().loadSpawnersFromConfig();
										p.sendMessage(tc2 + "Poistettiin sijainti spawnerista " + tc1 + args[1] + tc2 + "!");
									}
									else {
										p.sendMessage(tc3 + "Tässä sijainnissa ei ole spawneria!");
									}
								}
								else {
									p.sendMessage(tc3 + "Ei löydetty spawneria antamallasi nimellä!");
								}
							}
							else {
								p.sendMessage(usage + "/spawner removeloc <nimi>");
							}
						}
						else if (args[0].equalsIgnoreCase("reload")) {
							Mobs.getPlugin().reloadConfig();
							Mobs.getSpawnerManager().loadSpawnersFromConfig();
							p.sendMessage(tc2 + "Uudelleenladattiin kaikki spawnerit!");
						}
						else {
							p.sendMessage(usage + "/spawner add/remove/list/info/listloc/addloc/removeloc/reload");
						}
					}
					else {
						p.sendMessage(usage + "/spawner add/remove/list/info/listloc/addloc/removeloc/reload");
					}
				}
				else {
					p.sendMessage(noPermission);
				}
			}
			else {
				sender.sendMessage(playersOnly);
			}
		}
		return true;
	}
}