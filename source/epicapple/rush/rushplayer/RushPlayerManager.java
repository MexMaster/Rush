package epicapple.rush.rushplayer;

import epicapple.rush.RushPlugin;
import epicapple.rush.game.TeamManager;
import epicapple.rush.nms.NMSHandler;

public class RushPlayerManager {

	private RushPlugin plugin;
	private NameColorManager nameColorManager;
	private TeamManager teamManager;
	private NMSHandler nmsHandler;

	public RushPlayerManager(RushPlugin plugin, TeamManager teamManager, NMSHandler nmsHandler){
		this.plugin = plugin;
		this.teamManager = teamManager;
		this.nameColorManager = new NameColorManager(plugin);
		this.nmsHandler = nmsHandler;
		RushPlayer.setRushPlugin(plugin, this);
	}

	public NameColorManager getNameColorManager(){
		return nameColorManager;
	}

	public TeamManager getTeamManager(){
		return teamManager;
	}

	public NMSHandler getNmsHandler(){
		return nmsHandler;
	}
}
