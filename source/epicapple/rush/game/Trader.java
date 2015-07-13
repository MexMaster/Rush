package epicapple.rush.game;

import epicapple.rush.Messages;
import epicapple.rush.RushPlugin;
import epicapple.rush.nms.NMSHandler;
import epicapple.rush.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Trader implements Listener {

	private RushPlugin plugin;
	private NMSHandler nmsHandler;
	private Location loc;
	private Villager villager;

	public Trader(RushPlugin plugin, NMSHandler nmsHandler, Location loc){
		this.plugin = plugin;
		this.nmsHandler = nmsHandler;
		this.loc = loc;
	}

	public void spawn(){
		villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		villager.setCustomName(Messages.getTraderName());
		villager.setProfession(Villager.Profession.BLACKSMITH);
		nmsHandler.setNoAI(villager);
		plugin.registerListener(this);
	}

	public void despawn(){
		plugin.unregisterListener(this);
		villager.remove();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e){
		if(villager.getEntityId() != e.getEntity().getEntityId()){
			return;
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e){
		if(villager.getEntityId() != e.getRightClicked().getEntityId()){
			return;
		}
		e.setCancelled(true);
		e.getPlayer().openInventory(TraderInventory.getMainInventory());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInventory(InventoryClickEvent e){
		if(!TraderInventory.getMainInventory().getViewers().contains(e.getWhoClicked())){
			return;
		}
		e.setCancelled(true);
		if(!(e.getWhoClicked() instanceof Player)){
			return;
		}
		Player p = (Player) e.getWhoClicked();
		switch(e.getSlot()){
			case TraderInventory.BLOCK_SLOT: TraderInventory.openBlockInventory(p, nmsHandler); break;
			case TraderInventory.SWORD_SLOT: TraderInventory.openSwordInventory(p, nmsHandler); break;
			case TraderInventory.BOW_SLOT: TraderInventory.openBowInventory(p, nmsHandler); break;
			case TraderInventory.FOOD_SLOT: TraderInventory.openFoodInventory(p, nmsHandler); break;
			case TraderInventory.EXTRA_SLOT: TraderInventory.openExtraInventory(p, nmsHandler); break;
			default: break;
		}
	}
}
