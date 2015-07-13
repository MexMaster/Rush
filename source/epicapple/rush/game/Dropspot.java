package epicapple.rush.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Dropspot {

	private Plugin plugin;
	private ItemStack dropItem;
	private Location dropLocation;
	private int tickInterval;
	private boolean rndVelocity;
	private Vector velocity;

	private boolean started = false;
	private BukkitTask task = null;

	public Dropspot(Plugin plugin, ItemStack dropItem, Location dropLocation, int tickInterval){
		this(plugin, dropItem, dropLocation, tickInterval, null);
	}

	public Dropspot(Plugin plugin, ItemStack dropItem, Location dropLocation, int tickInterval, Vector velocity){
		this.plugin = plugin;
		this.dropItem = dropItem;
		this.dropLocation = dropLocation;
		this.tickInterval = tickInterval;
		this.velocity = velocity;
		this.rndVelocity = (velocity == null);
	}

	public void setItem(ItemStack dropItem){
		this.dropItem = dropItem;
	}

	public ItemStack getItem(){
		return dropItem;
	}

	public void setInterval(int tickInterval){
		this.tickInterval = tickInterval;
	}

	public int getInterval(){
		return tickInterval;
	}

	public void start(){
		if(started){
			return;
		}
		started = true;
		drop();
	}

	public void stop(){
		if(!started){
			return;
		}
		started = false;
		if(task != null){
			task.cancel();
			task = null;
		}
	}

	private void drop(){
		if(!started){
			return;
		}
		Item item = dropLocation.getWorld().dropItem(dropLocation, dropItem);
		if(!rndVelocity){
			item.setVelocity(velocity);
		}
		this.task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				drop();
			}
		}, tickInterval);
	}
}
