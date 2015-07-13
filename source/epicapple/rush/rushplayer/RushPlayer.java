package epicapple.rush.rushplayer;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import epicapple.rush.RushPlugin;
import epicapple.rush.TeamInfo;
import epicapple.rush.WrapperGameMode;
import epicapple.rush.game.ItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

public class RushPlayer {

	private static final String HUBSERVER_NAME = "hub01";

	private static RushPlugin rushPlugin;
	private static RushPlayerManager rushPlayerManager;

	public static void setRushPlugin(RushPlugin plugin, RushPlayerManager playerManager){
		rushPlugin = plugin;
		rushPlayerManager = playerManager;
	}

	private UUID bukkitPlayerUUID;
	private ChatColor nameColor;

	public RushPlayer(Player bukkitPlayer){
		this.bukkitPlayerUUID = bukkitPlayer.getUniqueId();
	}

	public Player toBukkitPlayer(){
		return Bukkit.getPlayer(bukkitPlayerUUID);
	}

	public UUID getUUID(){
		return bukkitPlayerUUID;
	}

	public String getName(){
		return toBukkitPlayer().getName();
	}

	public void setNameColor(ChatColor color){
		this.nameColor = color;
		rushPlayerManager.getNameColorManager().setColor(toBukkitPlayer(), color);
		toBukkitPlayer().setPlayerListName(color + getName());
	}

	public ChatColor getNameColor(){
		return nameColor;
	}

	public void setLobbyItems(){
		clearInventory();
		Player p = toBukkitPlayer();
		for(Map.Entry<Integer, ItemStack> entry : ItemStorage.getLobbyItems(rushPlayerManager.getTeamManager().getTeam(toBukkitPlayer())).entrySet()){
			p.getInventory().setItem(entry.getKey(), entry.getValue());
		}
	}

	public void clearInventory(){
		clearInventory(true);
	}

	public void clearInventory(boolean clearArmor){
		Player p = toBukkitPlayer();
		PlayerInventory inv = p.getInventory();
		inv.clear();
		if(clearArmor){
			inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
		}
	}

	public void resetStats(){
		Player p = toBukkitPlayer();
		p.setExp(0);
		p.setFoodLevel(20);
		p.setHealth(p.getMaxHealth());
		clearInventory();
	}

	public void scheduledUpdateInventory(long time){
		rushPlugin.getServer().getScheduler().runTaskLater(rushPlugin, new Runnable(){
			@Override
			public void run() {
				toBukkitPlayer().updateInventory();
			}
		}, time);
	}

	public void scheduledUpdateInventory(){
		scheduledUpdateInventory(20L);
	}

	public void openTeamsMenu(){
		Player p = toBukkitPlayer();
		TeamInfo team = rushPlayerManager.getTeamManager().getTeam(p);
		Inventory inv;
		if(team != null){
			inv = ItemStorage.getTeamMenu(team.getColor(), rushPlayerManager.getTeamManager().getTeams());
		}else{
			inv = ItemStorage.getTeamMenu(ChatColor.WHITE, rushPlayerManager.getTeamManager().getTeams());
		}
		p.openInventory(inv);
	}

	public void teleportToHub(){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(HUBSERVER_NAME);
		toBukkitPlayer().sendPluginMessage(rushPlugin, "BungeeCord", out.toByteArray());
	}

	public void setGameMode(WrapperGameMode mode){
		Player p = toBukkitPlayer();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode " + mode.getId() + " " + p.getName());
	}

	public void sendTitle(String message){
		rushPlayerManager.getNmsHandler().sendTitle(toBukkitPlayer(), message, "");
	}

	public void sendTitle(String title, String subtitle){
		rushPlayerManager.getNmsHandler().sendTitle(toBukkitPlayer(), title, subtitle);
	}

	public void sendHotbarTitle(String message){
		rushPlayerManager.getNmsHandler().sendHotbarTitle(toBukkitPlayer(), message);
	}
}
