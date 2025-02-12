package got.common.item.other;

import java.util.List;

import cpw.mods.fml.relauncher.*;
import got.common.database.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class GOTItemRobes extends GOTItemArmor {
	public static int ROBES_WHITE = 16777215;

	public GOTItemRobes(ArmorMaterial material, int slot) {
		super(material, slot);
		setCreativeTab(GOTCreativeTabs.tabMisc);
	}

	public GOTItemRobes(int slot) {
		this(GOTMaterial.ROBES, slot);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		if (GOTItemRobes.areRobesDyed(itemstack)) {
			list.add(StatCollector.translateToLocal("item.got.robes.dyed"));
		}
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		return GOTItemRobes.getRobesColor(itemstack);
	}

	public static boolean areRobesDyed(ItemStack itemstack) {
		return GOTItemRobes.getSavedDyeColor(itemstack) != -1;
	}

	public static int getRobesColor(ItemStack itemstack) {
		int dye = GOTItemRobes.getSavedDyeColor(itemstack);
		if (dye != -1) {
			return dye;
		}
		return ROBES_WHITE;
	}

	public static int getSavedDyeColor(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("RobesColor")) {
			return itemstack.getTagCompound().getInteger("RobesColor");
		}
		return -1;
	}

	public static void removeRobeDye(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			itemstack.getTagCompound().removeTag("RobesColor");
		}
	}

	public static void setRobesColor(ItemStack itemstack, int i) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setInteger("RobesColor", i);
	}
}
