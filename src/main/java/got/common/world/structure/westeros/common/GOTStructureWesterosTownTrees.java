package got.common.world.structure.westeros.common;

import java.util.Random;

import got.common.world.feature.GOTTreeType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class GOTStructureWesterosTownTrees extends GOTStructureWesterosBase {
	public GOTStructureWesterosTownTrees(boolean flag) {
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		int i1;
		int j1;
		int k1;
		this.setOriginAndRotation(world, i, j, k, rotation, 2);
		setupRandomBlocks(random);
		if (restrictions) {
			for (i1 = -6; i1 <= 6; ++i1) {
				for (k1 = -2; k1 <= 2; ++k1) {
					int j12 = getTopBlock(world, i1, k1) - 1;
					if (isSurface(world, i1, j12, k1)) {
						continue;
					}
					return false;
				}
			}
		}
		for (i1 = -6; i1 <= 6; ++i1) {
			for (k1 = -2; k1 <= 2; ++k1) {
				int i2 = Math.abs(i1);
				int k2 = Math.abs(k1);
				for (j1 = 0; (j1 >= 0 || !isOpaque(world, i1, j1, k1)) && getY(j1) >= 0; --j1) {
					setBlockAndMetadata(world, i1, j1, k1, rockSlabDoubleBlock, rockSlabDoubleMeta);
					setGrassToDirt(world, i1, j1 - 1, k1);
				}
				for (j1 = 1; j1 <= 10; ++j1) {
					setAir(world, i1, j1, k1);
				}
				if (i2 % 4 != 2 && k2 <= 1) {
					setBlockAndMetadata(world, i1, 0, k1, Blocks.grass, 0);
				}
				if (i2 % 4 != 2 || k2 != 2) {
					continue;
				}
				setBlockAndMetadata(world, i1, 1, k1, rockWallBlock, rockWallMeta);
				setBlockAndMetadata(world, i1, 2, k1, Blocks.torch, 5);
			}
		}
		for (int i12 : new int[] { -4, 0, 4 }) {
			WorldGenAbstractTree treeGen;
			j1 = 1;
			int k12 = 0;
			for (int l = 0; l < 16 && ((treeGen = GOTTreeType.CYPRESS.create(notifyChanges, random)) == null || !treeGen.generate(world, random, getX(i12, k12), getY(j1), getZ(i12, k12))); ++l) {
			}
		}
		return true;
	}
}
