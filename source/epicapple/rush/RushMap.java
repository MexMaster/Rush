package epicapple.rush;

import org.bukkit.World;

import java.util.ArrayList;

public class RushMap {

	private String name;
	private World w;
	private ArrayList<TeamInfo> teams;
	private int minPlayerCount;

	public RushMap(String name, World w, ArrayList<TeamInfo> teams, int minPlayerCount){
		this.w = w;
		this.teams = teams;
		this.minPlayerCount = minPlayerCount;
	}

	public World getWorld(){
		return w;
	}

	public int getTeamCount(){
		return teams.size();
	}

	public ArrayList<TeamInfo> getTeams(){
		return teams;
	}

	public int getMaxPlayerCount(){
		int i = 0;
		for (TeamInfo ti : teams) {
			i += ti.getMaxPlayerCount();
		}
		return i;
	}

	public int getMinPlayerCount(){
		return minPlayerCount;
	}

	public String getName(){
		return name;
	}
}
