package epicapple.rush.game;

import epicapple.rush.*;
import epicapple.rush.nms.NMSHandler;
import epicapple.rush.rushplayer.RushPlayer;
import epicapple.rush.rushplayer.RushPlayerStorage;
import epicapple.rush.util.BlockInfo;
import epicapple.rush.util.ListUtil;
import epicapple.rush.util.LocationUtil;
import epicapple.rush.util.VectorUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.Bed;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GameManager implements Listener {

	private RushPlugin plugin;
	private RushMap map;
	private NMSHandler nmsHandler;
	private StateManager stateManager;
	private TeamManager teamManager;

	private List<Dropspot> dropspots;
	private List<Trader> traders;

	private Location spectatorSpawn;
	private List<UUID> spectators = new ArrayList<>();
	private List<Vector> placedBlocks = new ArrayList<>();

	private boolean ended = false;

	public GameManager(RushPlugin plugin, RushMap map, NMSHandler nmsHandler, StateManager stateManager, TeamManager teamManager, List<Dropspot> dropspots, List<Trader> traders, Location spectatorSpawn){
		this.plugin = plugin;
		this.map = map;
		this.nmsHandler = nmsHandler;
		this.stateManager = stateManager;
		this.teamManager = teamManager;
		this.dropspots = dropspots;
		this.traders = traders;
		this.spectatorSpawn = spectatorSpawn;

		stateManager.setState(GameState.RUNNING);
		plugin.registerListener(this);

		plugin.debug("Game has started");

		removeNotNeededRespawnBlocks();
		//checkForBedBlockBecauseWeArePlayingAFakeOfBedwarsHere();
		closeInventories();
		setSurvivalMode();
		clearInventories();
		teleportPlayersToSpawn();
		startDropspots();
		spawnTraders();
	}

	public void removeNotNeededRespawnBlocks(){
		for(TeamInfo team : teamManager.getTeams()){
			if(!teamManager.isTeamEmpty(team)){
				return;
			}
			for(RespawnBlock respawnBlock : team.getRespawnBlocks()){
				Block b = LocationUtil.fromVector(map.getWorld(), respawnBlock.getLocation()).getBlock();
				b.setType(Material.AIR);
			}
		}
	}

	/*public void checkForBedBlockBecauseWeArePlayingAFakeOfBedwarsHere(){
		for(TeamInfo team : teamManager.getTeams()){
			Block destroyedBlock = LocationUtil.fromVector(map.getWorld(), team.getRespawnBlockLocation()).getBlock();
			if(destroyedBlock.getState() instanceof Bed){
				Block otherBlock;
				Bed bed = (Bed) destroyedBlock.getState();
				if(bed.isHeadOfBed()){
					otherBlock = destroyedBlock.getRelative(bed.getFacing());
				}else{
					otherBlock = destroyedBlock.getRelative(bed.getFacing().getOppositeFace());
				}
				placedBlocks.add(VectorUtil.fromLocation(otherBlock.getLocation()));
			}
		}
	}*/

	public void clearInventories(){
		for(Player p : getPlayingPlayers()){
			RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
			rp.clearInventory();
		}
	}

	public void closeInventories(){
		for(Player p : getPlayingPlayers()){
			p.closeInventory();
		}
	}

	public ArrayList<Player> getPlayingPlayers(){
		ArrayList<Player> players = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()){
			if(!spectators.contains(p.getUniqueId())){
				players.add(p);
			}
		}
		return players;
	}

	private void setSurvivalMode(){
		for(Player p : getPlayingPlayers()){
			p.setGameMode(GameMode.SURVIVAL);
		}
	}

	private void teleportPlayersToSpawn(){
		for(Player p : Bukkit.getOnlinePlayers()){
			teleportPlayerToSpawn(p);
		}
	}

	public void teleportPlayerToSpawn(Player p){
		if(spectators.contains(p.getUniqueId())){
			p.teleport(spectatorSpawn);
		}else{
			TeamInfo team = teamManager.getTeam(p);
			Location spawnLocation = LocationUtil.fromVector(map.getWorld(), team.getSpawn());
			p.teleport(spawnLocation);
		}
	}

	public void spawnTraders(){
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run(){
				for(Trader trader : traders){
					trader.spawn();
				}
			}
		}, 20L);
	}

	public void despawnTraders(){
		for(Trader trader : traders){
			trader.despawn();
		}
	}

	public void startDropspots(){
		for(Dropspot dropspot : dropspots){
			dropspot.start();
		}
	}

	public void broadcastTitle(String message){
		for(Player p : Bukkit.getOnlinePlayers()){
			RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
			rp.sendTitle(message);
		}
	}

	public void broadcastTitle(String title, String subtitle){
		for(Player p : Bukkit.getOnlinePlayers()){
			RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
			rp.sendTitle(title, subtitle);
		}
	}

	//Game Logic
	public boolean hasRespawnBlock(TeamInfo team){
		for(RespawnBlock respawnBlock : team.getRespawnBlocks()){
			Location blockLocation = LocationUtil.fromVector(map.getWorld(), respawnBlock.getLocation());
			Block b = blockLocation.getBlock();
			if(b.getType() == respawnBlock.getMaterial()) return true;
		}
		return false;
	}

	public TeamInfo isRespawnBlock(Location loc){
		for(TeamInfo team : teamManager.getTeams()){
			for(RespawnBlock respawnBlock : team.getRespawnBlocks()){
				if(LocationUtil.equalsVector(loc, respawnBlock.getLocation())){
					return team;
				}
			}
		}
		return null;
	}

	//Possible endings
	public void checkPlayerCount(){
		if(ended){
			return;
		}
		int teamCount = teamManager.getTeams().size();
		int zeroPlayerTeams = 0;
		for(TeamInfo team : teamManager.getTeams()){
			if(teamManager.getPlayerCount(team) == 0){
				zeroPlayerTeams++;
			}
		}
		//If only one team alive
		if(zeroPlayerTeams >= (teamCount - 1)){
			ended = true;
			TeamInfo winnerTeam = null;
			for(TeamInfo team : teamManager.getTeams()){
				if(teamManager.getPlayerCount(team) > 0){
					winnerTeam = team;
					break;
				}
			}
			if(winnerTeam == null){
				restart(0);
				return;
			}else{
				for(Player p : ListUtil.toPlayerList(teamManager.getPlayers(winnerTeam))){
					new Fireworks(plugin, p, winnerTeam.getColor());
				}
			}
			broadcastTitle(Messages.getWonMessage(winnerTeam));
			restart(10);
		}
	}

	public void restart(int delay){
		stateManager.setState(GameState.RESTARTING);
		if(delay <= 0){
			despawnTraders();
			for(RushPlayer rp : RushPlayerStorage.getOnlineRushPlayers()){
				rp.teleportToHub();
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
		}else{
			Bukkit.broadcastMessage(Messages.getEndgameMessage(delay));
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
				@Override
				public void run(){
					despawnTraders();
					for(RushPlayer rp : RushPlayerStorage.getOnlineRushPlayers()){
						rp.teleportToHub();
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				}
			}, delay * 20);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		placedBlocks.add(VectorUtil.fromLocation(e.getBlock().getLocation()));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Location blockLocation = e.getBlock().getLocation();
		TeamInfo team = isRespawnBlock(blockLocation);
		if(team != null){
			TeamInfo playerTeam = teamManager.getTeam(e.getPlayer());
			e.setCancelled(true);
			if(!playerTeam.equals(team)){
				e.getBlock().setType(Material.AIR);
				broadcastTitle(Messages.getBlockDestroyedTitle(team), Messages.getBlockDestroyedSubtitle());
				for(RespawnBlock respawnBlock : team.getRespawnBlocks()){
					Block b = LocationUtil.fromVector(map.getWorld(), respawnBlock.getLocation()).getBlock();
					b.setType(Material.AIR);
				}
			}
			return;
		}
		if(!placedBlocks.contains(VectorUtil.fromLocation(blockLocation))){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		final Player p = e.getEntity();
		TeamInfo team = teamManager.getTeam(p);

		ChatColor playerColor = team.getColor();
		Player killer = p.getKiller();
		ChatColor killerColor = null;
		if(killer != null){
			killerColor = teamManager.getTeam(killer).getColor();
		}

		e.setDeathMessage(Messages.getDeathMessage(p, playerColor, killer, killerColor));

		if(!hasRespawnBlock(team)){
			teamManager.setTeam(p, null);
			spectators.add(p.getUniqueId());
		}
		checkPlayerCount();
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run(){
				nmsHandler.respawnPlayer(p);
			}
		}, 1L);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		final Player p = e.getPlayer();
		if(spectators.contains(p.getUniqueId())){
			e.setRespawnLocation(spectatorSpawn);
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
				@Override
				public void run(){
					RushPlayerStorage.getRushPlayer(p.getUniqueId()).setGameMode(WrapperGameMode.SPECTATOR);
				}
			}, 20L);
		}else{
			TeamInfo team = teamManager.getTeam(p);
			Location spawnLocation = LocationUtil.fromVector(map.getWorld(), team.getSpawn());
			e.setRespawnLocation(spawnLocation);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		spectators.add(p.getUniqueId());
		e.setJoinMessage(Messages.getSpectatorJoinMessage(p));

		RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
		rp.resetStats();
		rp.setGameMode(WrapperGameMode.SPECTATOR);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		e.setQuitMessage(Messages.getLeaveMessage(p));
		if(spectators.contains(p.getUniqueId())){
			spectators.remove(p.getUniqueId());
		}else{
			teamManager.setTeam(p, null);
		}
		RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
		rp.resetStats();
		//Check for end after we removed him from his team!
		checkPlayerCount();
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
			final Player p = (Player) e.getEntity();
			final Player damager = (Player) e.getDamager();

			Vector directionVelocity = damager.getLocation().getDirection().multiply(1.8);
			p.setVelocity(directionVelocity);

			new BlockDestroyer(plugin, nmsHandler, p, 60);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		TeamInfo playerTeam = teamManager.getTeam(p);
		ChatColor color;

		if(playerTeam == null){
			color = ChatColor.GRAY;
		}else{
			color = playerTeam.getColor();
		}

		if(spectators.contains(p.getUniqueId())){
			for(Iterator<Player> iter = getPlayingPlayers().iterator(); iter.hasNext();){
				e.getRecipients().remove(iter.next());
			}
		}else{
			if(!e.getMessage().startsWith("!")){
				List<Player> noRecipients = new ArrayList<>();
				for(Player recipient : e.getRecipients()){
					TeamInfo teamInfo = teamManager.getTeam(recipient);
					if(!teamInfo.equals(playerTeam)){
						noRecipients.add(recipient);
					}
				}
				e.getRecipients().removeAll(noRecipients);
				e.setFormat(color + p.getName() + ChatColor.DARK_GRAY + ChatColor.BOLD + " TEAM >> " + ChatColor.GRAY + e.getMessage());
			}else{
				e.setFormat(color + p.getName() + ChatColor.DARK_GRAY + ChatColor.BOLD + " GLOBAL >> " + ChatColor.GRAY + e.getMessage().substring(1));
			}
		}
	}
}
