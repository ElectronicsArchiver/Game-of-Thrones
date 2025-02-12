package got.common.block.leaves;

import java.util.*;

import got.common.database.GOTRegistry;
import net.minecraft.item.*;
import net.minecraft.world.World;

public class GOTBlockLeaves8 extends GOTBlockLeavesBase {
	public GOTBlockLeaves8() {
		setLeafNames("plum", "redwood", "pomegranate", "palm");
	}

	@Override
	public void addSpecialLeafDrops(ArrayList drops, World world, int i, int j, int k, int meta, int fortune) {
		if ((meta & 3) == 0 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(GOTRegistry.plum));
		}
		if ((meta & 3) == 2 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(GOTRegistry.pomegranate));
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(GOTRegistry.sapling8);
	}
}
