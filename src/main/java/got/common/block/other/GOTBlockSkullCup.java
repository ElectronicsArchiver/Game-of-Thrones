package got.common.block.other;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class GOTBlockSkullCup extends GOTBlockMug {
	public GOTBlockSkullCup() {
		super(4.0f, 10.0f);
		setStepSound(Block.soundTypeStone);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return Blocks.skull.getIcon(i, 0);
	}
}
