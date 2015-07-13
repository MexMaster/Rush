package epicapple.rush.rushplayer;

import epicapple.rush.RushPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class NameColorManager implements Listener {

	private Scoreboard scoreboard;
	private HashMap<ChatColor, Team> teams = new HashMap<>();

	public NameColorManager(RushPlugin plugin){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		for(ChatColor color : ChatColor.values()){
			if(color == ChatColor.ITALIC || color == ChatColor.MAGIC || color == ChatColor.RESET || color == ChatColor.STRIKETHROUGH || color == ChatColor.UNDERLINE){
				return;
			}
			Team team = scoreboard.registerNewTeam(color.toString());
			team.setAllowFriendlyFire(false);
			team.setCanSeeFriendlyInvisibles(false);
			team.setPrefix(color + "");
			teams.put(color, team);
		}
		plugin.registerListener(this);
	}


	public void setColor(Player p, ChatColor color){
		for(Team team : teams.values()){
			for(OfflinePlayer player : team.getPlayers()){
				if(p.getUniqueId().equals(player.getUniqueId())){
					team.removePlayer(p);
				}
			}
		}
		Team team = teams.get(color);
		team.addPlayer(p);
	}

	public void removeColor(Player p){
		setColor(p, ChatColor.WHITE);
	}


	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		p.setScoreboard(scoreboard);
	}
}
