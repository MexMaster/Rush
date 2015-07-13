package epicapple.rush.game;

import epicapple.rush.util.VectorUtil;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class RespawnBlock {

	public static RespawnBlock fromString(String serializedRespawnBlock){
		String[] parts = serializedRespawnBlock.split("/");
		if(parts.length == 4){
			Vector vec = VectorUtil.deserialize(serializedRespawnBlock);
			Material material = Material.valueOf(parts[3]);
			if(vec != null && material != null){
				return new RespawnBlock(vec, material);
			}
		}
		return null;
	}

	private Vector location;
	private Material material;

	public RespawnBlock(Vector location, Material material){
		this.location = location;
		this.material = material;
	}

	public Vector getLocation(){
		return location;
	}

	public Material getMaterial(){
		return material;
	}
}
