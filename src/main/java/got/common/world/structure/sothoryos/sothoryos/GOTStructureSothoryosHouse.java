package got.common.world.structure.sothoryos.sothoryos;

import java.util.Random;

import got.common.database.GOTRegistry;
import got.common.world.structure.other.GOTStructureBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class GOTStructureSothoryosHouse extends GOTStructureBase {
	public Block brickBlock;
	public int brickMeta;
	public Block brickSlabBlock;
	public int brickSlabMeta;
	public Block brickStairBlock;
	public Block brickWallBlock;
	public int brickWallMeta;
	public Block floorBlock;
	public int floorMeta;
	public Block woodBlock;
	public int woodMeta;
	public Block plankBlock;
	public int plankMeta;
	public Block plankSlabBlock;
	public int plankSlabMeta;
	public Block plankStairBlock;
	public Block fenceBlock;
	public int fenceMeta;
	public Block fenceGateBlock;
	public Block doorBlock;
	public Block thatchBlock;
	public int thatchMeta;
	public Block thatchSlabBlock;
	public int thatchSlabMeta;
	public Block thatchStairBlock;
	public Block bedBlock;
	public Block plateBlock;

	public GOTStructureSothoryosHouse(boolean flag) {
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		this.setOriginAndRotation(world, i, j, k, rotation, getOffset());
		setupRandomBlocks(random);
		if (restrictions) {
			int minHeight = 0;
			int maxHeight = 0;
			int range = getOffset();
			for (int i1 = -range; i1 <= range; ++i1) {
				for (int k1 = -range; k1 <= range; ++k1) {
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
					if (maxHeight - minHeight <= 6) {
						continue;
					}
					return false;
				}
			}
		}
		return true;
	}

	public abstract int getOffset();

	public void layFoundation(World world, int i, int k) {
		for (int j = 0; (j == 0 || !isOpaque(world, i, j, k)) && getY(j) >= 0; --j) {
			setBlockAndMetadata(world, i, j, k, brickBlock, brickMeta);
			setGrassToDirt(world, i, j - 1, k);
		}
	}

	public void placeSothoryosFlowerPot(World world, int i, int j, int k, Random random) {
		ItemStack plant = null;
		if (random.nextInt(3) == 0) {
			plant = getRandomFlower(world, random);
		} else {
			int l = random.nextInt(6);
			switch (l) {
			case 0:
				plant = new ItemStack(Blocks.sapling, 1, 3);
				break;
			case 1:
				plant = new ItemStack(GOTRegistry.sapling6, 1, 0);
				break;
			case 2:
				plant = new ItemStack(GOTRegistry.fruitSapling, 1, 3);
				break;
			case 3:
				plant = new ItemStack(Blocks.tallgrass, 1, 2);
				break;
			case 4:
				plant = new ItemStack(Blocks.tallgrass, 1, 1);
				break;
			case 5:
				plant = new ItemStack(GOTRegistry.tallGrass, 1, 5);
				break;
			default:
				break;
			}
		}
		placeFlowerPot(world, i, j, k, plant);
	}

	public void placeSothoryosTorch(World world, int i, int j, int k) {
		setBlockAndMetadata(world, i, j, k, GOTRegistry.sothoryosDoubleTorch, 0);
		setBlockAndMetadata(world, i, j + 1, k, GOTRegistry.sothoryosDoubleTorch, 1);
	}

	@Override
	public void setupRandomBlocks(Random random) {
		if (useStoneBrick()) {
			brickBlock = GOTRegistry.brick4;
			brickMeta = 0;
			brickSlabBlock = GOTRegistry.slabSingle8;
			brickSlabMeta = 0;
			brickStairBlock = GOTRegistry.stairsSothoryosBrick;
			brickWallBlock = GOTRegistry.wallStone4;
			brickWallMeta = 0;
		} else {
			brickBlock = GOTRegistry.brick5;
			brickMeta = 0;
			brickSlabBlock = GOTRegistry.slabSingle9;
			brickSlabMeta = 5;
			brickStairBlock = GOTRegistry.stairsMudBrick;
			brickWallBlock = GOTRegistry.wallStone3;
			brickWallMeta = 8;
		}
		if (random.nextBoolean()) {
			floorBlock = Blocks.stained_hardened_clay;
			floorMeta = 7;
		} else {
			floorBlock = Blocks.stained_hardened_clay;
			floorMeta = 12;
		}
		if (random.nextInt(3) == 0) {
			woodBlock = GOTRegistry.wood6;
			woodMeta = 0;
			plankBlock = GOTRegistry.planks2;
			plankMeta = 8;
			plankSlabBlock = GOTRegistry.woodSlabSingle4;
			plankSlabMeta = 0;
			plankStairBlock = GOTRegistry.stairsMahogany;
			doorBlock = GOTRegistry.doorMahogany;
			fenceBlock = GOTRegistry.fence2;
			fenceMeta = 8;
			fenceGateBlock = GOTRegistry.fenceGateMahogany;
		} else {
			woodBlock = Blocks.log;
			woodMeta = 3;
			plankBlock = Blocks.planks;
			plankMeta = 3;
			plankSlabBlock = Blocks.wooden_slab;
			plankSlabMeta = 3;
			plankStairBlock = Blocks.jungle_stairs;
			doorBlock = GOTRegistry.doorJungle;
			fenceBlock = Blocks.fence;
			fenceMeta = 3;
			fenceGateBlock = GOTRegistry.fenceGateJungle;
		}
		thatchBlock = GOTRegistry.thatch;
		thatchMeta = 1;
		thatchSlabBlock = GOTRegistry.slabSingleThatch;
		thatchSlabMeta = 1;
		thatchStairBlock = GOTRegistry.stairsReed;
		bedBlock = GOTRegistry.strawBed;
		plateBlock = GOTRegistry.woodPlateBlock;
	}

	public boolean useStoneBrick() {
		return false;
	}
}
