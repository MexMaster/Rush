package epicapple.rush.game;

import epicapple.rush.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.UUID;

public class Fireworks {

	private Plugin plugin;
	private Random random = new Random(System.currentTimeMillis());
	private UUID player;
	private ChatColor color;

	public Fireworks(Plugin plugin, Player p, ChatColor color){
		this.plugin = plugin;
		this.player = p.getUniqueId();
		this.color = color;
		shoot();
	}

	private Player getPlayer(){
		return Bukkit.getPlayer(player);
	}

	private void shoot(){
		Player p = getPlayer();
		if(p == null){
			return;
		}
		Firework firework = (Firework) p.getWorld().spawnEntity(p.getLocation().add(random.nextDouble() * 3, 2, random.nextDouble() * 3), EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(meta.getPower() + 2);
		meta.clearEffects();
		FireworkEffect effect = FireworkEffect.builder().flicker(true).trail(true).with(FireworkEffect.Type.BALL).withColor(ColorUtil.toColor(color)).build();
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run(){
				shoot();
			}
		}, 13L);
	}
}
