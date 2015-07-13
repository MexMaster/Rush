package epicapple.rush;

import epicapple.rush.commands.LeaveCommand;
import epicapple.rush.config.ConfigHandler;
import epicapple.rush.game.*;
import epicapple.rush.nms.NMSHandler;
import epicapple.rush.rushplayer.RushPlayerManager;
import epicapple.rush.rushplayer.RushPlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class RushPlugin extends JavaPlugin {

	public static final boolean DEBUG = true;

	private GameStarter gameStarter;
	private NMSHandler nmsHandler;
	private ConfigHandler configHandler;
	private StateManager stateManager;
	private TeamManager teamManager;

	private RushPlayerManager rushPlayerManager;

	public void onEnable(){
		load();
		log("aktiviert");
	}

	public void onDisable(){
		unload();
		log("deaktiviert");
	}

	private void load(){
		for (World w : Bukkit.getWorlds()) {
			w.setAutoSave(false);
			w.setGameRuleValue("doDaylightCycle", "false");
			w.setThundering(false);
			for(Entity entity : w.getEntities()){
				entity.remove();
			}
		}

		HashMap<String, Object> configDefaults = new HashMap<>();
		ArrayList<String> list = new ArrayList<>();
		configDefaults.put("maps", list);
		configDefaults.put("mapsettings", list);
		configDefaults.put("settings", list);

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		registerListener(new RushPlayerStorage());
		nmsHandler = new NMSHandler();

		configHandler = new ConfigHandler(this.getConfig(), new File(this.getDataFolder(), "config.yml"), configDefaults);
		stateManager = new StateManager(GameState.RESTARTING);
		teamManager = new TeamManager(this);
		rushPlayerManager = new RushPlayerManager(this, teamManager, nmsHandler);
		gameStarter = new GameStarter(this, nmsHandler, configHandler, stateManager, teamManager);

		//commands
		LeaveCommand leaveCommand = new LeaveCommand();
		this.getCommand("leave").setExecutor(leaveCommand);
		this.getCommand("hub").setExecutor(leaveCommand);

		Bukkit.setWhitelist(false);
	}

	private void unload(){
		Bukkit.setWhitelist(true);
		for(Player p : Bukkit.getOnlinePlayers()){
			p.kickPlayer(Messages.getRestartMessage());
		}
	}

	public void broadcastDelayedMessage(final String message, long time){
		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			@Override
			public void run(){
				Bukkit.broadcastMessage(message);
			}
		}, time);
	}

	public void log(Object message) {
		this.getLogger().fine(message.toString());
	}

	public void warn(Object message) {
		this.getLogger().warning(message.toString());
	}

	public void debug(Object message) {
		if (DEBUG) {
			this.getLogger().info("[DEBUG] " + message.toString());
		}
	}

	public void registerListener(Listener listener) {
		this.getServer().getPluginManager().registerEvents(listener, this);
	}

	public void unregisterListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
}
