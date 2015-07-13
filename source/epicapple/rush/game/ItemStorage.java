package epicapple.rush.game;

import epicapple.rush.TeamInfo;
import epicapple.rush.util.ColorTranslator;
import epicapple.rush.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemStorage {

	public static final String TEAMS_ITEM_NAME = ChatColor.AQUA + "" + ChatColor.BOLD + "Team";
	public static final String HUB_ITEM_NAME = ChatColor.RED + "" + ChatColor.BOLD + "Zurück zum Hub";
	public static final String TEAMS_INVENTORY_TITLE = ChatColor.GRAY + "Wähle dein Team";

	public static HashMap<Integer, ItemStack> getLobbyItems(TeamInfo team){
		ItemMeta meta;

		Wool woolItem;
		if(team == null){
			woolItem = new Wool(DyeColor.WHITE);
		}else{
			DyeColor dyeColor = ColorUtil.toDyeColor(team.getColor());
			woolItem = new Wool(dyeColor);
		}

		ItemStack teamsItem = woolItem.toItemStack(1);
		meta = teamsItem.getItemMeta();
		meta.setDisplayName(TEAMS_ITEM_NAME);
		teamsItem.setItemMeta(meta);

		ItemStack hubItem = new ItemStack(Material.ENDER_PEARL, 1);
		meta = hubItem.getItemMeta();
		meta.setDisplayName(HUB_ITEM_NAME);
		hubItem.setItemMeta(meta);

		HashMap<Integer, ItemStack> items = new HashMap<>();

		items.put(1, teamsItem);
		items.put(8, hubItem);

		return items;
	}

	public static Inventory getTeamMenu(ChatColor teamColor, List<TeamInfo> teams){
		DyeColor woolColor = ColorUtil.toDyeColor(teamColor);

		int neededInvSize = teams.size() + 3 + 18;
		int invSize = 9;
		while(invSize <= neededInvSize){
			invSize += 9;
		}

		Inventory inv = Bukkit.createInventory(null, invSize, TEAMS_INVENTORY_TITLE);

		Wool wool = new Wool(woolColor);
		ItemStack yourTeamItem = wool.toItemStack(1);
		ItemMeta meta = yourTeamItem.getItemMeta();
		meta.setDisplayName(teamColor + "Dein Team");
		meta.setLore(Arrays.asList("" + ChatColor.GRAY + ChatColor.BOLD + ColorTranslator.translate(teamColor)));
		yourTeamItem.setItemMeta(meta);

		inv.setItem(4, yourTeamItem);

		int currentSlot = 18;
		for(TeamInfo team : teams){
			DyeColor color = ColorUtil.toDyeColor(team.getColor());
			if(color == null){
				System.out.println("Color could not be transformed: " + team.getColor().name());
			}
			Wool teamWool = new Wool(color);
			ItemStack teamItem = teamWool.toItemStack(1);
			setTeamItemName(team.getColor(), teamItem);
			inv.setItem(currentSlot, teamItem);
			currentSlot++;
		}

		return inv;
	}

	private static void setTeamItemName(ChatColor teamColor, ItemStack is){
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Team " + teamColor + ChatColor.BOLD + ColorTranslator.translate(teamColor));
		meta.setLore(Arrays.asList(ChatColor.GRAY + "*Klick*"));
		is.setItemMeta(meta);
	}
}
