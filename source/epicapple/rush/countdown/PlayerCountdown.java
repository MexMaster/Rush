package epicapple.rush.countdown;

import epicapple.rush.nms.NMSHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerCountdown {

	private Plugin plugin;
	private NMSHandler nmsHandler;
	private int time;
	private List<UUID> players;
	private List<Integer> broadcastTimes;
	private PlayerCountdownType type;
	private String messageFormat;
	private Runnable execute;

	private boolean started = false;
	private BukkitTask task = null;

	public PlayerCountdown(Plugin plugin, NMSHandler nmsHandler, List<UUID> players, int time, List<Integer> broadcastTimes, PlayerCountdownType type, String messageFormat, Runnable execute){
		this.plugin = plugin;
		this.nmsHandler = nmsHandler;
		this.players = players;
		this.time = time;
		this.broadcastTimes = broadcastTimes;
		this.type = type;
		this.messageFormat = ChatColor.translateAlternateColorCodes('&', messageFormat);
		this.execute = execute;
	}

	public void start(){
		if(started){
			return;
		}
		started = true;
		update();
	}

	public void stop(){
		if(!started){
			return;
		}
		started = false;
		if(task != null){
			task.cancel();
			task = null;
		}
	}

	private void finish(){
		started = false;
		if(task != null){
			task.cancel();
			task = null;
		}
		if(execute != null){
			execute.run();
		}
	}

	public void addPlayer(Player p){
		if(this.players.contains(p.getUniqueId())){
			return;
		}
		this.players.add(p.getUniqueId());
	}

	public void removePlayer(Player p){
		if(!this.players.contains(p.getUniqueId())){
			return;
		}
		this.players.remove(p.getUniqueId());
	}

	public List<Player> getPlayers(){
		List<Player> rtList = new ArrayList<Player>();
		for(UUID uuid : players){
			rtList.add(Bukkit.getPlayer(uuid));
		}
		return rtList;
	}

	public void setTime(int i){
		this.time = i;
	}

	public int getRemainingTime(){
		return time;
	}

	public void setBroadcastTimes(List<Integer> times){
		this.broadcastTimes = times;
	}

	public List<Integer> getBroadcastTimes(){
		return broadcastTimes;
	}

	public void setType(PlayerCountdownType type){
		this.type = type;
	}

	public PlayerCountdownType getType(){
		return type;
	}

	public void setMessageFormat(String messageFormat){
		this.messageFormat = messageFormat;
	}

	public String getMessageFormat(){
		return messageFormat;
	}

	//Logic
	private void update(){
		if(!started){
			return;
		}
		this.time--;
		if(broadcastTimes == null || broadcastTimes.contains(this.time)){
			updateTime();
		}
		if(time <= 0){
			finish();
		}
		this.task = Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable(){
			@Override
			public void run() {
				update();
			}
		}, 20L);
	}

	private void updateTime(){
		List<UUID> unknownPlayers = new ArrayList<>();
		String message = messageFormat.replaceAll("%time%", Integer.toString(this.time));
		for(UUID uuid : players){
			Player p = Bukkit.getPlayer(uuid);
			if(p == null){
				unknownPlayers.add(uuid);
				continue;
			}
			switch(this.type){
				case CHAT: p.sendMessage(message); break;
				case EXPBAR: p.setLevel(this.time); break;
				case ACTIONBAR: nmsHandler.sendHotbarTitle(p, message); break;
				case TITLE: nmsHandler.sendTitle(p, message, ""); break;
			}
		}
		players.remove(unknownPlayers);
	}
}
