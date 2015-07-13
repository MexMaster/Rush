package epicapple.rush.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {

	public static String serialize(Location loc){
		return loc.getWorld().getName() + "/" + loc.getX() + "/" + loc.getY() + "/" + loc.getZ();
	}

	public static Location deserialize(String s){
		String[] parts = s.split("/");
		World world = Bukkit.getWorld(parts[0]);
		double x = Double.valueOf(parts[1]);
		double y = Double.valueOf(parts[2]);
		double z = Double.valueOf(parts[3]);
		float yaw = -1;
		float pitch = -1;
		Location loc = new Location(world, x, y, z);
		if(parts.length > 4){
			yaw = Float.valueOf(parts[4]);
			pitch = Float.valueOf(parts[5]);
			loc = new Location(world, x, y, z, yaw, pitch);
		}
		return loc;
	}

	public static Location fromVector(World w, Vector vec){
		return new Location(w, vec.getX(), vec.getY(), vec.getZ());
	}

	public static boolean equalsVector(Location l, Vector v){
		return l.getX() == v.getX() && l.getY() == v.getY() && l.getZ() == v.getZ();
	}

	public static boolean equalsLocation(Location l1, Location l2){
		return l1.getWorld().getUID().equals(l2.getWorld().getUID()) && l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ();
	}

	//Do this instead of creating an explosion!
	@Deprecated
	public static List<Block> getBlocksInRadius(Location l, int radius){
		World w = l.getWorld();
		int xCoord = l.getBlockX();
		int yCoord = l.getBlockY();
		int zCoord = l.getBlockZ();

		List<Block> tempList = new ArrayList<>();
		for (int x = -radius; x <= radius; x++){
			for (int z = -radius; z <= radius; z++){
				for (int y = -radius; y <= radius; y++){
					tempList.add(new Location(w, xCoord + x, yCoord + y, zCoord + z).getBlock());
				}
			}
		}
		return tempList;
	}

	@Deprecated
	public static List<Block> getYBlocksAround(Block b, int radius){
		World w = b.getWorld();

		return null;
	}
 }
