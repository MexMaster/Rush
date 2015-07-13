package epicapple.rush.game;

import epicapple.rush.RushPlugin;
import epicapple.rush.TeamInfo;
import epicapple.rush.rushplayer.RushPlayer;
import epicapple.rush.rushplayer.RushPlayerStorage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class TeamManager implements Listener {

	private Random random = new Random(System.currentTimeMillis());

	private ArrayList<TeamInfo> teams = new ArrayList<>();
	private HashMap<UUID, TeamInfo> players = new HashMap<>();

	public TeamManager(RushPlugin plugin){
		plugin.registerListener(this);
	}

	public void registerTeam(TeamInfo team){
		if(!teams.contains(team)){
			teams.add(team);
		}
	}

	public void unregisterTeam(TeamInfo team){
		teams.remove(team);
		ArrayList<UUID> needRemove = new ArrayList<>();
		for(Map.Entry<UUID, TeamInfo> playerEntry : players.entrySet()){
			if(playerEntry.getValue().equals(team)){
				needRemove.add(playerEntry.getKey());
			}
		}
		for(UUID uuid : needRemove){
			players.remove(uuid);
		}
	}

	public ArrayList<TeamInfo> getTeams(){
		ArrayList<TeamInfo> clone = new ArrayList<>();
		for(TeamInfo team : teams){
			clone.add(team);
		}
		return clone;
	}

	public boolean setTeam(Player p, TeamInfo team){
		UUID uuid = p.getUniqueId();
		RushPlayer rp = RushPlayerStorage.getRushPlayer(uuid);

		if(team == null){
			players.remove(p.getUniqueId());
			rp.setNameColor(ChatColor.WHITE);
			return true;
		}

		int playerCount = getPlayerCount(team);
		int maxPlayerCount = team.getMaxPlayerCount();

		if(playerCount >= maxPlayerCount){
			return false;
		}

		if (players.containsKey(uuid)) {
			players.remove(uuid);
		}
		players.put(uuid, team);
		rp.setNameColor(team.getColor());
		return true;
	}

	public TeamInfo getTeam(Player p){
		return players.get(p.getUniqueId());
	}

	public int getPlayerCount(TeamInfo team){
		int i = 0;
		for(TeamInfo teamInfo : players.values()){
			if(team.equals(teamInfo)){
				i++;
			}
		}
		return i;
	}

	public boolean isTeamFull(TeamInfo team){
		return getPlayerCount(team) >= team.getMaxPlayerCount();
	}

	public boolean isTeamEmpty(TeamInfo team){
		return getPlayerCount(team) == 0;
	}

	public ArrayList<UUID> getPlayers(TeamInfo team){
		ArrayList<UUID> list = new ArrayList<>();
		for(Map.Entry<UUID, TeamInfo> teamEntry : players.entrySet()){
			if(teamEntry.getValue().equals(team)){
				list.add(teamEntry.getKey());
			}
		}
		return list;
	}

	public Set<UUID> getAllPlayers(){
		return players.keySet();
	}

	public void sortInRandom(List<Player> players){
		for(Player p : players){
			sortInRandom(p);
		}
	}

	public void sortInRandom(Player p){
		int maxTeamNumber = teams.size() - 1;
		int teamNumber = random.nextInt(teams.size() - 1);
		TeamInfo team = teams.get(teamNumber);
		int tries = 0;
		while(isTeamFull(team)){
			teamNumber = random.nextInt(maxTeamNumber);
			team = teams.get(teamNumber);
			tries++;
			if(tries > 80){
				for(TeamInfo teamInfo : teams){
					if(!isTeamFull(teamInfo)){
						setTeam(p, teamInfo);
					}
				}
				break;
			}
		}
		setTeam(p, team);
	}

	public TeamInfo getTeamByColor(ChatColor color){
		for(TeamInfo team : teams){
			if(team.getColor() == color){
				return team;
			}
		}
		return null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent e){
		if(players.keySet().contains(e.getPlayer().getUniqueId())){
			setTeam(e.getPlayer(), null);
		}
	}
}
