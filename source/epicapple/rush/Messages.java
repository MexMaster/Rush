package epicapple.rush;

import epicapple.rush.util.ColorTranslator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {

	public static String getJoinMessage(Player p){
		return ChatColor.RED + p.getName() + ChatColor.GRAY + " ist dem Spiel beigetreten";
	}

	public static String getLeaveMessage(Player p){
		return ChatColor.RED + p.getName() + ChatColor.GRAY + " hat das Spiel verlassen";
	}

	public static String getRestartMessage(){
		return ChatColor.RED + "Der Server startet gerade neu.";
	}

	public static String getRemainingPlayersMessage(int i){
		return ChatColor.DARK_GRAY + "Noch " + i + " Spieler";
	}

	public static String getNotEnoughPlayersMessage(){
		return ChatColor.RED + "Es sind nicht genügend Spieler online";
	}

	public static String getCountdownMessage(){
		return ChatColor.GREEN + "Noch %time% Sekunden";
	}

	public static String getTitleCountdownMessage(){
		return ChatColor.GRAY + "%time% Sekunden";
	}

	public static String getJoinTeamMessage(TeamInfo team){
		return team.getColor() + "Du bist dem Team '" + ColorTranslator.translate(team.getColor()) + "' beigetreten";
	}

	public static String getTeamFullMessage(TeamInfo team){
		return team.getColor() + "Das Team ist voll";
	}

	public static String getTraderName(){
		return ChatColor.GOLD + "Trader";
	}

	public static String getSpectatorJoinMessage(Player p){
		return ChatColor.RED + p.getName() + ChatColor.GRAY + " ist dem Spiel als Spectator beigetreten";
	}

	public static String getWonMessage(TeamInfo team){
		return team.getColor() + "Team " + ColorTranslator.translate(team.getColor()) + " hat das Spiel gewonnen!";
	}

	public static String getEndgameMessage(int i){
		return ChatColor.GRAY + "Der Server wird in " + i + " Sekunden neugestartet";
	}

	public static String getBlockDestroyedTitle(TeamInfo team){
		return ChatColor.RED + "Respawnblock von Team " + team.getColor() + ColorTranslator.translate(team.getColor());
	}

	public static String getBlockDestroyedSubtitle(){
		return ChatColor.GRAY + "wurde zerstört";
	}

	public static String getDeathMessage(Player p, ChatColor playerColor, Player killer, ChatColor killerColor){
		if(killer == null){
			return "" + playerColor + ChatColor.BOLD + p.getName() + ChatColor.GRAY + " ist gestorben";
		}else{
			return "" + playerColor + ChatColor.BOLD + p.getName() + ChatColor.GRAY + " wurde von " + killerColor + ChatColor.BOLD + killer.getName() + ChatColor.GRAY + " getötet";
		}
	}

	//Inventory

	public static String getBlockName(){
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Blöcke";
	}

	public static String getSwordName(){
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Waffen";
	}

	public static String getBowName(){
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Bögen";
	}

	public static String getFoodName(){
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Essen" + ChatColor.WHITE + "/" + ChatColor.AQUA + ChatColor.BOLD + "Tränke";
	}

	public static String getExtraName(){
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Extras";
	}
}
