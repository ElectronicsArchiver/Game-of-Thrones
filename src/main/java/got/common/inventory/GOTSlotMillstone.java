package got.common.inventory;

import got.common.GOTLevelData;
import got.common.database.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class GOTSlotMillstone extends Slot {
	public EntityPlayer thePlayer;
	public int itemsTaken;

	public GOTSlotMillstone(EntityPlayer entityplayer, IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
		thePlayer = entityplayer;
	}

	@Override
	public ItemStack decrStackSize(int i) {
		if (getHasStack()) {
			itemsTaken += Math.min(i, getStack().stackSize);
		}
		return super.decrStackSize(i);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public void onCrafting(ItemStack itemstack) {
		itemstack.onCrafting(thePlayer.worldObj, thePlayer, itemsTaken);
		itemsTaken = 0;
		if (!thePlayer.worldObj.isRemote && itemstack.getItem() == GOTRegistry.obsidianShard) {
			GOTLevelData.getData(thePlayer).addAchievement(GOTAchievement.smeltObsidianShard);
		}
	}

	@Override
	public void onCrafting(ItemStack itemstack, int i) {
		itemsTaken += i;
		this.onCrafting(itemstack);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityplayer, ItemStack itemstack) {
		this.onCrafting(itemstack);
		super.onPickupFromSlot(entityplayer, itemstack);
	}
}
