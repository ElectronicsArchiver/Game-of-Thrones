package got.common.quest;

import java.util.UUID;

import got.common.util.GOTLog;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IPickpocketable {
	public static class Helper {
		public static String getOwner(ItemStack itemstack) {
			if (itemstack.hasTagCompound()) {
				return itemstack.getTagCompound().getCompoundTag("GOTPickpocket").getString("Owner");
			}
			return null;
		}

		public static String getWanter(ItemStack itemstack) {
			if (itemstack.hasTagCompound()) {
				return itemstack.getTagCompound().getCompoundTag("GOTPickpocket").getString("Wanter");
			}
			return null;
		}

		public static UUID getWanterID(ItemStack itemstack) {
			if (itemstack.hasTagCompound()) {
				String id = itemstack.getTagCompound().getCompoundTag("GOTPickpocket").getString("WanterID");
				try {
					return UUID.fromString(id);
				} catch (IllegalArgumentException e) {
					GOTLog.logger.warn("Item %s has invalid pickpocketed wanter ID %s", itemstack.getDisplayName(), id);
				}
			}
			return null;
		}

		public static boolean isPickpocketed(ItemStack itemstack) {
			return itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("GOTPickpocket");
		}

		public static void setPickpocketData(ItemStack itemstack, String ownerName, String wanterName, UUID wantedID) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("Owner", ownerName);
			data.setString("Wanter", wanterName);
			data.setString("WanterID", wantedID.toString());
			itemstack.setTagInfo("GOTPickpocket", data);
		}
	}

}
