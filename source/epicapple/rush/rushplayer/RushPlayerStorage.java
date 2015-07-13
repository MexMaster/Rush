package epicapple.rush.rushplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class RushPlayerStorage implements Listener {

	private static ArrayList<RushPlayer> rushPlayers = new ArrayList<>();

	private static void registerRushPlayer(Player bukkitPlayer){
		RushPlayer rp = new RushPlayer(bukkitPlayer);
		rushPlayers.add(rp);
	}

	private static void unregisterRushPlayer(Player bukkitPlayer){
		RushPlayer rp = getRushPlayer(bukkitPlayer.getUniqueId());
		if(rp == null){
			return;
		}
		rushPlayers.remove(rp);
	}

	public static ArrayList<RushPlayer> getOnlineRushPlayers(){
		ArrayList<RushPlayer> copy = new ArrayList<>();
		copy.addAll(rushPlayers);
		return copy;
	}

	public static RushPlayer getRushPlayer(UUID uuid){
		for(RushPlayer rp : rushPlayers){
			if(rp.getUUID().equals(uuid)){
				return rp;
			}
		}
		return null;
	}

	public static RushPlayer getRushPlayer(String name){
		for(RushPlayer rp : rushPlayers){
			if(rp.getName().equals(name)){
				return rp;
			}
		}
		return null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e){
		registerRushPlayer(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e){
		unregisterRushPlayer(e.getPlayer());
	}
}
