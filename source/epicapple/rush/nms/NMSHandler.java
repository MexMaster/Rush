package epicapple.rush.nms;

import epicapple.rush.WrapperGameMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NMSHandler {

	private final String MC_VERSION;

	public NMSHandler() {
		MC_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
	}

	public Class<?> getNMSClass(String name){
		Class<?> clazz;
		try {
			clazz = Class.forName("net.minecraft.server." + MC_VERSION + "." + name);
		} catch (Exception e) {
			clazz = null;
			e.printStackTrace();
		}
		return clazz;
	}

	public Class<?> getOBCClass(String name){
		Class<?> clazz;
		try {
			clazz = Class.forName("org.bukkit.craftbukkit." + MC_VERSION + "." + name);
		} catch (Exception e) {
			clazz = null;
			e.printStackTrace();
		}
		return clazz;
	}

	public Class<?> getClass(String path){
		Class<?> clazz;
		try {
			clazz = Class.forName(path);
		} catch (Exception e) {
			clazz = null;
		}
		return clazz;
	}

	private Object getPlayerHandle(Player p){
		try{
			Class<?> craftPlayerClass = getOBCClass("entity.CraftPlayer");
			Object craftPlayer = craftPlayerClass.cast(p);
			return craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	private void sendPacket(Player p, Object packet){
		try{
			Object nmsPlayer = getPlayerHandle(p);
			Object playerConnection = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
			Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
			sendPacket.invoke(playerConnection, packet);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void sendTitle(Player p, String message, String subtitle){
		try{
			Class<?> iChatBaseComponentClass = getNMSClass("IChatBaseComponent");
			Class<?> chatComponentTextClass = getNMSClass("ChatComponentText");
			Class<?> enumTitleActionClass = getNMSClass("EnumTitleAction");

			Object titleTextComponent = chatComponentTextClass.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', message));
			Object subtitleTextComponent = chatComponentTextClass.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', subtitle));
			Object emptyChatComponent = chatComponentTextClass.getConstructor(String.class).newInstance("");

			Object titleActionTimes = enumTitleActionClass.getDeclaredField("TIMES").get(null);
			Object titleActionSubtitle = enumTitleActionClass.getDeclaredField("SUBTITLE").get(null);
			Object titleActionTitle = enumTitleActionClass.getDeclaredField("TITLE").get(null);

			Class<?> packetPlayOutTitleClass = getNMSClass("PacketPlayOutTitle");
			Constructor<?> packetTitleConstructor = packetPlayOutTitleClass.getConstructor(enumTitleActionClass, iChatBaseComponentClass);

			Object timesPacket = packetTitleConstructor.newInstance(titleActionTimes, emptyChatComponent);
			Object subtitlePacket = packetTitleConstructor.newInstance(titleActionSubtitle, subtitleTextComponent);
			Object titlePacket = packetTitleConstructor.newInstance(titleActionTitle, titleTextComponent);

			sendPacket(p, timesPacket);
			sendPacket(p, subtitlePacket);
			sendPacket(p, titlePacket);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void sendHotbarTitle(Player p, String message){
		try{
			Class<?> iChatBaseComponentClass = getNMSClass("IChatBaseComponent");
			Class<?> chatComponentTextClass = getNMSClass("ChatComponentText");

			Object textComponent = chatComponentTextClass.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', message));

			Class<?> packetPlayOutTitleClass = getNMSClass("PacketPlayOutChat");
			Constructor<?> packetTitleConstructor = packetPlayOutTitleClass.getConstructor(iChatBaseComponentClass, byte.class);

			Object packet = packetTitleConstructor.newInstance(textComponent, (byte) 2);

			sendPacket(p, packet);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void setNoAI(LivingEntity entity){
		try{
			Class<?> craftEntityClass = getOBCClass("entity.CraftEntity");
			Object craftEntity = craftEntityClass.cast(entity);
			Object nmsEntity = craftEntityClass.getMethod("getHandle").invoke(craftEntity);
			Class<?> entityClass = getNMSClass("Entity");
			Object nbttagcompound = entityClass.getMethod("getNBTTag").invoke(nmsEntity);
			Class<?> nbttagcompoundClass = getNMSClass("NBTTagCompound");
			if(nbttagcompound == null){
				nbttagcompound = nbttagcompoundClass.newInstance();
			}
			entityClass.getMethod("c", nbttagcompoundClass).invoke(nmsEntity, nbttagcompound);
			nbttagcompoundClass.getMethod("setInt", String.class, int.class).invoke(nbttagcompound, "NoAI", 1);
			entityClass.getMethod("f", nbttagcompoundClass).invoke(nmsEntity, nbttagcompound);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void setSpectatorMode(Player p){
		/*try{
			Class<?> enumGameModeClass = getNMSClass("EnumGamemode");
			Object spectatorMode = enumGameModeClass.getDeclaredField("SPECTATOR").get(null);

			Object nmsPlayer = getPlayerHandle(p);
			Method setGameMode = nmsPlayer.getClass().getMethod("a", enumGameModeClass);
			setGameMode.invoke(nmsPlayer, spectatorMode);
		}catch(Exception ex){
			ex.printStackTrace();
		}*/
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode 3 " + p.getName());
	}

	public void openVillagerInventory(Player p, List<VillagerTrade> trades){
		try{
			Object nmsVillager = getVillager(p.getWorld());
			clearTrades(nmsVillager);
			for(VillagerTrade trade : trades){
				addTrade(nmsVillager, trade.getCost1(), trade.getOutput());
			}
			Method openTrade = nmsVillager.getClass().getMethod("a", getNMSClass("EntityHuman"));
			openTrade.invoke(nmsVillager, getPlayerHandle(p));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private Object getVillager(World w){
		try{
			Class<?> entityVillagerClass = getNMSClass("EntityVillager");
			Class<?> worldClass = getNMSClass("World");
			Object craftWorld = getOBCClass("CraftWorld").cast(w);
			Object nmsWorld = craftWorld.getClass().getMethod("getHandle").invoke(craftWorld);
			return entityVillagerClass.getConstructor(worldClass).newInstance(nmsWorld);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	private void addTrade(Object nmsvillager, ItemStack input, ItemStack output){
		try {
			Field recipeListField = null;
			for(Field field : nmsvillager.getClass().getDeclaredFields()){
				if(field.getType().toString().equalsIgnoreCase("MerchantRecipeList") && recipeListField == null){
					recipeListField = field;
				}
			}
			if(recipeListField == null){
				recipeListField = nmsvillager.getClass().getDeclaredField("bp");
			}
			recipeListField.setAccessible(true);
			Object recipeList = recipeListField.get(nmsvillager);
			Class<?> craftItemStackClass = getOBCClass("inventory.CraftItemStack");
			Object nmsinput = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, craftItemStackClass.getMethod("asCraftCopy", ItemStack.class).invoke(null, input));
			Object nmsoutput = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, craftItemStackClass.getMethod("asCraftCopy", ItemStack.class).invoke(null, output));
			Class<?> itemStackClass = getNMSClass("ItemStack");
			Object merchantRecipeObject = getNMSClass("MerchantRecipe").getConstructor(itemStackClass, itemStackClass).newInstance(nmsinput, nmsoutput);
			Field usesField = merchantRecipeObject.getClass().getDeclaredField("uses");
			usesField.setAccessible(true);
			usesField.set(merchantRecipeObject, Integer.MIN_VALUE);
			usesField.setAccessible(false);
			((ArrayList)recipeList).add(merchantRecipeObject);
			recipeListField.set(nmsvillager, recipeList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void clearTrades(Object nmsvillager){
		try {
			Field recipeList = null;
			for(Field field : nmsvillager.getClass().getDeclaredFields()){
				if(field.getType().toString().equalsIgnoreCase("MerchantRecipeList") && recipeList == null){
					recipeList = field;
				}
			}
			if(recipeList == null){
				recipeList = nmsvillager.getClass().getDeclaredField("bp");
			}
			recipeList.setAccessible(true);
			Object merchantClearList = getNMSClass("MerchantRecipeList").newInstance();
			recipeList.set(nmsvillager, merchantClearList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void respawnPlayer(Player p){
		try {
			Object obcplayer = getOBCClass("entity.CraftPlayer").cast(p);
			Object nmsplayer = obcplayer.getClass().getMethod("getHandle").invoke(obcplayer);

			Object packet = null;
			for(Object enumtype : getNMSClass("EnumClientCommand").getEnumConstants()){
				if(enumtype.toString().equalsIgnoreCase("PERFORM_RESPAWN")){
					packet = getNMSClass("PacketPlayInClientCommand").getConstructor(getNMSClass("EnumClientCommand")).newInstance(enumtype);
				}
			}
			Object playerConnection = nmsplayer.getClass().getField("playerConnection").get(nmsplayer);
			Method aMethod = playerConnection.getClass().getMethod("a", new Class[]{packet.getClass()});
			aMethod.invoke(playerConnection, new Object[]{packet});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object getWorldHandle(World w){
		try{
			Method getHandle = getOBCClass("CraftWorld").getMethod("getHandle");
			Object craftWorld = getOBCClass("CraftWorld").cast(w);
			Object worldServer = getHandle.invoke(craftWorld);
			return worldServer;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public Object getTileEntity(Block b){
		try{
			Object world = getWorldHandle(b.getWorld());
			Method getTileEntity = null;
			Object tileEntity = null;
			try{
				getTileEntity = world.getClass().getMethod("getTileEntity", getNMSClass("BlockPosition"));
				tileEntity = getTileEntity.invoke(world, getNMSClass("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(b.getX(), b.getY(), b.getZ()));
			}catch(Exception ex){
				getTileEntity = world.getClass().getMethod("getTileEntity", int.class, int.class, int.class);
				tileEntity = getTileEntity.invoke(world, b.getX(), b.getY(), b.getZ());
			}
			return tileEntity;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public Object getTileEntityNBT(Block b){
		try{
			Object tileEntity = getTileEntity(b);
			if(tileEntity == null){
				return null;
			}
			Object nbttag = getNMSClass("NBTTagCompound").newInstance();
			tileEntity.getClass().getMethod("b", getNMSClass("NBTTagCompound")).invoke(tileEntity, nbttag);
			return nbttag;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public void setTileEntityNBT(Block b, Object nbttag){
		try{
			Object tileEntity = getTileEntity(b);
			if(tileEntity == null){
				return;
			}
			tileEntity.getClass().getMethod("a", getNMSClass("NBTTagCompound")).invoke(tileEntity, nbttag);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
