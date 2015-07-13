package epicapple.rush;

import epicapple.rush.nms.NMSHandler;
import epicapple.rush.util.BlockInfo;
import epicapple.rush.util.VectorUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BlockDestroyer {

	private static final long UPDATE_TIME = 5;

	private RushPlugin plugin;
	private NMSHandler nmsHandler;
	private UUID uuid;
	private int maxTime;
	private int elapsedTime = 0;

	public BlockDestroyer(RushPlugin plugin, NMSHandler nmsHandler, Player p, int maxTime){
		this.plugin = plugin;
		this.nmsHandler = nmsHandler;
		this.uuid = p.getUniqueId();
		this.maxTime = maxTime;
		destroy();
	}

	private Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}

	private void destroy(){
		Player p = getPlayer();
		if(p == null || VectorUtil.isZero(p.getVelocity(), 0.17)){
			return;
		}

		List<Block> blockList = getBlocks(p);
		if(blockList.size() > 0){
			Location l = blockList.get(0).getLocation();
			l.getWorld().playSound(l, Sound.ZOMBIE_WOODBREAK, 2, 2);
		}
		List<BlockInfo> replaceBlocks = new ArrayList<>(blockList.size());
		for(Block b : blockList){
			replaceBlocks.add(new BlockInfo(nmsHandler, b));
			b.getWorld().playEffect(b.getLocation().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, b.getType().getId());
			b.setType(Material.AIR);
		}
		if(replaceBlocks.size() > 0){
			new BlockReplacer(plugin, replaceBlocks, 45);
		}

		elapsedTime += UPDATE_TIME;
		if(elapsedTime >= maxTime){
			return;
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run(){
				List<Block> blockList = new ArrayList<>();
			}
		}, UPDATE_TIME);
	}

	private static List<Block> getBlocks(Player p){
		Location footLoc = p.getLocation();
		Location eyeLoc = p.getEyeLocation();

		List<Block> blockList = new ArrayList<>();

		blockList.add(footLoc.clone().add(1, 0, 0).getBlock());
		blockList.add(footLoc.clone().add(-1, 0, 0).getBlock());
		blockList.add(footLoc.clone().add(0, 0, 1).getBlock());
		blockList.add(footLoc.clone().add(0, 0, -1).getBlock());
		blockList.add(footLoc.clone().add(1, 0, 1).getBlock());
		blockList.add(footLoc.clone().add(1, 0, -1).getBlock());
		blockList.add(footLoc.clone().add(-1, 0, 1).getBlock());
		blockList.add(footLoc.clone().add(-1, 0, -1).getBlock());

		blockList.add(eyeLoc.clone().add(1, 0, 0).getBlock());
		blockList.add(eyeLoc.clone().add(-1, 0, 0).getBlock());
		blockList.add(eyeLoc.clone().add(0, 0, 1).getBlock());
		blockList.add(eyeLoc.clone().add(0, 0, -1).getBlock());
		blockList.add(eyeLoc.clone().add(1, 0, 1).getBlock());
		blockList.add(eyeLoc.clone().add(1, 0, -1).getBlock());
		blockList.add(eyeLoc.clone().add(-1, 0, 1).getBlock());
		blockList.add(eyeLoc.clone().add(-1, 0, -1).getBlock());

		for(Iterator<Block> iter = blockList.iterator(); iter.hasNext();){
			Block b = iter.next();
			if(b.getType() == Material.AIR){
				iter.remove();
			}
		}

		return blockList;
	}
}
