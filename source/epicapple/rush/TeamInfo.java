package epicapple.rush;

import epicapple.rush.game.RespawnBlock;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import java.util.List;

public class TeamInfo {

	private Vector spawn;
	private List<RespawnBlock> respawnBlocks;
	private ChatColor teamColor;
	private int maxPlayerCount;

	public TeamInfo(Vector spawn, ChatColor teamColor, int playerCount, List<RespawnBlock> respawnBlocks){
		this.spawn = spawn;
		this.respawnBlocks = respawnBlocks;
		this.teamColor = teamColor;
		this.maxPlayerCount = playerCount;
	}

	public int getMaxPlayerCount(){
		return maxPlayerCount;
	}

	public Vector getSpawn(){
		return spawn;
	}

	public List<RespawnBlock> getRespawnBlocks(){
		return respawnBlocks;
	}

	public ChatColor getColor(){
		return teamColor;
	}

	public boolean equals(TeamInfo team){
		return this.getColor() == team.getColor();
	}
}
