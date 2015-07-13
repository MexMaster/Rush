package epicapple.rush.util;

import org.bukkit.ChatColor;

import java.util.HashMap;

public class ColorTranslator {

	private static HashMap<ChatColor, String> chatColorTranslations = new HashMap<>();

	static {
		chatColorTranslations.put(ChatColor.AQUA, "Türkis");
		chatColorTranslations.put(ChatColor.BLACK, "Schwarz");
		chatColorTranslations.put(ChatColor.BLUE, "Blau");
		chatColorTranslations.put(ChatColor.DARK_AQUA, "Dunkeltürkis");
		chatColorTranslations.put(ChatColor.DARK_BLUE, "Dunkelblau");
		chatColorTranslations.put(ChatColor.DARK_GRAY, "Dunkelgrau");
		chatColorTranslations.put(ChatColor.DARK_GREEN, "Dunkelgrün");
		chatColorTranslations.put(ChatColor.DARK_PURPLE, "Dunkelviolett");
		chatColorTranslations.put(ChatColor.DARK_RED, "Dunkelrot");
		chatColorTranslations.put(ChatColor.GOLD, "Gold");
		chatColorTranslations.put(ChatColor.GRAY, "Grau");
		chatColorTranslations.put(ChatColor.GREEN, "Grün");
		chatColorTranslations.put(ChatColor.LIGHT_PURPLE, "Hellviolett");
		chatColorTranslations.put(ChatColor.RED, "Rot");
		chatColorTranslations.put(ChatColor.WHITE, "Weiß");
		chatColorTranslations.put(ChatColor.YELLOW, "Gelb");
	}

	public static String translate(ChatColor color){
		return chatColorTranslations.get(color);
	}
}
