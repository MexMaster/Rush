package epicapple.rush.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class VectorUtil {

	public static String serialize(Vector vec){
		return vec.getX() + "/" + vec.getY() + "/" + vec.getZ();
	}

	public static Vector deserialize(String s){
		String[] parts = s.split("/");
		double x = Double.valueOf(parts[0]);
		double y = Double.valueOf(parts[1]);
		double z = Double.valueOf(parts[2]);
		return new Vector(x, y, z);
	}

	public static Vector fromLocation(Location l){
		return new Vector(l.getX(), l.getY(), l.getZ());
	}

	public static boolean isZero(Vector vector, double offset){
		double x = vector.getX(), y = vector.getY(), z = vector.getZ();
		if(!(x < offset && x > -offset)){
			return false;
		}
		if(!(y < offset && y > -offset)){
			return false;
		}
		if(!(z < offset && z > -offset)){
			return false;
		}
		return true;
	}

	public static boolean isZero(Vector vector){
		return isZero(vector, (short) 0);
	}
}
