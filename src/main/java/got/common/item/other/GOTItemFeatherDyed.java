package got.common.item.other;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class GOTItemFeatherDyed extends Item {
	public GOTItemFeatherDyed() {
		setMaxStackSize(1);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		return GOTItemFeatherDyed.getFeatherColor(itemstack);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int i) {
		return Items.feather.getIconFromDamage(i);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
	}

	public static int getFeatherColor(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("FeatherColor")) {
			return itemstack.getTagCompound().getInteger("FeatherColor");
		}
		return 16777215;
	}

	public static boolean isFeatherDyed(ItemStack itemstack) {
		return GOTItemFeatherDyed.getFeatherColor(itemstack) != 16777215;
	}

	public static void removeFeatherDye(ItemStack itemstack) {
		GOTItemFeatherDyed.setFeatherColor(itemstack, 16777215);
	}

	public static void setFeatherColor(ItemStack itemstack, int i) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setInteger("FeatherColor", i);
	}
}
