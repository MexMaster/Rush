package epicapple.rush.game;

import epicapple.rush.Messages;
import epicapple.rush.nms.NMSHandler;
import epicapple.rush.nms.VillagerTrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class TraderInventory {

	public static final String MAININVENTORY_NAME = ChatColor.GRAY + "" + ChatColor.BOLD + "TRADER";

	public static final int BLOCK_SLOT = 0;
	public static final int SWORD_SLOT = 2;
	public static final int BOW_SLOT = 4;
	public static final int FOOD_SLOT = 6;
	public static final int EXTRA_SLOT = 8;

	private static Inventory mainInventory = Bukkit.createInventory(null, 9, MAININVENTORY_NAME);

	private static List<VillagerTrade> blockTrades = new ArrayList<>();
	private static List<VillagerTrade> swordTrades = new ArrayList<>();
	private static List<VillagerTrade> bowTrades = new ArrayList<>();
	private static List<VillagerTrade> foodTrades = new ArrayList<>();
	private static List<VillagerTrade> extraTrades = new ArrayList<>();

	static {
		ItemStack item;
		ItemMeta meta;

		item = new ItemStack(Material.QUARTZ_BLOCK, 1);
		meta = item.getItemMeta();
		meta.setDisplayName(Messages.getBlockName());
		item.setItemMeta(meta);
		mainInventory.setItem(BLOCK_SLOT, item);

		item = new ItemStack(Material.IRON_SWORD, 1);
		meta = item.getItemMeta();
		meta.setDisplayName(Messages.getSwordName());
		item.setItemMeta(meta);
		mainInventory.setItem(SWORD_SLOT, item);

		item = new ItemStack(Material.BOW, 1);
		meta = item.getItemMeta();
		meta.setDisplayName(Messages.getBowName());
		item.setItemMeta(meta);
		mainInventory.setItem(BOW_SLOT, item);

		item = new ItemStack(Material.GOLDEN_APPLE, 1);
		meta = item.getItemMeta();
		meta.setDisplayName(Messages.getFoodName());
		item.setItemMeta(meta);
		mainInventory.setItem(FOOD_SLOT, item);

		item = new ItemStack(Material.ENDER_PEARL, 1);
		meta = item.getItemMeta();
		meta.setDisplayName(Messages.getExtraName());
		item.setItemMeta(meta);
		mainInventory.setItem(EXTRA_SLOT, item);

		//TO-DO: Add trades
		ItemStack input;
		ItemStack output;

		Potion potion;

		//Blocks
		input = new ItemStack(Material.GOLD_NUGGET, 4);
		output = new ItemStack(Material.QUARTZ_BLOCK, 8);
		blockTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.GOLD_NUGGET, 16);
		output = new ItemStack(Material.ENDER_STONE, 4);
		blockTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.EMERALD, 1);
		output = new ItemStack(Material.CHEST, 1);
		blockTrades.add(new VillagerTrade(input, output));

		//Weapons
		input = new ItemStack(Material.GOLD_NUGGET, 8);
		output = new ItemStack(Material.WOOD_SWORD, 1);
		output.addEnchantment(Enchantment.DURABILITY, 1);
		swordTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.EMERALD, 6);
		output = new ItemStack(Material.STONE_SWORD, 1);
		output.addEnchantment(Enchantment.DURABILITY, 1);
		swordTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 4);
		output = new ItemStack(Material.IRON_SWORD, 1);
		output.addEnchantment(Enchantment.DURABILITY, 1);
		swordTrades.add(new VillagerTrade(input, output));

		//Bows
		input = new ItemStack(Material.DIAMOND, 3);
		output = new ItemStack(Material.BOW, 1);
		output.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		bowTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 6);
		output = new ItemStack(Material.BOW, 1);
		output.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		output.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		bowTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 9);
		output = new ItemStack(Material.BOW, 1);
		output.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		output.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		bowTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 1);
		output = new ItemStack(Material.ARROW, 1);
		bowTrades.add(new VillagerTrade(input, output));

		//Food
		input = new ItemStack(Material.GOLD_NUGGET, 4);
		output = new ItemStack(Material.APPLE, 4);
		foodTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.GOLD_NUGGET, 8);
		output = new ItemStack(Material.BREAD, 4);
		foodTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 1);
		output = new ItemStack(Material.GOLDEN_APPLE, 1);
		foodTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.EMERALD, 3);
		potion = new Potion(PotionType.SPEED, 2);
		potion.setSplash(true);
		output = potion.toItemStack(1);
		foodTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.EMERALD, 3);
		potion = new Potion(PotionType.SPEED);
		potion.setSplash(true);
		output = potion.toItemStack(1);
		PotionMeta potionMeta = (PotionMeta) output.getItemMeta();
		potionMeta.removeCustomEffect(PotionEffectType.SPEED);
		potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 67, 2), true);
		output.setItemMeta(potionMeta);
		foodTrades.add(new VillagerTrade(input, output));

		//Extras
		input = new ItemStack(Material.EMERALD, 6);
		output = new ItemStack(Material.FLINT_AND_STEEL, 1);
		extraTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.EMERALD, 3);
		output = new ItemStack(Material.FISHING_ROD, 1);
		extraTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 3);
		output = new ItemStack(Material.MONSTER_EGG, 1);
		output.setDurability((short) 50);
		extraTrades.add(new VillagerTrade(input, output));

		input = new ItemStack(Material.DIAMOND, 9);
		output = new ItemStack(Material.ENDER_PEARL, 1);
		extraTrades.add(new VillagerTrade(input, output));
	}

	public static Inventory getMainInventory(){
		return mainInventory;
	}

	public static void openBlockInventory(Player p, NMSHandler nmsHandler){
		nmsHandler.openVillagerInventory(p, blockTrades);
	}

	public static void openSwordInventory(Player p, NMSHandler nmsHandler){
		nmsHandler.openVillagerInventory(p, swordTrades);
	}

	public static void openBowInventory(Player p, NMSHandler nmsHandler){
		nmsHandler.openVillagerInventory(p, bowTrades);
	}

	public static void openFoodInventory(Player p, NMSHandler nmsHandler){
		nmsHandler.openVillagerInventory(p, foodTrades);
	}

	public static void openExtraInventory(Player p, NMSHandler nmsHandler){
		nmsHandler.openVillagerInventory(p, extraTrades);
	}
}
