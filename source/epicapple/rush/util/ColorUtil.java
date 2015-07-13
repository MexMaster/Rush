package epicapple.rush.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

	private static HashMap<DyeColor, ChatColor> colors = new HashMap<>();
	private static HashMap<ChatColor, Vector> rgbValues = new HashMap<>();

	static {
		colors.put(DyeColor.BLACK, ChatColor.DARK_GRAY);
		colors.put(DyeColor.BLUE, ChatColor.DARK_BLUE);
		colors.put(DyeColor.BROWN, ChatColor.GOLD);
		colors.put(DyeColor.CYAN, ChatColor.AQUA);
		colors.put(DyeColor.GRAY, ChatColor.GRAY);
		colors.put(DyeColor.GREEN, ChatColor.DARK_GREEN);
		colors.put(DyeColor.LIGHT_BLUE, ChatColor.BLUE);
		colors.put(DyeColor.LIME, ChatColor.GREEN);
		colors.put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE);
		colors.put(DyeColor.ORANGE, ChatColor.GOLD);
		colors.put(DyeColor.PINK, ChatColor.LIGHT_PURPLE);
		colors.put(DyeColor.PURPLE, ChatColor.DARK_PURPLE);
		colors.put(DyeColor.RED, ChatColor.RED);
		colors.put(DyeColor.SILVER, ChatColor.GRAY);
		colors.put(DyeColor.WHITE, ChatColor.WHITE);
		colors.put(DyeColor.YELLOW, ChatColor.YELLOW);
	}

	static {
		rgbValues.put(ChatColor.BLACK, new Vector(0, 0, 0));
		rgbValues.put(ChatColor.DARK_BLUE, new Vector(0, 0, 170));
		rgbValues.put(ChatColor.DARK_GREEN, new Vector(0, 170, 0));
		rgbValues.put(ChatColor.DARK_AQUA, new Vector(0, 170, 170));
		rgbValues.put(ChatColor.DARK_RED, new Vector(170, 0, 0));
		rgbValues.put(ChatColor.DARK_PURPLE, new Vector(170, 0, 170));
		rgbValues.put(ChatColor.GOLD, new Vector(255, 170, 0));
		rgbValues.put(ChatColor.GRAY, new Vector(153, 153, 153));
		rgbValues.put(ChatColor.DARK_GRAY, new Vector(85, 85, 85));
		rgbValues.put(ChatColor.BLUE, new Vector(85, 85, 255));
		rgbValues.put(ChatColor.GREEN, new Vector(85, 204, 65));
		rgbValues.put(ChatColor.AQUA, new Vector(85, 204, 204));
		rgbValues.put(ChatColor.RED, new Vector(255, 85, 85));
		rgbValues.put(ChatColor.LIGHT_PURPLE, new Vector(255, 85, 255));
		rgbValues.put(ChatColor.YELLOW, new Vector(204, 204, 85));
		rgbValues.put(ChatColor.WHITE, new Vector(170, 170, 170));
	}

	public static DyeColor toDyeColor(ChatColor chatColor){
		for(Map.Entry<DyeColor, ChatColor> entry : colors.entrySet()){
			if(entry.getValue() == chatColor){
				return entry.getKey();
			}
		}
		return null;
	}

	public static ChatColor toChatColor(DyeColor dyeColor){
		for(Map.Entry<DyeColor, ChatColor> entry : colors.entrySet()){
			if(entry.getKey() == dyeColor){
				return entry.getValue();
			}
		}
		return null;
	}

	public static Color toColor(ChatColor color){
		Vector rgb = rgbValues.get(color);
		return Color.fromRGB(rgb.getBlockX(), rgb.getBlockY(), rgb.getBlockZ());
	}
}
