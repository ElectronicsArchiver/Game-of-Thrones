package got.common.world.structure.essos.lhazar;

import java.util.Random;

import got.common.database.GOTRegistry;
import got.common.entity.essos.ghiscar.GOTEntityGhiscarSlave;
import got.common.entity.essos.lhazar.GOTEntityLhazarFarmer;
import net.minecraft.block.Block;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class GOTStructureLhazarFarm extends GOTStructureLhazarBase {
	public Block crop1Block;
	public Item seed1;
	public Block crop2Block;
	public Item seed2;

	public GOTStructureLhazarFarm(boolean flag) {
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		int j1;
		this.setOriginAndRotation(world, i, j, k, rotation, 6);
		setupRandomBlocks(random);
		if (restrictions) {
			int minHeight = 0;
			int maxHeight = 0;
			for (int i1 = -5; i1 <= 5; ++i1) {
				for (int k1 = -5; k1 <= 5; ++k1) {
					j1 = getTopBlock(world, i1, k1) - 1;
					if (!isSurface(world, i1, j1, k1)) {
						return false;
					}
					if (j1 < minHeight) {
						minHeight = j1;
					}
					if (j1 > maxHeight) {
						maxHeight = j1;
					}
					if (maxHeight - minHeight <= 6) {
						continue;
					}
					return false;
				}
			}
		}
		for (int i1 = -5; i1 <= 5; ++i1) {
			for (int k1 = -5; k1 <= 5; ++k1) {
				int i2 = Math.abs(i1);
				int k2 = Math.abs(k1);
				if (i2 == 5 && k2 == 5) {
					continue;
				}
				for (j1 = 1; j1 <= 6; ++j1) {
					setAir(world, i1, j1, k1);
				}
			}
		}
		loadStrScan("lhazar_farm");
		associateBlockMetaAlias("WOOD", woodBlock, woodMeta);
		associateBlockMetaAlias("FENCE", fenceBlock, fenceMeta);
		associateBlockAlias("FENCE_GATE", fenceGateBlock);
		associateBlockMetaAlias("CROP1", crop1Block, 7);
		associateBlockMetaAlias("CROP2", crop2Block, 7);
		associateBlockMetaAlias("FLAG", flagBlock, flagMeta);
		associateBlockMetaAlias("BONE", boneBlock, boneMeta);
		generateStrScan(world, random, 0, 0, 0);
		if (random.nextInt(4) == 0) {
			GOTEntityLhazarFarmer farmer = new GOTEntityLhazarFarmer(world);
			spawnNPCAndSetHome(farmer, world, 0, 1, -1, 8);
		}
		GOTEntityGhiscarSlave farmhand1 = new GOTEntityGhiscarSlave(world);
		farmhand1.seedsItem = seed1;
		spawnNPCAndSetHome(farmhand1, world, -2, 1, 0, 8);
		GOTEntityGhiscarSlave farmhand2 = new GOTEntityGhiscarSlave(world);
		farmhand2.seedsItem = seed2;
		spawnNPCAndSetHome(farmhand2, world, 2, 1, 0, 8);
		return true;
	}

	@Override
	public void setupRandomBlocks(Random random) {
		int randomCrop;
		super.setupRandomBlocks(random);
		if (random.nextBoolean()) {
			crop1Block = Blocks.wheat;
			seed1 = Items.wheat_seeds;
		} else {
			randomCrop = random.nextInt(4);
			switch (randomCrop) {
			case 0:
				crop1Block = Blocks.carrots;
				seed1 = Items.carrot;
				break;
			case 1:
				crop1Block = Blocks.potatoes;
				seed1 = Items.potato;
				break;
			case 2:
				crop1Block = GOTRegistry.lettuceCrop;
				seed1 = GOTRegistry.lettuce;
				break;
			case 3:
				crop1Block = GOTRegistry.turnipCrop;
				seed1 = GOTRegistry.turnip;
				break;
			default:
				break;
			}
		}
		if (random.nextBoolean()) {
			crop2Block = Blocks.wheat;
			seed2 = Items.wheat_seeds;
		} else {
			randomCrop = random.nextInt(4);
			switch (randomCrop) {
			case 0:
				crop2Block = Blocks.carrots;
				seed2 = Items.carrot;
				break;
			case 1:
				crop2Block = Blocks.potatoes;
				seed2 = Items.potato;
				break;
			case 2:
				crop2Block = GOTRegistry.lettuceCrop;
				seed2 = GOTRegistry.lettuce;
				break;
			case 3:
				crop2Block = GOTRegistry.turnipCrop;
				seed2 = GOTRegistry.turnip;
				break;
			default:
				break;
			}
		}
	}
}
