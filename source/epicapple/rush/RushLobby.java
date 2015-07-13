package epicapple.rush;

import org.bukkit.Location;

public class RushLobby {

	private Location spawn;

	public RushLobby(Location spawn){
		this.spawn = spawn;
		spawn.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
	}

	public Location getSpawn(){
		return spawn;
	}
}
