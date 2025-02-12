package got.common.world.structure.essos.common;

import java.util.*;

import got.common.database.GOTRegistry;
import got.common.entity.animal.*;
import got.common.world.structure.other.GOTStructureBase;
import net.minecraft.world.World;

public class GOTStructureEssosBazaar extends GOTStructureEssosBase {
	public GOTStructureEssosBazaar(boolean flag) {
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		this.setOriginAndRotation(world, i, j, k, rotation, 10);
		setupRandomBlocks(random);
		if (restrictions) {
			int minHeight = 0;
			int maxHeight = 0;
			for (int i1 = -13; i1 <= 13; ++i1) {
				for (int k1 = -9; k1 <= 9; ++k1) {
					int j1 = getTopBlock(world, i1, k1) - 1;
					if (!isSurface(world, i1, j1, k1)) {
						return false;
					}
					if (j1 < minHeight) {
						minHeight = j1;
					}
					if (j1 > maxHeight) {
						maxHeight = j1;
					}
					if (maxHeight - minHeight <= 12) {
						continue;
					}
					return false;
				}
			}
		}
		for (int i1 = -13; i1 <= 13; ++i1) {
			for (int k1 = -9; k1 <= 9; ++k1) {
				int j1;
				for (j1 = 1; j1 <= 8; ++j1) {
					setAir(world, i1, j1, k1);
				}
				j1 = -1;
				while (!isOpaque(world, i1, j1, k1) && getY(j1) >= 0) {
					setBlockAndMetadata(world, i1, j1, k1, stoneBlock, stoneMeta);
					setGrassToDirt(world, i1, j1 - 1, k1);
					--j1;
				}
			}
		}
		loadStrScan("essos_bazaar");
		associateBlockMetaAlias("STONE", stoneBlock, stoneMeta);
		associateBlockMetaAlias("BRICK", brickBlock, brickMeta);
		associateBlockMetaAlias("BRICK_SLAB", brickSlabBlock, brickSlabMeta);
		associateBlockMetaAlias("BRICK_SLAB_INV", brickSlabBlock, brickSlabMeta | 8);
		associateBlockAlias("BRICK_STAIR", brickStairBlock);
		associateBlockMetaAlias("PILLAR", pillarBlock, pillarMeta);
		associateBlockMetaAlias("BRICK2", brick2Block, brick2Meta);
		associateBlockMetaAlias("BRICK2_SLAB", brick2SlabBlock, brick2SlabMeta);
		associateBlockMetaAlias("BRICK2_SLAB_INV", brick2SlabBlock, brick2SlabMeta | 8);
		associateBlockMetaAlias("FENCE", fenceBlock, fenceMeta);
		associateBlockAlias("FENCE_GATE", fenceGateBlock);
		associateBlockMetaAlias("BEAM", woodBeamBlock, woodBeamMeta);
		generateStrScan(world, random, 0, 0, 0);
		placeAnimalJar(world, -3, 1, -7, GOTRegistry.butterflyJar, 0, new GOTEntityButterfly(world));
		placeAnimalJar(world, 11, 1, -1, GOTRegistry.birdCageWood, 0, null);
		placeAnimalJar(world, 3, 1, 7, GOTRegistry.birdCage, 0, new GOTEntityBird(world));
		placeAnimalJar(world, -9, 3, 0, GOTRegistry.birdCageWood, 0, new GOTEntityBird(world));
		placeAnimalJar(world, 4, 3, 3, GOTRegistry.birdCageWood, 0, new GOTEntityBird(world));
		ArrayList<Class> stallClasses = new ArrayList<>(Arrays.asList(getStallClasses()));
		while (stallClasses.size() > 6) {
			stallClasses.remove(random.nextInt(stallClasses.size()));
		}
		try {
			GOTStructureBase stall0 = (GOTStructureBase) stallClasses.get(0).getConstructor(Boolean.TYPE).newInstance(notifyChanges);
			GOTStructureBase stall1 = (GOTStructureBase) stallClasses.get(1).getConstructor(Boolean.TYPE).newInstance(notifyChanges);
			GOTStructureBase stall2 = (GOTStructureBase) stallClasses.get(2).getConstructor(Boolean.TYPE).newInstance(notifyChanges);
			GOTStructureBase stall3 = (GOTStructureBase) stallClasses.get(3).getConstructor(Boolean.TYPE).newInstance(notifyChanges);
			GOTStructureBase stall4 = (GOTStructureBase) stallClasses.get(4).getConstructor(Boolean.TYPE).newInstance(notifyChanges);
			GOTStructureBase stall5 = (GOTStructureBase) stallClasses.get(5).getConstructor(Boolean.TYPE).newInstance(notifyChanges);
			generateSubstructure(stall0, world, random, -8, 1, -4, 2);
			generateSubstructure(stall1, world, random, 0, 1, -4, 2);
			generateSubstructure(stall2, world, random, 8, 1, -4, 2);
			generateSubstructure(stall3, world, random, -8, 1, 4, 0);
			generateSubstructure(stall4, world, random, 0, 1, 4, 0);
			generateSubstructure(stall5, world, random, 8, 1, 4, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public Class[] getStallClasses() {
		return null;
	}
}
