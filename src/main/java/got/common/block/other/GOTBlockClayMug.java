package got.common.block.other;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class GOTBlockClayMug extends GOTBlockMug {
	public GOTBlockClayMug() {
		setStepSound(Block.soundTypeStone);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return Blocks.hardened_clay.getIcon(i, 0);
	}
}
