package epicapple.rush.nms;

import org.bukkit.inventory.ItemStack;

public class VillagerTrade {

	private ItemStack cost1;
	private ItemStack cost2;
	private ItemStack output;

	public VillagerTrade(ItemStack cost1, ItemStack cost2, ItemStack output){
		this.cost1 = cost1;
		this.cost2 = cost2;
		this.output = output;
	}

	public VillagerTrade(ItemStack cost, ItemStack output){
		this(cost, null, output);
	}

	public boolean hasTwoCosts(){
		return cost2 != null;
	}

	public ItemStack getCost1(){
		return cost1;
	}

	public ItemStack getCost2(){
		return cost2;
	}

	public ItemStack getOutput(){
		return output;
	}
}
