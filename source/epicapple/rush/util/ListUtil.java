package epicapple.rush.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class ListUtil {

	public static List<Player> toPlayerList(Collection<UUID> uuids){
		List<Player> playerList = new ArrayList<>();
		for(UUID uuid : uuids){
			playerList.add(Bukkit.getPlayer(uuid));
		}
		return playerList;
	}

	public static List<UUID> toUUIDList(Collection<Player> players){
		List<UUID> uuidList = new ArrayList<>();
		for(Player p : players){
			uuidList.add(p.getUniqueId());
		}
		return uuidList;
	}
}
