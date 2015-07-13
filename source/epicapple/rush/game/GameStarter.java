package epicapple.rush.game;

import epicapple.rush.*;
import epicapple.rush.config.ConfigHandler;
import epicapple.rush.countdown.PlayerCountdown;
import epicapple.rush.countdown.PlayerCountdownType;
import epicapple.rush.nms.NMSHandler;
import epicapple.rush.rushplayer.RushPlayer;
import epicapple.rush.rushplayer.RushPlayerStorage;
import epicapple.rush.util.ColorUtil;
import epicapple.rush.util.ListUtil;
import epicapple.rush.util.LocationUtil;
import epicapple.rush.util.VectorUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameStarter implements Listener {

	private RushPlugin plugin;
	private NMSHandler nmsHandler;
	private ConfigHandler configHandler;
	private TeamManager teamManager;

	private ArrayList<RushMap> maps = new ArrayList<>();
	private RushMap votedMap;
	private ArrayList<Dropspot> dropspots = new ArrayList<>();
	private ArrayList<Trader> traders = new ArrayList<>();
	private Location spectatorSpawn;
	private StateManager stateManager;
	private RushLobby lobby;

	private PlayerCountdown countdown1 = null;
	private PlayerCountdown countdown2 = null;

	public GameStarter(RushPlugin plugin, NMSHandler nmsHandler, ConfigHandler configHandler, StateManager stateManager, TeamManager teamManager){
		this.plugin = plugin;
		this.nmsHandler = nmsHandler;
		this.configHandler = configHandler;
		this.stateManager = stateManager;
		this.teamManager = teamManager;
		plugin.registerListener(this);
		startup();
	}

	private void startup(){
		//Load maps
		List<String> mapNames = (List<String>) configHandler.getValue("maps");
		if(mapNames == null || mapNames.size() <= 0){
			plugin.warn("No rush maps are configured. Plugin will not work");
			return;
		}
		//HashMap<TeamInfo, Vector> signs = new HashMap<>();
		Location lobbyspawn = LocationUtil.deserialize((String) configHandler.getValue("settings.spawn"));
		for(String name : mapNames){
			plugin.debug("Map '" + name + "' will be loaded!");
			World w = Bukkit.getWorld(name);
			if(w == null){
				continue;
			}
			ArrayList<TeamInfo> teams = new ArrayList<>();
			int counter = 0;
			String teamPath = "mapsettings." + name + ".teams.team" + counter;
			while(configHandler.isSet(teamPath)){
				plugin.debug("Team '" + counter + "' found!");

				Vector spawn = VectorUtil.deserialize((String) configHandler.getValue(teamPath + ".spawn"));
				int playerCount = (int) configHandler.getValue(teamPath + ".playerCount");
				ChatColor teamColor = ChatColor.valueOf((String) configHandler.getValue(teamPath + ".color"));
				List<String> respawnBlocks = (List<String>) configHandler.getValue(teamPath + ".respawnBlocks");
				List<RespawnBlock> respawnBlockVectors = new ArrayList<>();

				for(String s : respawnBlocks){
					respawnBlockVectors.add(RespawnBlock.fromString(s));
				}

				TeamInfo teamInfo = new TeamInfo(spawn, teamColor, playerCount, respawnBlockVectors);
				teams.add(teamInfo);
				teamManager.registerTeam(teamInfo);

				//signs.put(teamInfo, signLocation);

				counter++;
				teamPath = "mapsettings." + name + ".teams.team" + counter;
			}

			int minPlayerCount = (int) configHandler.getValue("mapsettings." + name + ".minplayercount");
			String mapName = (String) configHandler.getValue("mapsettings." + name + ".name");
			mapName = ChatColor.translateAlternateColorCodes('&', mapName);
			RushMap map = new RushMap(mapName, w, teams, minPlayerCount);
			maps.add(map);
			plugin.debug("Map '" + name + "' loaded!");
			//Because fuck tom and cause he wanted no vote system (you just need to put one world in config, the rest will be ignored)
			votedMap = maps.get(0);

			counter = 0;
			String dropspotPath = "mapsettings." + name + ".dropspots.dropspot" + counter;
			while(configHandler.isSet(dropspotPath)){
				Vector location = VectorUtil.deserialize((String) configHandler.getValue(dropspotPath + ".location"));
				int interval = (int) configHandler.getValue(dropspotPath + ".interval");
				Vector velocity = VectorUtil.deserialize((String) configHandler.getValue(dropspotPath + ".velocity"));
				ItemStack item;
				Material type = Material.valueOf((String) configHandler.getValue(dropspotPath + ".item.type"));
				if(type == null){
					plugin.debug("Wrong itemtype was given for dropspot" + counter);
					continue;
				}
				item = new ItemStack(type, 1);
				ItemMeta meta = item.getItemMeta();
				String displayName = ChatColor.translateAlternateColorCodes('&', (String) configHandler.getValue(dropspotPath + ".item.name"));
				List<String> lores = (List<String>) configHandler.getValue(dropspotPath + ".item.lores");
				List<String> coloredLores = new ArrayList<>();
				for(String s : lores){
					coloredLores.add(ChatColor.translateAlternateColorCodes('&', s));
				}
				meta.setDisplayName(displayName);
				meta.setLore(coloredLores);
				item.setItemMeta(meta);

				Dropspot dropspot = new Dropspot(plugin, item, LocationUtil.fromVector(votedMap.getWorld(), location), interval, velocity);
				dropspots.add(dropspot);

				counter++;
				dropspotPath = "mapsettings." + name + ".dropspots.dropspot" + counter;
			}

			counter = 0;
			String traderPath = "mapsettings." + name + ".traders.trader" + counter;
			while(configHandler.isSet(traderPath)){
				Vector vecLoc = VectorUtil.deserialize((String) configHandler.getValue(traderPath));
				Location loc = LocationUtil.fromVector(votedMap.getWorld(), vecLoc);

				Trader trader = new Trader(plugin, nmsHandler, loc);
				traders.add(trader);

				counter++;
				traderPath = "mapsettings." + name + ".traders.trader" + counter;
			}

			Vector specSpawnVec = VectorUtil.deserialize((String) configHandler.getValue("mapsettings." + name + ".spectatorspawn"));
			spectatorSpawn = LocationUtil.fromVector(votedMap.getWorld(), specSpawnVec);
		}
		lobby = new RushLobby(lobbyspawn);
		stateManager.setState(GameState.WAITING);
	}

	public GameState getState(){
		return stateManager.getState();
	}

	private void startCountdown(){
		ArrayList<UUID> playerUUIDs = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()){
			playerUUIDs.add(p.getUniqueId());
		}
		ArrayList<Integer> broadcastTimes = new ArrayList<>();
		broadcastTimes.add(10);
		broadcastTimes.add(5);
		broadcastTimes.add(4);
		broadcastTimes.add(3);
		broadcastTimes.add(2);
		broadcastTimes.add(1);
		broadcastTimes.add(0);

		Runnable runnable = new Runnable() {
			@Override
			public void run(){
				startGame();
			}
		};

		countdown1 = new PlayerCountdown(plugin, nmsHandler, playerUUIDs, 30, null, PlayerCountdownType.ACTIONBAR, Messages.getCountdownMessage(), null);
		countdown2 = new PlayerCountdown(plugin, nmsHandler, playerUUIDs, 30, broadcastTimes, PlayerCountdownType.TITLE, Messages.getTitleCountdownMessage(), runnable);

		countdown1.start();
		countdown2.start();
	}

	private void startGame(){
		ArrayList<Player> unteamedPlayers = new ArrayList<>();
		unteamedPlayers.addAll(Bukkit.getOnlinePlayers());
		unteamedPlayers.removeAll(ListUtil.toPlayerList(teamManager.getAllPlayers()));
		teamManager.sortInRandom(unteamedPlayers);

		plugin.unregisterListener(this);
		GameManager gameManager = new GameManager(plugin, votedMap, nmsHandler, stateManager, teamManager, dropspots, traders, spectatorSpawn);
	}

	//Events

	@EventHandler
	public void callPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		e.setJoinMessage(Messages.getJoinMessage(p));

		Location loc = lobby.getSpawn();
		p.teleport(loc);

		p.setGameMode(GameMode.ADVENTURE);

		RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
		rp.resetStats();
		rp.setLobbyItems();

		int onlinePlayers = Bukkit.getOnlinePlayers().size();
		if(onlinePlayers < votedMap.getMinPlayerCount()){
			String message = Messages.getRemainingPlayersMessage(votedMap.getMinPlayerCount() - onlinePlayers);
			plugin.broadcastDelayedMessage(message, 1L);
		}
		if(onlinePlayers >= votedMap.getMinPlayerCount()){
			GameState state = stateManager.getState();
			if(state == GameState.WAITING){
				stateManager.setState(GameState.COUNTDOWN);
				startCountdown();
				return;
			}
		}

		if(stateManager.getState() == GameState.COUNTDOWN){
			if(countdown1 != null){
				countdown1.addPlayer(p);
			}
			if(countdown2 != null){
				countdown2.addPlayer(p);
			}
		}
	}

	@EventHandler
	public void callPlayerLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		e.setQuitMessage(Messages.getLeaveMessage(p));

		RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
		rp.resetStats();
		teamManager.setTeam(p, null);

		GameState state = stateManager.getState();
		if(state == GameState.COUNTDOWN){
			if(countdown1 != null){
				countdown1.removePlayer(p);
			}
			if(countdown2 != null){
				countdown2.removePlayer(p);
			}

			int onlinePlayers = Bukkit.getOnlinePlayers().size() - 1;
			if(onlinePlayers < votedMap.getMinPlayerCount()){
				countdown1.stop();
				countdown2.stop();
				countdown1 = null;
				countdown2 = null;
				stateManager.setState(GameState.WAITING);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
					@Override
					public void run(){
						Bukkit.broadcastMessage(Messages.getNotEnoughPlayersMessage());
					}
				}, 1L);
			}
		}
	}

	@EventHandler
	public void callPlayerLogin(PlayerLoginEvent e){
		GameState state = stateManager.getState();
		switch(state){
			case ERROR:
			case RESTARTING:
				e.setKickMessage(Messages.getRestartMessage());
				e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				break;
		}
	}


	@EventHandler
	public void callPlayerInteract(PlayerInteractEvent e){
		e.setCancelled(true);
		Action action = e.getAction();
		if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR){
			return;
		}
		if(!e.hasItem()){
			return;
		}
		ItemStack itemStack = e.getItem();
		if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()){
			String displayName = itemStack.getItemMeta().getDisplayName();
			Player p = e.getPlayer();
			RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
			if(displayName.equals(ItemStorage.TEAMS_ITEM_NAME)){
				rp.openTeamsMenu();
			}else if(displayName.equals(ItemStorage.HUB_ITEM_NAME)){
				rp.teleportToHub();
			}
		}
	}

	@EventHandler
	public void callPlayerInventory(InventoryClickEvent e){
		if(!(e.getWhoClicked() instanceof Player)){
			return;
		}
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		UUID uuid = p.getUniqueId();
		RushPlayer rp = RushPlayerStorage.getRushPlayer(uuid);
		rp.scheduledUpdateInventory();

		//Check team inventory etc.
		Inventory inv = e.getInventory();
		if(inv.getTitle().equals(ItemStorage.TEAMS_INVENTORY_TITLE)){
			ItemStack item = e.getCurrentItem();
			int slot = e.getSlot();
			if(item != null && item.getData() instanceof Wool && slot != 4){
				Wool woolData = (Wool) item.getData();
				ChatColor teamColor = ColorUtil.toChatColor(woolData.getColor());
				TeamInfo newTeam = teamManager.getTeamByColor(teamColor);
				if(newTeam == null){
					plugin.debug("A non existent team was clicked ('newTeam' was null)");
					return;
				}
				boolean teamChanged = teamManager.setTeam(p, newTeam);
				if(teamChanged){
					p.closeInventory();
					p.sendMessage(Messages.getJoinTeamMessage(newTeam));
					rp.setLobbyItems();
				}else{
					p.sendMessage(Messages.getTeamFullMessage(newTeam));
				}
			}
		}
	}

	@EventHandler
	public void callPlayerDropItem(PlayerDropItemEvent e){
		e.setCancelled(true);
	}

	@EventHandler
	public void callPlayerFoodLevelChange(FoodLevelChangeEvent e){
		e.setCancelled(true);
	}

	@EventHandler
	public void callPlayerDamage(EntityDamageEvent e){
		e.setCancelled(true);
	}

	@EventHandler
	public void callPlayerMove(PlayerMoveEvent e){
		if(e.getTo().getY() < 0){
			Player p = e.getPlayer();
			p.teleport(lobby.getSpawn());
		}
	}

	@EventHandler
	public void callWeatherChange(WeatherChangeEvent e){
		e.setCancelled(true);
	}

	@EventHandler
	public void callPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		TeamInfo team = teamManager.getTeam(p);
		ChatColor color;
		if(team == null){
			color = ChatColor.GRAY;
		}else{
			color = team.getColor();
		}
		e.setFormat(color + p.getName() + ChatColor.DARK_GRAY + " >> " + ChatColor.GRAY + e.getMessage());
	}
}
