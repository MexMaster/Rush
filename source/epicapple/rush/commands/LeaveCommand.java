package epicapple.rush.commands;

import epicapple.rush.rushplayer.RushPlayer;
import epicapple.rush.rushplayer.RushPlayerStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			return true;
		}
		Player p = (Player) sender;
		RushPlayer rp = RushPlayerStorage.getRushPlayer(p.getUniqueId());
		rp.teleportToHub();
		return true;
	}
}
