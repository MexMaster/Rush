package epicapple.rush.util;

import epicapple.rush.nms.NMSHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public class BlockInfo {

	private NMSHandler nmsHandler;
	private Location loc;
	private Material material;
	private byte data;
	private MaterialData materialData;
	private Object tileEntityData;

	public BlockInfo(NMSHandler nmsHandler, Block block){
		this.nmsHandler = nmsHandler;
		this.loc = block.getLocation();
		this.material = block.getType();
		this.data = block.getData();
		this.materialData = block.getState().getData();
		this.tileEntityData = nmsHandler.getTileEntityNBT(block);
	}

	public void apply(Block block){
		block.setType(material);
		block.setData(data);
		block.getState().setData(materialData);
		if(tileEntityData != null){
			nmsHandler.setTileEntityNBT(block, this.tileEntityData);
		}
		block.getState().update();
	}

	public Location getLocation(){
		return loc;
	}

	public Material getMaterial(){
		return material;
	}

	public byte getData(){
		return data;
	}

	public MaterialData getMaterialData(){
		return materialData;
	}

	public Object getNBTCompound(){
		return tileEntityData;
	}
}
