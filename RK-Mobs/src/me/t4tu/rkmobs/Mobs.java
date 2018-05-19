package me.t4tu.rkmobs;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.t4tu.rkcore.utils.CoreUtils;

public class Mobs extends JavaPlugin {
	
	private static Plugin plugin;
	private static MobManager mobManager = new MobManager();
	private static MobsListener mobsListener = new MobsListener();
	private static MobsCommand mobsCommand = new MobsCommand();
	
	private void registerCommand(String s, CommandExecutor c, boolean tabCompletion) {
		getCommand(s).setExecutor(c);
		if (tabCompletion) {
			CoreUtils.getRegisteredCommandsWithTabCompletion().add(s);
		}
		else {
			CoreUtils.getRegisteredCommands().add(s);
		}
	}
	
	public void onEnable() {
		plugin = this;
		saveConfig();
		mobManager.loadMobsFromConfig();
		Bukkit.getPluginManager().registerEvents(mobsListener, this);
		registerCommand("mobs", mobsCommand, false);
		
		new BukkitRunnable() {
			public void run() {
				mobManager.tickAbilities();
			}
		}.runTaskTimer(plugin, 200, 200);
		
		new BukkitRunnable() {
			public void run() {
				mobManager.tickParticleEffects();
			}
		}.runTaskTimer(plugin, 1, 1);
		
		new BukkitRunnable() {
			public void run() {
				mobManager.tickNametags();
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	public void onDisable() {
		plugin = null;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static MobManager getMobManager() {
		return mobManager;
	}
	
	public static MobsListener getMobsListener() {
		return mobsListener;
	}
	
	public static MobsCommand getMobsCommand() {
		return mobsCommand;
	}
}