package got.common.block.pillar;

import java.util.List;

import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;

public class GOTBlockPillar2 extends GOTBlockPillarBase {
	public GOTBlockPillar2() {
		setPillarNames("labradorite", "chalk", "stone", "brick", "stone", "stone", "stone", "stone", "yiti", "yitiRed", "stone", "sothoryosGold", "sothoryosObsidian", "stone", "stone");
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 8));
		list.add(new ItemStack(item, 1, 9));
		list.add(new ItemStack(item, 1, 11));
		list.add(new ItemStack(item, 1, 12));
	}
}
