package epicapple.rush;

import epicapple.rush.util.BlockInfo;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;

public class BlockReplacer {

	private RushPlugin plugin;
	private Random random;
	private List<BlockInfo> blockInfos;

	public BlockReplacer(RushPlugin plugin, List<BlockInfo> blockInfos, int time){
		if(blockInfos.size() < 1){
			return;
		}
		this.plugin = plugin;
		this.blockInfos = blockInfos;
		random = new Random(System.currentTimeMillis());
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run(){
				replace();
			}
		}, time);
	}

	private void replace(){
		int rd = random.nextInt(blockInfos.size());
		BlockInfo info = blockInfos.get(rd);
		info.apply(info.getLocation().getBlock());
		blockInfos.remove(rd);
		if(blockInfos.size() <= 0){
			return;
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run(){
				replace();
			}
		}, random.nextInt(5));
	}
}
